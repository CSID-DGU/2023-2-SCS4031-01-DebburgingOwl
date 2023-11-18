package com.example.logintest;
import java.io.Serializable;

public class Notice implements Serializable {
    private String title;
    private String content;

    // 기본 생성자 및 게터, 세터 메서드 추가

    public Notice() {
        // 기본 생성자가 필요합니다.
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
