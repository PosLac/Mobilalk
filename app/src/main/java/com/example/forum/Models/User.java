package com.example.forum.Models;

public class User {
    private String userName;
    private String email;
    private String userImage;

    public User() {}

    public User(String userName, String email, String userImage) {
        this.userName = userName;
        this.email = email;
        this.userImage = userImage;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserImage() {
        return userImage;
    }
}