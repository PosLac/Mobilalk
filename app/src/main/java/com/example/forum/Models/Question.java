package com.example.forum.Models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Question {
    private String id;
    private String title;
    private String description;
    private List<String> answers = new ArrayList<String>();
    private String userName;
    private String userEmail;
    @ServerTimestamp
    private Date date;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    private String imageResource;

    public Question(String id, String title, String description, String userEmail, String imageResource, List<String> answers, String userName, Date date) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.answers = answers;
        this.userEmail = userEmail;
        this.imageResource = imageResource;
        this.userName = userName;
        this.date = date;
    }

    public Question(String id, String title, String description, String userName, String imageResource, List<String> answers, Date date) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.answers = answers;
        this.imageResource = imageResource;
        this.userName = userName;
        this.date = date;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
