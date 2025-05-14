package com.example.NotificationApp.model;

public class NotificationPreferences {
    private boolean comment;
    private boolean follow;
    private boolean tag;

    public NotificationPreferences(boolean comment, boolean follow, boolean tag) {
        this.comment = comment;
        this.follow = follow;
        this.tag = tag;
    }

    public boolean isComment() {
        return comment;
    }

    public void setComment(boolean comment) {
        this.comment = comment;
    }

    public boolean isFollow() {
        return follow;
    }

    public void setFollow(boolean follow) {
        this.follow = follow;
    }

    public boolean isTag() {
        return tag;
    }

    public void setTag(boolean tag) {
        this.tag = tag;
    }
}
