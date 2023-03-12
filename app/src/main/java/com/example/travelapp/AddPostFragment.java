package com.example.travelapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.example.travelapp.databinding.FragmentAddPostBinding;
import com.example.travelapp.model.Model;
import com.example.travelapp.model.Post;
import com.google.android.material.textfield.TextInputEditText;

public class AddPostFragment extends Fragment {
    CurrentUserViewModel currentUser;
    FragmentAddPostBinding binding;
    Boolean isAvatarSelected = false;
    ActivityResultLauncher<Void> cameraLauncher;
    ActivityResultLauncher<String> galleryAppLauncher;
    String email;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentActivity parentActivity = getActivity();

        // update menu:
        parentActivity.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menu.removeItem(R.id.addPost);  // remove bottom add in up menu
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                return false;
            }
        }, this, Lifecycle.State.RESUMED);


        cameraLauncher = registerForActivityResult(new ActivityResultContracts.TakePicturePreview(),
                new ActivityResultCallback<Bitmap>() {
                    @Override
                    public void onActivityResult(Bitmap result) {
                        if (result != null) {
                            binding.avatarImg.setImageBitmap(result); // display the photo of post
                            isAvatarSelected = true;
                        }
                    }
                });

        galleryAppLauncher = registerForActivityResult(new
                ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        if (result != null) {
                            binding.avatarImg.setImageURI(result);
                            isAvatarSelected = true;
                        }
                    }
                });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddPostBinding.inflate(inflater,container,false);
        View view = binding.getRoot();
        // get email of user
        Model.instance().getCurrentUser(user -> {
            email = user.getEmail();
        });

        binding.saveBtn.setOnClickListener(view1 -> {
            savePost(view1);
        });

        binding.cancellBtn.setOnClickListener(view1 ->
                Navigation.findNavController(view1).popBackStack(R.id.PostsListFragment,false));

        binding.camerabutton.setOnClickListener(view1->{
            cameraLauncher.launch(null);
        });

        binding.gallerybutton.setOnClickListener(view1->{
            galleryAppLauncher.launch("image/*");
        });

        return view;
    }


    public void savePost(View view1){
        String name = binding.nameEt.getText().toString();
        String description = binding.descriptionEt.getText().toString();
        String id = name;

        // check the field input
        if(name.isEmpty()){
            TextInputEditText input = binding.nameEt;
            input.setError("This field cannot be empty");
        }

        else {
            // create new post object
            Post post = new Post(name, id, "", false, description, email);

            // save image post
            if (isAvatarSelected) {
                binding.avatarImg.setDrawingCacheEnabled(true);
                binding.avatarImg.buildDrawingCache();
                Bitmap bitmap = ((BitmapDrawable) binding.avatarImg.getDrawable()).getBitmap();

                // save image in firebase
                Model.instance().uploadImage(id, bitmap, url -> {
                    if (url != null) {
                        post.setAvatarUrl(url);
                    }
                    // save post on firebase
                    Model.instance().addPost(post, (unused) -> {
                        Navigation.findNavController(view1).popBackStack();
                    });
                });
            } else { // if the user not upload a photo

                // save post on firebase
                Model.instance().addPost(post, (unused) -> {
                    Navigation.findNavController(view1).popBackStack();
                });
            }
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        currentUser = new ViewModelProvider(this).get(CurrentUserViewModel.class);
    }

}