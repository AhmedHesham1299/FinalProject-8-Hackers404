package com.example.FinalPrpject.DTO;

public class Post {
    private Long userId;
    private String content;
    private String title;

    public Post() {
    }

    public Post(Long userId, String content, String title) {
        this.userId = userId;
        this.content = content;
        this.title = title;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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
