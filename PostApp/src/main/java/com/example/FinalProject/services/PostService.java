package com.example.FinalProject.services;

import com.example.FinalProject.criteria.SearchCriteria;
import com.example.FinalProject.models.Comment;
import com.example.FinalProject.models.Post;
import com.example.FinalProject.repositories.PostRepository;
import com.example.FinalProject.events.PostEventPublisher;
import com.example.FinalProject.events.dtos.PostEventPayload;
import com.example.FinalProject.events.dtos.PostCreatedEvent;
import com.example.FinalProject.events.dtos.PostUpdatedEvent;
import com.example.FinalProject.dtos.PostUpdateRequestDto;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.ArrayList;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final PostEventPublisher postEventPublisher;

    public PostService(PostRepository postRepository, PostEventPublisher postEventPublisher) {
        this.postRepository = postRepository;
        this.postEventPublisher = postEventPublisher;
    }

    public List<Post> searchPosts(SearchCriteria criteria) {
        return postRepository.searchPosts(criteria);
    }

    public Post createPost(Post post) {
        Post saved = postRepository.save(post);
        PostEventPayload payload = new PostEventPayload(saved.getId(), saved.getTitle(), saved.getAuthorId(),
                saved.getCreatedAt());
        PostCreatedEvent event = new PostCreatedEvent(UUID.randomUUID().toString(), "POST_CREATED", LocalDateTime.now(),
                payload);
        postEventPublisher.sendPostCreatedEvent(event);
        return saved;
    }

    public Post updatePost(String postId, PostUpdateRequestDto updateRequest) {
        Post existing = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));
        List<String> updatedFields = new ArrayList<>();
        if (updateRequest.title() != null) {
            existing.setTitle(updateRequest.title());
            updatedFields.add("title");
        }
        if (updateRequest.content() != null) {
            existing.setContent(updateRequest.content());
            updatedFields.add("content");
        }
        if (updateRequest.tags() != null) {
            existing.setTags(updateRequest.tags());
            updatedFields.add("tags");
        }
        Post updated = postRepository.save(existing);
        PostEventPayload payload = new PostEventPayload(updated.getId(), updated.getTitle(), updated.getAuthorId(),
                updated.getCreatedAt());
        PostUpdatedEvent event = new PostUpdatedEvent(UUID.randomUUID().toString(), "POST_UPDATED", LocalDateTime.now(),
                payload, updatedFields);
        postEventPublisher.sendPostUpdatedEvent(event);
        return updated;
    }

    public Post findPost(String postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));
    }

    public void deletePost(String postId) {
        if (!postRepository.existsById(postId)) {
            throw new RuntimeException("Post not found with id: " + postId);
        }
        postRepository.deleteById(postId);
    }

    public Post addComment(String postId, Comment comment) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        post.getComments().add(comment);
        return postRepository.save(post);
    }

    public List<Comment> getComments(String postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        return post.getComments();
    }

    public Post updateComment(String postId, int index, Comment updatedComment) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        if (index < 0 || index >= post.getComments().size())
            throw new IllegalArgumentException("Invalid comment index");
        post.getComments().set(index, updatedComment);
        return postRepository.save(post);
    }

    public Post deleteComment(String postId, int index) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        if (index < 0 || index >= post.getComments().size())
            throw new IllegalArgumentException("Invalid comment index");
        post.getComments().remove(index);
        return postRepository.save(post);
    }
}