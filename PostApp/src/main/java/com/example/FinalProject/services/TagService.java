package com.example.FinalProject.services;

import org.springframework.stereotype.Service;
import com.example.FinalProject.repositories.PostRepository;
import com.example.FinalProject.models.Post;
import com.example.FinalProject.events.dtos.Notification;

import java.time.LocalDateTime;

@Service
public class TagService {
    private final PostRepository postRepository;
    private final NotificationEventPublisher notificationEventPublisher;

    public TagService(PostRepository postRepository,
            NotificationEventPublisher notificationEventPublisher) {
        this.postRepository = postRepository;
        this.notificationEventPublisher = notificationEventPublisher;
    }

    public Post tagUserInPost(String postId, String taggerId, String taggedUserId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));
        // For simplicity, add taggedUserId to tags list
        post.getTags().add(taggedUserId);
        Post updated = postRepository.save(post);

        Notification notification = new Notification(
                postId,
                taggerId,
                taggedUserId,
                null,
                null,
                "You were tagged in post '" + post.getTitle() + "'",
                "TAG",
                LocalDateTime.now());
        notificationEventPublisher.sendNotification(notification);
        return updated;
    }
}