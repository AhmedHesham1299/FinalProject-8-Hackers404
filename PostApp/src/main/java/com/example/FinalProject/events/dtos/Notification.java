package com.example.FinalProject.events.dtos;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Notification implements Serializable {
    private String postID;
    private String senderID;
    private String receiverID;
    private String senderName;
    private String receiverName;
    private String content;
    private String type;
    private LocalDateTime timestamp;

    public Notification() {
    }

    public Notification(String postID, String senderID, String receiverID, String senderName, String receiverName,
            String content, String type, LocalDateTime timestamp) {
        this.postID = postID;
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.senderName = senderName;
        this.receiverName = receiverName;
        this.content = content;
        this.type = type;
        this.timestamp = timestamp;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getReceiverID() {
        return receiverID;
    }

    public void setReceiverID(String receiverID) {
        this.receiverID = receiverID;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}