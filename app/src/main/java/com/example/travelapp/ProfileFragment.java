package com.example.travelapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.travelapp.databinding.FragmentProfileBinding;
import com.example.travelapp.model.Model;
import com.example.travelapp.model.Post;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class ProfileFragment extends PostsListFragment {
    FragmentProfileBinding binding;
    String email;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // get the details of user from firebase
        Model.instance().getCurrentUser(currentUser-> {
            email = currentUser.getEmail();
            binding.email.setText(currentUser.email);
            binding.firstName.setText(currentUser.firstName);
            binding.lastName.setText(currentUser.lastName);
            if(currentUser.avatarUrl !="")
                Picasso.get().load(currentUser.avatarUrl).error(R.drawable.error).into(binding.avatarImg3);
        });

        // list
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // set to inflater and data live list
        adapter = new PostRecyclerAdapter(getLayoutInflater(),viewModel.getData());
        binding.recyclerView.setAdapter(adapter);

        // click on post (get pos)
        adapter.setOnItemClickListener(new PostRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                Log.d("TAG", "Row was clicked " + pos);
                Post post = viewModel.getData().get(pos);   // save the post in line "pos"(int) ;

                // send the arguments to userPostFragment
                ProfileFragmentDirections.ActionProfileToFragmentUserPostPage action = ProfileFragmentDirections.actionProfileToFragmentUserPostPage(post.getName(),post.getDescription(),post.getAvatarUrl());
                Navigation.findNavController(view).navigate(action);
            }
        });

        return view;
    }

    @Override
    void reloadData() {
        binding.progressBar3.setVisibility(View.VISIBLE);
        Model.instance().getAllPosts((postList)-> {
            binding.progressBar3.setVisibility(View.GONE);
            viewModel.getData().removeAll(viewModel.getData());
            for(Post post : postList) {
                if(post.author.equals(email))
                    viewModel.getData().add(post);
            }
            adapter.setData(viewModel.getData());
        });

    }

}