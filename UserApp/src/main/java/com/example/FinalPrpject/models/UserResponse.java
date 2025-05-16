package com.example.FinalPrpject.models;

import java.util.List;

public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private boolean isBanned;
    private boolean isPushEnabled;
    private boolean isEmailEnabled;
    private List<String> warnings;
    private List<Long> followingIds;
    private List<Long> followerIds;
    private List<Long> blockedUserIds;


    public UserResponse(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.isBanned = user.isBanned();
        this.warnings = user.getWarnings();
        this.isPushEnabled = user.isPushEnabled();
        this.isEmailEnabled = user.isEmailEnabled();
        this.followingIds = user.getFollowing().stream().map(User::getId).toList();
        this.followerIds = user.getFollowers().stream().map(User::getId).toList();
        this.blockedUserIds = user.getBlockedUsers().stream()
                .map(User::getId)
                .toList();
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public boolean isBanned() {
        return isBanned;
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public List<Long> getFollowingIds() {
        return followingIds;
    }

    public List<Long> getFollowerIds() {
        return followerIds;
    }

    public List<Long> getBlockedUserIds() {
        return blockedUserIds;
    }

    public boolean isPushEnabled() {
        return isPushEnabled;
    }

    public void setPushEnabled(boolean isPushEnabled) {
        this.isPushEnabled = isPushEnabled;
    }

    public boolean isEmailEnabled() {
        return isEmailEnabled;
    }

    public void setEmailEnabled(boolean isEmailEnabled) {
        this.isEmailEnabled = isEmailEnabled;
    }
}