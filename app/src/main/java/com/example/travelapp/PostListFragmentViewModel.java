package com.example.travelapp;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.example.travelapp.model.Model;
import com.example.travelapp.model.Post;
import java.util.LinkedList;
import java.util.List;

public class PostListFragmentViewModel extends ViewModel {

    private List<Post> data = new LinkedList<>();  // list posts - for display data that not in cache (stars, user posts)

    private LiveData<List<Post>> liveData = Model.instance().getAllPostsNew(); // cache posts = livedata

    List<Post> getData(){
        return data;
    }

    LiveData<List<Post>> getLiveData(){
        return liveData;
    }
}
