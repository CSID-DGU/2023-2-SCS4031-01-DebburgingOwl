package com.example.logintest;

import java.util.ArrayList;
import java.util.Arrays;

public class User {
    private String name;
    private String email;
    private String nickname;
    private String profileImageUrl;
    private int postCount;
    private int likeCount;
    private int exp;
    private ArrayList<Integer> levelThresholds;

    public User() {
        this.name = "";
        this.email = "";
        this.nickname = "";
        this.profileImageUrl = "";
        this.postCount = 0;
        this.likeCount = 0;
        this.exp = 0;
        this.levelThresholds = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5));
    }

    public User(String name, String email, String nickname) {
        this.name = name;
        this.email = email;
        this.nickname = nickname;
        this.profileImageUrl = "";
        this.postCount = 0;
        this.likeCount = 0;
        this.exp = 0;
        this.levelThresholds = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5));
    }


    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getNickname() { return nickname; }

    public String getProfileImageUrl() { return profileImageUrl; }

    public int getPostCount() { return postCount; }

    public int getLikeCount() { return likeCount; }

    public int getExp() { return exp; }
    public ArrayList<Integer> getLevelThresholds() {return levelThresholds;}
}