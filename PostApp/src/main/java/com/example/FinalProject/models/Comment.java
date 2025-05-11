package com.example.FinalProject.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

public class Comment {
    @Id
    private String commentId;
    private String content;
    private int likes;
    private int dislikes;

    public Comment() {
    }

    public Comment(String content, int likes, int dislikes) {
        this.content = content;
        this.likes = likes;
        this.dislikes = dislikes;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getDislikes() {
        return dislikes;
    }

    public void setDislikes(int dislikes) {
        this.dislikes = dislikes;
    }

    public String getCommentId() {
        return commentId;
    }

}
