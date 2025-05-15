package com.example.FinalProject.services;

import org.springframework.stereotype.Service;
import com.example.FinalProject.repositories.PostRepository;
import com.example.FinalProject.models.Post;
import com.example.FinalProject.events.dtos.UserTaggedInPostEvent;

import java.time.LocalDateTime;
import java.util.UUID;

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

        UserTaggedInPostEvent event = new UserTaggedInPostEvent(
                UUID.randomUUID().toString(),
                "USER_TAGGED_IN_POST",
                LocalDateTime.now(),
                postId,
                taggerId,
                taggedUserId,
                post.getAuthorId());
        notificationEventPublisher.sendUserTaggedEvent(event);
        return updated;
    }
}