package com.example.travelapp.model;

import java.util.HashMap;
import java.util.Map;
import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity
public class Post {
    @PrimaryKey
    @NonNull
    public String id;
    public String description = "";
    public String img = "";
    public Boolean cb =false;

    public Post() {}

    public Post(String id, String description, String img, Boolean cb) {
        this.id = id;
        this.description = description;
        this.img = img;
        this.cb = cb;
    }

    static final String ID = "id";
    static final String DESCRIPTION = "description";
    static final String IMG = "img";
    static final String CB = "cb";
    static final String COLLECTION = "posts";

    public static Post fromJson(Map<String,Object> json) {
        String id = (String)json.get(ID);
        String description = (String)json.get(DESCRIPTION);
        String img = (String) json.get(IMG);
        Boolean cb = (Boolean) json.get(CB);
        Post post = new Post(id, description, img, cb);
        return post;
    }

    public Map<String,Object> toJson() {
        Map<String, Object> json = new HashMap<>();
        json.put(ID, getId());
        json.put(DESCRIPTION, getDescription());
        json.put(IMG, getImg());
        json.put(CB, getCb());
        return json;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getImg() {
        return img;
    }

    public Boolean getCb() {
        return cb;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public void setCb(Boolean cb) {
        this.cb = cb;
    }

}