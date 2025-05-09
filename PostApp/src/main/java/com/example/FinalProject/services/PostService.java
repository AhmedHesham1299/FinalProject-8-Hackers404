package com.example.FinalProject.services;

import com.example.FinalProject.criteria.SearchCriteria;
import com.example.FinalProject.models.Post;
import com.example.FinalProject.models.Comment;
import com.example.FinalProject.repositories.PostRepository;
import com.example.FinalProject.events.PostEventPublisher;
import com.example.FinalProject.events.dtos.PostEventPayload;
import com.example.FinalProject.events.dtos.PostCreatedEvent;
import com.example.FinalProject.events.dtos.PostUpdatedEvent;
import com.example.FinalProject.events.dtos.PostDeletedEvent;
import com.example.FinalProject.dtos.PostUpdateRequestDto;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.ArrayList;
import java.util.Optional;

@Service
public class PostService {
    private final PostRepository postRepository;
    

    public PostService(PostRepository postRepository, PostEventPublisher postEventPublisher) {
        this.postRepository = postRepository;
    }
    
    public Post findPost(String postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));
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
        return updated;
    }
    public void deletePost(String postId) {
        Post existing = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));
        postRepository.delete(existing);
        PostEventPayload payload = new PostEventPayload(existing.getId(), existing.getTitle(), existing.getAuthorId(),
                existing.getCreatedAt());
        PostDeletedEvent event = new PostDeletedEvent(UUID.randomUUID().toString(), "POST_DELETED", LocalDateTime.now(),
                payload);
    }

    
    public Post addComment(String postId, String content, int likes, int dislikes) {
        Post post = findPost(postId);
        post.addComment(content, likes, dislikes);
        return postRepository.save(post);
    }
    
    
    public Post addComment(String postId, Comment comment) {
        Post post = findPost(postId);
        post.addComment(comment);
        return postRepository.save(post);
    }
    
    
    public Optional<Comment> findComment(String postId, String content) {
        Post post = findPost(postId);
        return post.findCommentByContent(content);
    }
    
   
    public List<Comment> getComments(String postId) {
        Post post = findPost(postId);
        return post.getComments();
    }
    
   
    
   
    public Post updateComment(String postId, String oldContent, Comment updatedComment) {
        Post post = findPost(postId);
        boolean updated = post.updateComment(oldContent, updatedComment);
        if (updated) {
            return postRepository.save(post);
        }
        return null;
    }
    
   
    public Post deleteComment(String postId, String content) {
        Post post = findPost(postId);
        boolean removed = post.deleteComment(content);
        if (removed) {
            return postRepository.save(post);
        }
        return null;
    }
}