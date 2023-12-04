package com.example.logintest;

public class DiaryEntry {
    private String id;
    private String title; // 제목 필드 추가
    private String content;
    private String timestamp;

    // Firebase를 위한 기본 생성자
    public DiaryEntry() {
    }

    // Parameterized Constructor
    public DiaryEntry(String id, String title, String content, String timestamp) {
        this.id = id;
        this.title = title; // 제목 설정
        this.content = content;
        this.timestamp = timestamp;
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

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
