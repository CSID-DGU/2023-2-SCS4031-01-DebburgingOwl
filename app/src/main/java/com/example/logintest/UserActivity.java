package com.example.logintest;

public class UserActivity {
    private int likes;
    private int comments;

    // 기본 생성자 (Firebase가 필요로 함)
    public UserActivity() {
    }

    // 좋아요와 댓글 수를 포함한 생성자
    public UserActivity(int likes, int comments) {
        this.likes = likes;
        this.comments = comments;
    }

    // getter와 setter
    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getComments() {
        return comments;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }

    // 좋아요 또는 댓글 수 증가
    public void incrementLikesOrComments(boolean isLike) {
        if (isLike) {
            this.likes++;
        } else {
            this.comments++;
        }
    }

    // 좋아요 또는 댓글 수 감소
    public void decrementLikesOrComments(boolean isLike) {
        if (isLike && this.likes > 0) {
            this.likes--;
        } else if (!isLike && this.comments > 0) {
            this.comments--;
        }
    }
}
