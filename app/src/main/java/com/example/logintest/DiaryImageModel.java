package com.example.logintest;

public class DiaryImageModel {
    private String imageUrl;
    private String uploader;
    private String imageId;

    // Firebase 데이터 스냅샷을 위한 기본 생성자
    public DiaryImageModel() {
    }

    // Parameterized Constructor
    public DiaryImageModel(String imageUrl, String uploader) {
        this.imageUrl = imageUrl;
        this.uploader = uploader;
    }

    // Getters and Setters
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUploader() {
        return uploader;
    }

    public void setUploader(String uploader) {
        this.uploader = uploader;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }
}
