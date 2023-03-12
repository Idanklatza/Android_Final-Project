package com.example.travelapp;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.travelapp.databinding.FragmentPostsListBinding;
import com.example.travelapp.model.Model;
import com.example.travelapp.model.Post;

import java.util.Collections;
import java.util.Comparator;

public class PostsListFragment extends Fragment {
    FragmentPostsListBinding binding;
    PostRecyclerAdapter adapter;
    PostListFragmentViewModel viewModel;
    CurrentUserViewModel currentUser;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPostsListBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        getActivity().findViewById(R.id.main_bottomNavigationView).setVisibility(View.VISIBLE);

        // list
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // set to inflater and data live list
        adapter = new PostRecyclerAdapter(getLayoutInflater(),viewModel.getLiveData().getValue());
        binding.recyclerView.setAdapter(adapter);

        // click on post (get pos)
        adapter.setOnItemClickListener(new PostRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                // set the post
                Post post = viewModel.getLiveData().getValue().get(pos);

                // send data of posts to next fragment (userPostPage)
                    PostsListFragmentDirections.ActionPostsListFragmentToPostFragment action = PostsListFragmentDirections.actionPostsListFragmentToPostFragment(post.getName(),post.getDescription(),post.getAvatarUrl());
                Navigation.findNavController(view).navigate(action);
            }
        });

        // done loading
        binding.progressBar.setVisibility(View.GONE);

        // refresh > update loading status
        Model.instance().EventPostsListLoadingState.observe(getViewLifecycleOwner(),status->{
            binding.swipeRefresh.setRefreshing(status == Model.LoadingState.LOADING);
        });

        // update the data list by data live (cache)
        viewModel.getLiveData().observe( getViewLifecycleOwner(),list->{
            // list == cache - update
            Collections.sort(list, Comparator.comparing(Post::getName));  // sort the cache
            adapter.setData(list);   // set cache in the data list
        });

        // update
        binding.swipeRefresh.setOnRefreshListener(()->{
            reloadData();
        });
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        viewModel = new ViewModelProvider(this).get(PostListFragmentViewModel.class);
    }

    // list of post
    @Override
    public void onResume() {
        super.onResume();
        reloadData();
    }

    // get posts by live data
    void reloadData(){
        Model.instance().refreshAllPosts();
    }
}