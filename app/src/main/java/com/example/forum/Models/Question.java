package com.example.forum.Models;

import java.util.ArrayList;
import java.util.List;

public class Question {
    private String title;
    private String description;
    private List<String> answers = new ArrayList<String>();
    private String userName;
    private String userEmail;
    private Date;// TODO: 2022. 07. 01.

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    private String imageResource;

    public Question(String title, String description, String userEmail, String imageResource, List<String> answers, String userName) {
        this.title = title;
        this.description = description;
        this.answers = answers;
        this.userEmail = userEmail;
        this.imageResource = imageResource;
        this.userName = userName;
    }

    public Question(String title, String description, String userName, String imageResource, List<String> answers) {
        this.title = title;
        this.description = description;
        this.answers = answers;
        this.imageResource = imageResource;
        this.userName = userName;
    }

    public String getImageResource() {
        return imageResource;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public void setImageResource(String imageResource) {
        this.imageResource = imageResource;
    }

    public List<String> getAnswers() {
        return answers;
    }

    public void setAnswers(List<String> answers) {
        this.answers = answers;
    }

    public Question() {}

    public String getUserEmail() {
        return userEmail;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
