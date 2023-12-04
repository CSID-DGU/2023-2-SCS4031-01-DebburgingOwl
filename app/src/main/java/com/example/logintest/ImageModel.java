package com.example.logintest;

public class ImageModel {
    private String imageUrl;
    private boolean publicStatus;
    private String uploader;
    private String imageId;
    private int likesCount; // 좋아요 수를 나타내는 필드 추가

    // Default Constructor
    public ImageModel() {
        // Firebase 데이터 스냅샷을 위한 기본 생성자
    }

    // Parameterized Constructor
    public ImageModel(String imageUrl, boolean publicStatus, String uploader) {
        this.imageUrl = imageUrl;
        this.publicStatus = publicStatus;
        this.uploader = uploader;
        this.likesCount = 0; // 기본값으로 0 설정
    }

    // Getters and Setters
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean getPublicStatus() {
        return publicStatus;
    }

    public void setPublicStatus(boolean publicStatus) {
        this.publicStatus = publicStatus;
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

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }
}
