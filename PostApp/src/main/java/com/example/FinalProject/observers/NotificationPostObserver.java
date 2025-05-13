package com.example.FinalProject.observers;

import com.example.FinalProject.events.NotificationEventPublisher;
import com.example.FinalProject.events.dtos.NotificationEvent;
import com.example.FinalProject.models.Comment;
import com.example.FinalProject.models.Like;
import com.example.FinalProject.models.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

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

        notificationEventPublisher.sendNotificationEvent(
                new NotificationEvent(
                        post.getAuthorId(),
                        "New Comment",
                        "Your post '" + post.getTitle() + "' has a new comment",
                        "/posts/" + post.getId(),
                        "COMMENT"
                )
        );
    }

    @Override
    public void onPostLiked(Post post, Like like) {
        // Skip notification if author likes their own post
        if (post.getAuthorId().equals(like.getUserId())) {
            return;
        }

        String action = like.isLike() ? "liked" : "disliked";
        
        notificationEventPublisher.sendNotificationEvent(
                new NotificationEvent(
                        post.getAuthorId(),
                        "Post " + action,
                        "Someone has " + action + " your post '" + post.getTitle() + "'",
                        "/posts/" + post.getId(),
                        "LIKE"
                )
        );
    }

    @Override
    public void onCommentLiked(Comment comment, Like like) {
        // Skip notification if author likes their own comment
        if (comment.getAuthorId().equals(like.getUserId())) {
            return;
        }

        String action = like.isLike() ? "liked" : "disliked";
        
        notificationEventPublisher.sendNotificationEvent(
                new NotificationEvent(
                        comment.getAuthorId(),
                        "Comment " + action,
                        "Someone has " + action + " your comment",
                        "/posts/" + comment.getPostId(),
                        "LIKE"
                )
        );
    }
} 