package com.example.logintest;

public class DiaryEntry {
    private String id;
    private String title; // 제목 필드 추가
    private String content;
    private String timestamp;

    private String imageUrl;

    // Firebase를 위한 기본 생성자
    public DiaryEntry() {
    }

    // Parameterized Constructor
    public DiaryEntry(String id, String title, String content, String timestamp, String imageUrl) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.timestamp = timestamp;
        this.imageUrl = imageUrl; // 이미지 URL 설정
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() { // 제목 getter
        return title;
    }

    public void setTitle(String title) { // 제목 setter
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
