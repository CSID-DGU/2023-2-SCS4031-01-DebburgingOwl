package com.example.logintest;

public class Comment {
    private String commentId;   // 댓글의 고유 ID
    private String userId;      // 사용자 ID
    private String nickname;    // 사용자 닉네임
    private String commentText; // 댓글 텍스트
    private String currentDate; // 댓글 작성 날짜

    // Firebase를 위한 기본 생성자
    public Comment() {
    }

    // 모든 필드를 포함하는 생성자
    public Comment(String commentId ,String userId, String nickname, String commentText, String currentDate) {
        this.commentId=commentId;
        this.userId = userId;
        this.nickname = nickname;
        this.commentText = commentText;
        this.currentDate = currentDate;
    }

    // Getter와 Setter 메소드들
    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }
}
