package com.example.FinalProject.events.dtos;

import java.io.Serializable;

public class NotificationEvent implements Serializable {
    private String userId;
    private String title;
    private String message;
    private String link;
    private String type;

    public NotificationEvent() {
    }

    public NotificationEvent(String userId, String title, String message, String link, String type) {
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.link = link;
        this.type = type;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}