package com.example.forum;

public class Question {
    private String userName;
    private String title;
    private String description;
    private int imageResource;

    public Question(String userName, String title, String description, int imageResource) {
        this.userName = userName;
        this.title = title;
        this.description = description;
        this.imageResource = imageResource;
    }

    public Question() {}

    public String getUserName() {
        return userName;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getImageResource() {
        return imageResource;
    }
}
