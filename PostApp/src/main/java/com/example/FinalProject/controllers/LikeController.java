package com.example.FinalProject.controllers;

import com.example.FinalProject.models.Like;
import com.example.FinalProject.services.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class LikeController {

    private final LikeService likeService;

    @Autowired
    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    @PostMapping("/posts/{postId}/like")
    public ResponseEntity<?> likePost(@PathVariable String postId, @RequestBody Map<String, String> payload) {
        if (!payload.containsKey("userId")) {
            return ResponseEntity.badRequest().build();
        }
        
        Like like = likeService.togglePostReaction(postId, payload.get("userId"), true);
        return ResponseEntity.ok(like);
    }

    @PostMapping("/posts/{postId}/dislike")
    public ResponseEntity<?> dislikePost(@PathVariable String postId, @RequestBody Map<String, String> payload) {
        if (!payload.containsKey("userId")) {
            return ResponseEntity.badRequest().build();
        }
        
        Like like = likeService.togglePostReaction(postId, payload.get("userId"), false);
        return ResponseEntity.ok(like);
    }

    @PostMapping("/comments/{commentId}/like")
    public ResponseEntity<?> likeComment(@PathVariable String commentId, @RequestBody Map<String, String> payload) {
        if (!payload.containsKey("userId")) {
            return ResponseEntity.badRequest().build();
        }
        
        Like like = likeService.toggleCommentReaction(commentId, payload.get("userId"), true);
        return ResponseEntity.ok(like);
    }

    @PostMapping("/comments/{commentId}/dislike")
    public ResponseEntity<?> dislikeComment(@PathVariable String commentId, @RequestBody Map<String, String> payload) {
        if (!payload.containsKey("userId")) {
            return ResponseEntity.badRequest().build();
        }
        
        Like like = likeService.toggleCommentReaction(commentId, payload.get("userId"), false);
        return ResponseEntity.ok(like);
    }
} 