package com.example.FinalProject.services;

import com.example.FinalProject.events.CommentEventPublisher;
import com.example.FinalProject.events.dtos.CommentCreatedEvent;
import com.example.FinalProject.events.dtos.CommentUpdatedEvent;
import com.example.FinalProject.models.Comment;
import com.example.FinalProject.models.Post;
import com.example.FinalProject.observers.PostObservable;
import com.example.FinalProject.repositories.CommentRepository;
import com.example.FinalProject.repositories.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final CommentEventPublisher commentEventPublisher;
    private final PostObservable postObservable;

    @Autowired
    public CommentService(CommentRepository commentRepository, 
                          PostRepository postRepository,
                          CommentEventPublisher commentEventPublisher,
                          PostObservable postObservable) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.commentEventPublisher = commentEventPublisher;
        this.postObservable = postObservable;
    }

    @Cacheable(value = "comments", key = "#postId")
    public List<Comment> getCommentsByPostId(String postId) {
        return commentRepository.findByPostId(postId);
    }

    public Comment getCommentById(String id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found"));
    }

    @CacheEvict(value = "comments", key = "#comment.postId")
    public Comment createComment(Comment comment) {
        // Verify that the post exists
        Post post = postRepository.findById(comment.getPostId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));
        
        Comment savedComment = commentRepository.save(comment);
        if (post.getComments() == null) {
            post.setComments(new java.util.ArrayList<>());
        }
        post.getComments().add(savedComment);
        postRepository.save(post);
        // Publish event
        commentEventPublisher.sendCommentCreatedEvent(
                new CommentCreatedEvent(savedComment.getId(), savedComment.getPostId(), savedComment.getAuthorId()));
        
        // Notify observers (for in-app notifications)
        postObservable.notifyCommentAdded(post, savedComment);
        
        return savedComment;
    }

    @CacheEvict(value = "comments", key = "#result.postId")
    public Comment updateComment(String id, Comment comment) {
        Comment existingComment = getCommentById(id);
        
        // Only update content, not other fields
        existingComment.setContent(comment.getContent());
        
        Comment updatedComment = commentRepository.save(existingComment);
        Post post = postRepository.findById(updatedComment.getPostId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));
        if (post.getComments() != null) {
            for (int i = 0; i < post.getComments().size(); i++) {
                Comment c = post.getComments().get(i);
                if (c.getId().equals(updatedComment.getId())) {
                    post.getComments().set(i, updatedComment);
                    break;
                }
            }
        } else {
            post.setComments(new java.util.ArrayList<>());
            post.getComments().add(updatedComment);
        }
        postRepository.save(post);
        // Publish event
        commentEventPublisher.sendCommentUpdatedEvent(
                new CommentUpdatedEvent(updatedComment.getId(), updatedComment.getPostId(), updatedComment.getAuthorId()));
        
        return updatedComment;
    }

    @CacheEvict(value = "comments", key = "#root.target.getCommentById(#id).postId", beforeInvocation = true)
    public void deleteComment(String id) {
        Comment comment = getCommentById(id);
        String postId = comment.getPostId();
        
        commentRepository.deleteById(id);
    }
} 