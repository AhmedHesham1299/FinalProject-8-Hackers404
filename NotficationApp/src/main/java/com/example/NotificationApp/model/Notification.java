package com.example.NotificationApp.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;



@Document(collection = "notifications")
public class Notification {
    // TODO Include attributes: senderID, receiverID, senderName, receiverName, receiverEmail,
    //  type, timestamp, content
    @Id
    private String id;
    private String postID;
    private String senderID;
    private String receiverID;
    private String senderName;
    private String receiverName;
    private String receiverEmail;
    private String content;
    private String type;
    private LocalDateTime timestamp;
    private boolean read;

    public Notification() {}

    public Notification(String postID, String senderID, String receiverID, String senderName, String receiverName, String receiverEmail, String content, String type, LocalDateTime timestamp, boolean read) {
        this.postID = postID;
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.senderName = senderName;
        this.receiverName = receiverName;
        this.receiverEmail = receiverEmail;
        this.content = content;
        this.type = type;
        this.timestamp = timestamp;
        this.read = read;
    }

    public String getReceiverID() {
        return receiverID;
    }

    public void setReceiverID(String receiverID) {
        this.receiverID = receiverID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getReceiverEmail() {
        return receiverEmail;
    }

    public void setReceiverEmail(String receiverEmail) {
        this.receiverEmail = receiverEmail;
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

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

}
