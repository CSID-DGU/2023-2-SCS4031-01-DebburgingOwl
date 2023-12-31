package com.example.logintest;

import java.util.ArrayList;
import java.util.Arrays;

public class User {
    private String name;
    private String email;
    private String nickname;
    private String profileImageUrl;
    private String usertype;
    private int postCount;
    private int likeCount;
    private String userType;
    private int exp;
    private  int level;
    private int point;
    public User() {
        this.name = "";
        this.email = "";
        this.nickname = "";
        this.profileImageUrl = "";
        this.usertype="";
        this.postCount = 0;
        this.likeCount = 0;
        this.exp = 0;
        this.level=1;
        this.point=0;

    }

    public User(String name, String email, String nickname,String userType) {
        this.name = name;
        this.email = email;
        this.nickname = nickname;
        this.profileImageUrl = "";
        this.usertype=userType;
        this.postCount = 0;
        this.likeCount = 0;
        this.exp = 0;
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
    public void setPoint(int point){
        this.point=point;
    }
    public void setUserType(String usertype){
        this.usertype=usertype;
    }
    public String getUserType(){
        return usertype;
    }
    public int getLikeCount() { return likeCount; }
    public void setExp(int exp){
        this.exp=exp;
    }
    public int getExp() { return exp; }

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

        if(this.exp>=2000 && this.level<5){
            this.level=5;
        }
        // 추가적인 레벨 업 조건을 여기에 구현할 수 있습니다.
    }
}