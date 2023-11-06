package com.example.logintest;

public class ImageModel {
    private String imageUrl;
    private boolean isPublic;
    private String uploader; // or any other fields you need

    // Constructor
    public ImageModel() {
        // Default constructor required for calls to DataSnapshot.getValue(ImageModel.class)
    }

    // Parameterized constructor
    public ImageModel(String imageUrl, boolean isPublic, String uploader) {
        this.imageUrl = imageUrl;
        this.isPublic = isPublic;
        this.uploader = uploader;
    }

    // Getters and setters
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public String getUploader() {
        return uploader;
    }

    public void setUploader(String uploader) {
        this.uploader = uploader;
    }
}
