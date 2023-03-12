package com.example.travelapp;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import androidx.navigation.Navigation;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.travelapp.databinding.FragmentAddPostBinding;
import com.example.travelapp.model.Model;
import com.example.travelapp.model.Post;
import com.squareup.picasso.Picasso;

public class EditUserPostFragment extends AddPostFragment {
    String title;
    String description;
    String imageString;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAddPostBinding.inflate(inflater,container,false);
        View view = binding.getRoot();

        getElement();
        Model.instance().getCurrentUser(user -> {
            email = user.getEmail();
        });

        binding.saveBtn.setOnClickListener(view1 -> {
            savePost(view1);
        });


        binding.cancellBtn.setOnClickListener((view1)->{
            Navigation.findNavController(view1).popBackStack();
            Navigation.findNavController(view1).popBackStack();

        });

        binding.camerabutton.setOnClickListener(view1->{
            cameraLauncher.launch(null);
        });
        binding.gallerybutton.setOnClickListener(view1->{
            galleryAppLauncher.launch("image/*");
        });

        return view;
    }

    public void getElement(){

        // set from argument fragment to Strings;
        title = PostFragmentArgs.fromBundle(getArguments()).getNamePost();
        description = PostFragmentArgs.fromBundle(getArguments()).getDescription();
        imageString = (PostFragmentArgs.fromBundle(getArguments()).getAvatarUrl());

        // set data user in the ui
        if (title != null){
            binding.nameEt.setText(title);
        }
        if (description != null){
            binding.descriptionEt.setText(description);
        }
        if (imageString.isEmpty() || imageString ==""){
            binding.avatarImg.setImageResource(R.drawable.nophoto);

        }else{
            Picasso.get().load(imageString).error(R.drawable.error).into(binding.avatarImg);
        }

        // set Enabled name of post
        binding.nameEt.setEnabled(false);
        binding.styleNameEt.setStartIconDrawable(null);
    }

    @Override
    public void savePost(View view1) {

        String name = binding.nameEt.getText().toString();
        String description = binding.descriptionEt.getText().toString();
        String id = name;

        // create new post object
        Post post = new Post(name, id, "", false, description, email);

        // save image post
        if (isAvatarSelected || imageString != "") {
            binding.avatarImg.setDrawingCacheEnabled(true);
            binding.avatarImg.buildDrawingCache();
            Bitmap bitmap = ((BitmapDrawable) binding.avatarImg.getDrawable()).getBitmap();

            // save the photo in firebase and return the url
            Model.instance().uploadImage(id, bitmap, url -> {
                if (url != null) {
                    post.setAvatarUrl(url);
                }

                // save post
                Model.instance().addPost(post, (unused) -> {
                    Navigation.findNavController(view1).popBackStack();
                });
            });

        } else {

            Model.instance().addPost(post, (unused) -> {
                Navigation.findNavController(view1).popBackStack();
            });
        }

    }
}