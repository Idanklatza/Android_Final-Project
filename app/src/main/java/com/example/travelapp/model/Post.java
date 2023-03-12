package com.example.travelapp.model;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;
import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.travelapp.MyApplication;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;


@Entity
public class Post {
    @PrimaryKey
    @NonNull
    public String name = "";
    public String id = "";
    public String avatarUrl = "";
    public String description = "";
    public Boolean cb = false;
    public String author = "";
    public Long lastUpdated;

    public Post() {}

    public Post(String name, String id, String avatarUrl, Boolean cb, String description, String author) {
        this.name = name;
        this.id = id;
        this.description = description;
        this.avatarUrl = avatarUrl;
        this.cb = cb;
        this.author = author;
    }

    static final String LAST_UPDATED = "lastUpdated";
    static final String LOCAL_LAST_UPDATED = "posts_local_last_update";
    // static final String COLLECTION = "posts";

    public static Post fromJson(Map<String,Object> json) {
        String id = (String)json.get("id");
        String name = (String)json.get("name");
        String description = (String)json.get("description");
        String avatarUrl = (String) json.get("avatarUrl");
        String author = (String) json.get("author");
        Boolean cb = (Boolean) json.get("cb");
        Post post = new Post(name, id, avatarUrl, cb, description, author);

        try{
            Timestamp time = (Timestamp) json.get(LAST_UPDATED) ;
            post.setLastUpdated(time.getSeconds());
        }catch (Exception e){}
        return post;
    }

    public static Long getLocalLastUpdate() {
        SharedPreferences sharedPref = MyApplication.getMyContext().getSharedPreferences("TAG", Context.MODE_PRIVATE);
        return sharedPref.getLong(LOCAL_LAST_UPDATED,0);
    }

    public static void setLocalLastUpdate(Long time) {
        SharedPreferences sharedPref = MyApplication.getMyContext().getSharedPreferences("TAG", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putLong(LOCAL_LAST_UPDATED, time);
        editor.commit();
    }

    public Map<String,Object> toJson() {
        Map<String, Object> json = new HashMap<>();
        json.put("name", getName());
        json.put("id", getId());
        json.put("description", getDescription());
        json.put("avatarUrl", getAvatarUrl());
        json.put("author", getAuthor());
        json.put("cb", getCb());
        json.put(LAST_UPDATED, FieldValue.serverTimestamp());
        return json;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getAuthor() { return author; }

    public Boolean getCb() {
        return cb;
    }

    public Long getLastUpdated() {
        return lastUpdated;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public void setAuthor(String author) { this.author = author; }

    public void setCb(Boolean cb) {
        this.cb = cb;
    }

    public void setLastUpdated(Long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

}