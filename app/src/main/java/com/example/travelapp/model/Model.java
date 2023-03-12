package com.example.travelapp.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.core.os.HandlerCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import okhttp3.ResponseBody;

public class Model {
    private static final Model _instance = new Model();
    private Executor executor = Executors.newSingleThreadExecutor();
    private Handler mainHandler = HandlerCompat.createAsync(Looper.getMainLooper());
    private FirebaseModel firebaseModel = new FirebaseModel();
    private LiveData<List<Post>> postList;
    AppLocalDbRepository localDb = AppLocalDb.getAppDb();

    public static Model instance(){
        return _instance;
    }

    private Model() {}

    public interface Listener<T> {
        void onComplete( T data);
    }

    public void getAllPosts(Listener<List<Post>> callback) {
        firebaseModel.getAllPosts(callback);

    }

    // for cache
    public enum LoadingState{
        LOADING,
        NOT_LOADING
    }

    final public MutableLiveData<LoadingState> EventPostsListLoadingState = new MutableLiveData<LoadingState>(LoadingState.NOT_LOADING);

    public LiveData<List<Post>> getAllPostsNew() {
        if(postList == null){
            postList = localDb.postDao().getAll();
            refreshAllPosts();
        }
        return postList;
    }

    public void refreshAllPosts(){
        EventPostsListLoadingState.setValue(LoadingState.LOADING);
        // get local last update
        Long localLastUpdate = Post.getLocalLastUpdate();

        // get all updated recorde from firebase since local last update
        firebaseModel.getAllPostsSince(localLastUpdate,list->{
            executor.execute(()->{
                Log.d("TAG", " firebase return : " + list.size());
                Long time = localLastUpdate;
                for(Post post:list){
                    // insert new records into ROOM
                    localDb.postDao().insertAll(post);
                    if (time < post.getLastUpdated()){
                        time = post.getLastUpdated();
                    }
                }
                try {
                    Thread.sleep(0000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // update local last update
                Post.setLocalLastUpdate(time);
                EventPostsListLoadingState.postValue(LoadingState.NOT_LOADING);
            });
        });
    }

    public void addPost(Post post, Listener<Void> listener) {
        firebaseModel.addPost(post, listener);
    }

    public void UpdatePost(Post post, Listener<Void> listener) {
        executor.execute(() -> {
            localDb.postDao().update(post);
            mainHandler.post(() -> {
                listener.onComplete(null);
            });
        });
    }

    public void addUser(User us, Listener<Void> listener) {
        firebaseModel.addUser(us, listener);
    }

    public void saveStar(String namePost){
        firebaseModel.saveStar(namePost);
    }

    public void getAllStars(Listener<List<String>> listener){
        firebaseModel.getStar(listener);
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void uploadImage(String name, Bitmap bitmap, Listener<String> listener) {
        firebaseModel.uploadImage(name,bitmap,listener);
    }

    // create user
    public void createUserWithEmailAndPassword(String email, String password, Listener<Boolean> listener) {
        firebaseModel.createAccount(email,password,listener);
    }

    // login user
    public void login(String email, String password,Listener<Boolean> listener){
        firebaseModel.login(email,password,listener);
    }

    // update user
    public void updateUser(User us,Listener<Boolean> listener){
        firebaseModel.updateUser(us, listener);
    }

    // check if user login
    public void isSignedIn(Model.Listener<Boolean> listener) {
        firebaseModel.isSignedIn(listener);
    }

    // get all details user
    public void getCurrentUser(Listener<User> listener){
        firebaseModel.getCurrentUser(listener);
    }

    // logout user
    public void logout(){
        firebaseModel.logOut();
    }

}
