package com.example.FinalProject.observers;

import com.example.FinalProject.services.NotificationEventPublisher;
import com.example.FinalProject.events.dtos.Notification;
import com.example.FinalProject.models.Comment;
import com.example.FinalProject.models.Like;
import com.example.FinalProject.models.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;

@Component
public class NotificationPostObserver implements PostObserver {

    private final PostObservable postObservable;
    private final NotificationEventPublisher notificationEventPublisher;

    @Autowired
    public NotificationPostObserver(PostObservable postObservable,
            NotificationEventPublisher notificationEventPublisher) {
        this.postObservable = postObservable;
        this.notificationEventPublisher = notificationEventPublisher;
    }

    @PostConstruct
    public void init() {
        postObservable.addObserver(this);
    }

    @Override
    public void onCommentAdded(Post post, Comment comment) {
        // Skip notification if author is commenting on their own post
        if (post.getAuthorId().equals(comment.getAuthorId())) {
            return;
        }
        notificationEventPublisher.sendNotification(
                new Notification(
                        post.getId(),
                        comment.getAuthorId(),
                        post.getAuthorId(),
                        null,
                        null,
                        "Your post '" + post.getTitle() + "' has a new comment",
                        "COMMENT",
                        LocalDateTime.now()));
    }

    @Override
    public void onPostLiked(Post post, Like like) {
        // Skip notification if author likes their own post
        if (post.getAuthorId().equals(like.getUserId())) {
            return;
        }
        String action = like.isLike() ? "liked" : "disliked";
        notificationEventPublisher.sendNotification(
                new Notification(
                        post.getId(),
                        like.getUserId(),
                        post.getAuthorId(),
                        null,
                        null,
                        "Someone has " + action + " your post '" + post.getTitle() + "'",
                        "LIKE",
                        LocalDateTime.now()));
    }

    @Override
    public void onCommentLiked(Comment comment, Like like) {
        // Skip notification if author likes their own comment
        if (comment.getAuthorId().equals(like.getUserId())) {
            return;
        }
        String action = like.isLike() ? "liked" : "disliked";
        notificationEventPublisher.sendNotification(
                new Notification(
                        comment.getPostId(),
                        like.getUserId(),
                        comment.getAuthorId(),
                        null,
                        null,
                        "Someone has " + action + " your comment",
                        "LIKE",
                        LocalDateTime.now()));
    }
}