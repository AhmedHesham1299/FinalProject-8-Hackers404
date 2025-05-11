package com.example.FinalProject.services;

import org.springframework.stereotype.Service;
import com.example.FinalProject.repositories.PostRepository;
import com.example.FinalProject.models.Post;
import com.example.FinalProject.models.Comment;
import com.example.FinalProject.events.dtos.CommentAddedEvent;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class CommentService {
    private final PostRepository postRepository;
    private final NotificationEventPublisher notificationEventPublisher;

    public CommentService(PostRepository postRepository,
            NotificationEventPublisher notificationEventPublisher) {
        this.postRepository = postRepository;
        this.notificationEventPublisher = notificationEventPublisher;
    }

    public Comment addCommentToPost(String postId, String commenterId, String text, String postAuthorId) {
        // Create and save comment
        Comment comment = new Comment(text, 0, 0);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));
        post.getComments().add(comment);
        postRepository.save(post);

        // Prepare and send event
        String preview = text.length() > 100 ? text.substring(0, 100) : text;
        CommentAddedEvent event = new CommentAddedEvent(
                UUID.randomUUID().toString(),
                "COMMENT_ADDED",
                LocalDateTime.now(),
                postId,
                comment.getCommentId(),
                commenterId,
                postAuthorId,
                preview);
        notificationEventPublisher.sendCommentAddedEvent(event);
        return comment;
    }
}