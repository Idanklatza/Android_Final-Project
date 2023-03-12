package com.example.travelapp;

import static com.example.travelapp.MyApplication.getMyContext;
import static com.example.travelapp.model.Model.isOnline;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.example.travelapp.databinding.FragmentSavePostBinding;
import com.example.travelapp.model.Model;
import com.example.travelapp.model.Post;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;


public class SavePostFragment extends PostsListFragment {
    FragmentSavePostBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSavePostBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

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
                // set the post
                Post post = viewModel.getData().get(pos);


                // send data of posts to next fragment (PostFragment)
                SavePostFragmentDirections.ActionStarsFragmentToPostFragment action = SavePostFragmentDirections.actionStarsFragmentToPostFragment(post.name,post.description,post.avatarUrl);
                Navigation.findNavController(view).navigate(action);
            }
        });
        return view;
    }

    @Override
    void reloadData(){
        binding.progressBar2.setVisibility(View.VISIBLE);

        // get all posts (not live data)
        Model.instance().getAllPosts((postList)->{
            // postlist = all the posts in app
            // clear the data list
            viewModel.getData().removeAll(viewModel.getData());

            // get all stars of the user connect from firebase
                if(isOnline(getMyContext())) {
                Model.instance().getAllStars(stars -> {
                    for (Post post : postList) {
                        // filter the relist by name posts in stars list and save in data list
                        if (stars.contains(post.name))
                            viewModel.getData().add(post);

                    }
                    // sort the data and send to adapter
                    Collections.sort(viewModel.getData(), Comparator.comparing(Post::getName));
                    adapter.setData(viewModel.getData());
                });
            }
        });

        binding.progressBar2.setVisibility(View.GONE);

    }
}