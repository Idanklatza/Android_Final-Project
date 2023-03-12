package com.example.travelapp;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.example.travelapp.databinding.FragmentUserPostPageBinding;
import com.example.travelapp.model.Model;
import com.squareup.picasso.Picasso;


public class UserPostPageFragment extends PostFragment {

    FragmentUserPostPageBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentUserPostPageBinding.inflate(inflater,container,false);
        View view = binding.getRoot();
        this.getElement();

        avatarImg = (PostFragmentArgs.fromBundle(getArguments()).getAvatarUrl());

        if (title != null){
            binding.postTitleTv.setText(title);
        }
        if (description != null){
            binding.DescriptionTv.setText(description);
        }
        if (avatarImg.isEmpty() || avatarImg == ""){
            binding.avatarImg.setImageResource(R.drawable.nophoto);
        }else{
            Picasso.get().load(avatarImg).error(R.drawable.error).into(binding.avatarImg);
        }

        binding.postEditBtn.setOnClickListener((view2)->{
            UserPostPageFragmentDirections.ActionFragmentUserPostPageToEditUserPostPageFragment2 action = UserPostPageFragmentDirections.actionFragmentUserPostPageToEditUserPostPageFragment2(title,description,avatarImg);
            Navigation.findNavController(view).navigate(action);
        });

        binding.backBtn.setOnClickListener((view1)->{
            Navigation.findNavController(view1).popBackStack();
        });

        return view;
    }
}