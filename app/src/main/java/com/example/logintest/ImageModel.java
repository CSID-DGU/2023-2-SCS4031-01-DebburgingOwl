package com.example.logintest;

public class ImageModel {
    private String imageUrl;
    private boolean publicStatus; // 필드 이름 변경
    private String uploader;
    private String imageId; // 고유 ID 필드 추가


    // Constructor
    public ImageModel() {
        // Default constructor required for calls to DataSnapshot.getValue(ImageModel.class)
    }

    // Parameterized constructor
    public ImageModel(String imageUrl, boolean publicStatus, String uploader) {
        this.imageUrl = imageUrl;
        this.publicStatus = publicStatus;
        this.uploader = uploader;
    }

    // Getters and setters
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
}
