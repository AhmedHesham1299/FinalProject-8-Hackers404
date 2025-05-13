package com.example.FinalProject.events.dtos;

import java.io.Serializable;

public class CommentCreatedEvent implements Serializable {
    private String commentId;
    private String postId;
    private String authorId;

    public CommentCreatedEvent() {
    }

    public CommentCreatedEvent(String commentId, String postId, String authorId) {
        this.commentId = commentId;
        this.postId = postId;
        this.authorId = authorId;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }
} 