package com.example.FinalProject.events.dtos;

import java.io.Serializable;

public class LikeEvent implements Serializable {
    private String likeId;
    private String userId;
    private String targetId;
    private String targetType;
    private boolean isLike;

    public LikeEvent() {
    }

    public LikeEvent(String likeId, String userId, String targetId, String targetType, boolean isLike) {
        this.likeId = likeId;
        this.userId = userId;
        this.targetId = targetId;
        this.targetType = targetType;
        this.isLike = isLike;
    }

    public String getLikeId() {
        return likeId;
    }

    public void setLikeId(String likeId) {
        this.likeId = likeId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public boolean isLike() {
        return isLike;
    }

    public void setLike(boolean like) {
        isLike = like;
    }
} 