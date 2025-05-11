package com.example.FinalProject.services;

import org.springframework.stereotype.Service;
import com.example.FinalProject.repositories.PostRepository;
import com.example.FinalProject.models.Post;
import com.example.FinalProject.events.dtos.PostLikedEvent;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class LikeService {
    private final PostRepository postRepository;
    private final NotificationEventPublisher notificationEventPublisher;

    public LikeService(PostRepository postRepository,
            NotificationEventPublisher notificationEventPublisher) {
        this.postRepository = postRepository;
        this.notificationEventPublisher = notificationEventPublisher;
    }

    public Post likePost(String postId, String likerId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));
        post.setLikes(post.getLikes() + 1);
        Post updated = postRepository.save(post);

        PostLikedEvent event = new PostLikedEvent(
                UUID.randomUUID().toString(),
                "POST_LIKED",
                LocalDateTime.now(),
                postId,
                likerId,
                post.getAuthorId());
        notificationEventPublisher.sendPostLikedEvent(event);
        return updated;
    }
}