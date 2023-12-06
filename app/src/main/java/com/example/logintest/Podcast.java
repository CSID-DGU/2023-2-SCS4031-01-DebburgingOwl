package com.example.logintest;

import java.io.Serializable;

public class Podcast implements Serializable {
    private String title;
    private String description;
    private String userID;
    private String youtubeLink;

    public Podcast(String title, String description, String userID, String youtubeLink) {
        this.title = title;
        this.youtubeLink = youtubeLink;
        this.description = description;
        this.userID = userID;
    }

    public Podcast() {

    }

    public String getTitle() {
        return title;
    }

    public String getYoutubeLink() {
        return youtubeLink;
    }

    public  String getDescription() {
        return description;
    }

    public String getUserID() {
        return userID;
    }
}
