package com.example.logintest;

import java.io.Serializable;

public class Bookmark implements Serializable {
    private String contentType;
    private String userID;
    private String contentKey;
    private boolean isBookmarked;

    public Bookmark() {
        // 기본 생성자가 필요합니다.
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getContentKey() {
        return contentKey;
    }

    public void setContentKey(String contentKey) {
        this.contentKey = contentKey;
    }

    public boolean getIsBookmarked() {
        return isBookmarked;
    }

    public void setIsBookmarked(boolean isBookmarked) {
        this.isBookmarked = isBookmarked;
    }
}
