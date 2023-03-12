package com.example.travelapp.model;

import android.graphics.Bitmap;
import android.net.Uri;

import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class FirebaseModel{
    FirebaseFirestore db;
    FirebaseStorage storage;
    FirebaseAuth myAuth;

    FirebaseModel(){
        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(false)
                .build();
        db.setFirestoreSettings(settings);
        storage = FirebaseStorage.getInstance();
        myAuth = FirebaseAuth.getInstance();
    }

    public void getAllPosts(Model.Listener<List<Post>> callback ) {
        db.collection("posts").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<Post> posts = new LinkedList<>();
                if(task.isSuccessful()){
                    QuerySnapshot jsonsList = task.getResult();
                    for(DocumentSnapshot json: jsonsList){
                        Post post = Post.fromJson(json.getData());
                        posts.add(post);
                    }
                }
                callback.onComplete(posts);
            }
        });
    }

    // for cache
    public void getAllPostsSince(Long since, Model.Listener<List<Post>> callback){
        db.collection("posts")
                .whereGreaterThanOrEqualTo(Post.LAST_UPDATED, new Timestamp(since,0))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<Post> list = new LinkedList<>();
                        if (task.isSuccessful()){
                            QuerySnapshot jsonsList = task.getResult();
                            for(DocumentSnapshot json: jsonsList){
                                Post post = Post.fromJson(json.getData());
                                list.add(post);
                            }
                        }
                        callback.onComplete(list);
                    }
                });
    }

    public void addPost(Post post, Model.Listener<Void> listener) { // add post to firebase
        db.collection("posts").document(post.getId()).set(post.toJson()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                listener.onComplete(null);
            }
        });
    }

    public void addUser(User us, Model.Listener<Void> listener) {  // add user to firebase
        db.collection("users").document(us.getEmail()).set(us.toJson()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                listener.onComplete(null);
            }
        });
    }

    public void createAccount(String email, String password, Model.Listener<Boolean> listener){
        myAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                FirebaseUser user = myAuth.getCurrentUser();
                listener.onComplete(task.isSuccessful());

            }
        });
    }

    public void login(String email, String password,Model.Listener<Boolean> listener){ // login user
        if(!email.isEmpty() && !password.isEmpty()) {
            myAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            FirebaseUser user = myAuth.getCurrentUser();
                            listener.onComplete(task.isSuccessful());
                        }
                    });
        }
    }

    public void isSignedIn(Model.Listener<Boolean> listener){ // check if the user connected to app
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            listener.onComplete(true);
        } else {
            listener.onComplete(false);
        }
    }

    public void logOut(){
        FirebaseAuth.getInstance().signOut();
    }

    public void uploadImage(String name, Bitmap bitmap, Model.Listener<String> listener){
        StorageReference storageRef = storage.getReference();
        StorageReference imagesRef = storageRef.child("images/" + name + ".jpg");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = imagesRef.putBytes(data);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                listener.onComplete(null);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        listener.onComplete(uri.toString());
                    }
                });

            }
        });
    }

    public void updateUser(User us , Model.Listener<Boolean> listener){
        db.collection("users").document(us.getEmail()).set(us.toJson()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                listener.onComplete(null);
            }
        });
    }

    public void getCurrentUser(Model.Listener<User> listener){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        db.collection("users").document(user.getEmail()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                User us = null;
                if(task.isSuccessful()){
                    DocumentSnapshot json = task.getResult();
                    if(json !=null) {
                        us = User.fromJson(json.getData());
                    }
                }
                listener.onComplete(us);
            }
        });
    }

    public void saveStar(String namePost){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        db.collection("stars").document(user.getEmail()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot json = task.getResult();
                List<String> posts = new ArrayList<>();

                if(!json.contains("post")){
                    posts.add(namePost);
                    Map<String ,List<String>> p = new HashMap<>();
                    p.put("post",posts);
                    db.collection("stars").document(user.getEmail()).set(p);
                }
                else {
                    posts.addAll((List<String>) json.get("post"));
                    if (!posts.contains(namePost)) {
                        posts.add(namePost);
                        Map<String, Object> map = new HashMap<>();
                        map.put("post", posts);
                        db.collection("stars").document(user.getEmail()).set(map);

                    } else {
                        posts.remove(namePost);
                        Map<String, Object> map = new HashMap<>();
                        map.put("post", posts);
                        db.collection("stars").document(user.getEmail()).set(map);
                    }
                }
            }
        });
    }



    public void getStar(Model.Listener<List<String>> listener){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        db.collection("stars").document(user.getEmail()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot json = task.getResult();
                List<String> posts = new LinkedList<>();
                if(json.contains("post")) {
                    posts.addAll((List<String>) json.get("post"));
                }
                listener.onComplete(posts);

            }
        });
    }

}
