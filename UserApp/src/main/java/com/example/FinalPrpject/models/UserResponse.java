package com.example.FinalPrpject.models;

import java.util.List;

public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private List<Long> followingIds;
    private List<Long> followerIds;
    private List<Long> blockedUserIds;

    public UserResponse(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
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

    public List<Long> getFollowingIds() {
        return followingIds;
    }

    public List<Long> getFollowerIds() {
        return followerIds;
    }

    public List<Long> getBlockedUserIds() {
        return blockedUserIds;
    }
}