package com.example.travelapp;

import androidx.lifecycle.ViewModel;

import com.example.travelapp.model.Post;
import com.example.travelapp.model.User;

import java.util.LinkedList;
import java.util.List;


// details of user connected
public class CurrentUserViewModel extends ViewModel {

    private static User user = null;

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() { return this.user; }
}
