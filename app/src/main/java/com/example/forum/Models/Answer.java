package com.example.forum.Models;

public class Answer {
    private String userName;
    private String description;
    private int imageResource;

    public Answer(String userName, String description, int imageResource) {
        this.userName = userName;
        this.description = description;
        this.imageResource = imageResource;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getImageResource() {
        return imageResource;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }
}
