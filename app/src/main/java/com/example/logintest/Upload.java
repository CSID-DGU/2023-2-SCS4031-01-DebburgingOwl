package com.example.logintest;

public class Upload {
    private String imageUrl;
    private String uploaderId;
    private int likes;

    public Upload(String s, int i) {
        // Default constructor required for calls to DataSnapshot.getValue(Upload.class)
    }

    public Upload(String imageUrl, String uploaderId, int likes) {
        this.imageUrl = imageUrl;
        this.uploaderId = uploaderId;
        this.likes = likes;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUploaderId() {
        return uploaderId;
    }

    public void setUploaderId(String uploaderId) {
        this.uploaderId = uploaderId;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }
}
