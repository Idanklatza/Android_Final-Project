package com.example.travelapp.model;

public class User {

    String name;
    String password;
    String img;

    public User(String name, String password, String img) {
        this.name = name;
        this.password = password;
        this.img = img;
    }

    public User() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
