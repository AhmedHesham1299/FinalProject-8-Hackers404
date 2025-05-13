package com.example.FinalProject.services;

import com.example.FinalProject.events.LikeEventPublisher;
import com.example.FinalProject.events.dtos.LikeEvent;
import com.example.FinalProject.models.Comment;
import com.example.FinalProject.models.Like;
import com.example.FinalProject.models.Post;
import com.example.FinalProject.observers.PostObservable;
import com.example.FinalProject.repositories.CommentRepository;
import com.example.FinalProject.repositories.LikeRepository;
import com.example.FinalProject.repositories.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final LikeEventPublisher likeEventPublisher;
    private final PostObservable postObservable;

    @Autowired
    public LikeService(LikeRepository likeRepository,
                       PostRepository postRepository,
                       CommentRepository commentRepository,
                       LikeEventPublisher likeEventPublisher,
                       PostObservable postObservable) {
        this.likeRepository = likeRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.likeEventPublisher = likeEventPublisher;
        this.postObservable = postObservable;
    }

    /**
     * Toggle a like/dislike on a post
     */
    @CacheEvict(value = "posts", key = "#postId")
    public Like togglePostReaction(String postId, String userId, boolean isLike) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));

        // Check if user already reacted to this post
        Optional<Like> existingLike = likeRepository.findByUserIdAndTargetIdAndTargetType(userId, postId, "post");

        if (existingLike.isPresent()) {
            Like like = existingLike.get();
            
            // If the reaction type is the same, remove it
            if (like.isLike() == isLike) {
                // Update post counts
                if (isLike) {
                    post.setLikes(post.getLikes() - 1);
                } else {
                    post.setDislikes(post.getDislikes() - 1);
                }
                postRepository.save(post);
                
                // Delete the like
                likeRepository.delete(like);
                return null;
            } else {
                // Toggle the reaction type
                like.setLike(isLike);
                
                // Update post counts
                if (isLike) {
                    post.setLikes(post.getLikes() + 1);
                    post.setDislikes(post.getDislikes() - 1);
                } else {
                    post.setLikes(post.getLikes() - 1);
                    post.setDislikes(post.getDislikes() + 1);
                }
                postRepository.save(post);
                
                // Update the like
                Like updatedLike = likeRepository.save(like);
                
                // Publish event
                publishLikeEvent(updatedLike);
                
                // Notify observers
                postObservable.notifyPostLiked(post, updatedLike);
                
                return updatedLike;
            }
        } else {
            // Create new reaction
            Like newLike = new Like(userId, postId, "post", isLike);
            
            // Update post counts
            if (isLike) {
                post.setLikes(post.getLikes() + 1);
            } else {
                post.setDislikes(post.getDislikes() + 1);
            }
            postRepository.save(post);
            
            // Save the new like
            Like savedLike = likeRepository.save(newLike);
            
            // Publish event
            publishLikeEvent(savedLike);
            
            // Notify observers
            postObservable.notifyPostLiked(post, savedLike);
            
            return savedLike;
        }
    }

    /**
     * Toggle a like/dislike on a comment
     */
    @CacheEvict(value = "comments", key = "#comment.postId")
    public Like toggleCommentReaction(String commentId, String userId, boolean isLike) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found"));

        // Check if user already reacted to this comment
        Optional<Like> existingLike = likeRepository.findByUserIdAndTargetIdAndTargetType(userId, commentId, "comment");

        if (existingLike.isPresent()) {
            Like like = existingLike.get();
            
            // If the reaction type is the same, remove it
            if (like.isLike() == isLike) {
                // Update comment counts
                if (isLike) {
                    comment.setLikes(comment.getLikes() - 1);
                } else {
                    comment.setDislikes(comment.getDislikes() - 1);
                }
                commentRepository.save(comment);
                
                // Delete the like
                likeRepository.delete(like);
                return null;
            } else {
                // Toggle the reaction type
                like.setLike(isLike);
                
                // Update comment counts
                if (isLike) {
                    comment.setLikes(comment.getLikes() + 1);
                    comment.setDislikes(comment.getDislikes() - 1);
                } else {
                    comment.setLikes(comment.getLikes() - 1);
                    comment.setDislikes(comment.getDislikes() + 1);
                }
                commentRepository.save(comment);
                
                // Update the like
                Like updatedLike = likeRepository.save(like);
                
                // Publish event
                publishLikeEvent(updatedLike);
                
                // Notify observers
                postObservable.notifyCommentLiked(comment, updatedLike);
                
                return updatedLike;
            }
        } else {
            // Create new reaction
            Like newLike = new Like(userId, commentId, "comment", isLike);
            
            // Update comment counts
            if (isLike) {
                comment.setLikes(comment.getLikes() + 1);
            } else {
                comment.setDislikes(comment.getDislikes() + 1);
            }
            commentRepository.save(comment);
            
            // Save the new like
            Like savedLike = likeRepository.save(newLike);
            
            // Publish event
            publishLikeEvent(savedLike);
            
            // Notify observers
            postObservable.notifyCommentLiked(comment, savedLike);
            
            return savedLike;
        }
    }

    private void publishLikeEvent(Like like) {
        likeEventPublisher.sendLikeEvent(
                new LikeEvent(like.getId(), like.getUserId(), like.getTargetId(), like.getTargetType(), like.isLike())
        );
    }
} 