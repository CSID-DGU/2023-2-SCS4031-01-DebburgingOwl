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
    private  int level;
    private int point;
    public User() {
        this.name = "";
        this.email = "";
        this.nickname = "";
        this.profileImageUrl = "";
        this.postCount = 0;
        this.likeCount = 0;
        this.exp = 0;
        this.levelThresholds = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5));
        this.level=1;
        this.point=0;

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
        this.level=1;
        this.point=0;

    }


    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
    public int getPoint(){return point;}
    public String getNickname() { return nickname; }

    public String getProfileImageUrl() { return profileImageUrl; }

    public int getPostCount() { return postCount; }

    public int getLikeCount() { return likeCount; }
    public void setExp(int exp){
        this.exp=exp;
    }
    public int getExp() { return exp; }
    public ArrayList<Integer> getLevelThresholds() {return levelThresholds;}

    public int getLevel(){return level;}

    public void updateLevel() {
        // 경험치가 200을 초과하면 레벨을 2로 설정
        if (this.exp >= 200 && this.level < 2) {
            this.level = 2;
        }
        if(this.exp >=500 && this.level <3){
            this.level = 3;
        }
        if(this.exp>=1000 && this.level <4){
            this.level=4;
        }
        // 추가적인 레벨 업 조건을 여기에 구현할 수 있습니다.
    }
}