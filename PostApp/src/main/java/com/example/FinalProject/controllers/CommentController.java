package com.example.FinalProject.controllers;

import com.example.FinalProject.models.Comment;
import com.example.FinalProject.services.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api")
public class CommentController {

    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<List<Comment>> getCommentsByPostId(@PathVariable String postId) {
        return ResponseEntity.ok(commentService.getCommentsByPostId(postId));
    }

    @GetMapping("/comments/{id}")
    public ResponseEntity<Comment> getCommentById(@PathVariable String id) {
        return ResponseEntity.ok(commentService.getCommentById(id));
    }

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<Comment> createComment(@PathVariable String postId, 
                                               @RequestBody Map<String, String> payload) {
        if (!payload.containsKey("content") || !payload.containsKey("authorId")) {
            return ResponseEntity.badRequest().build();
        }
        
        Comment comment = new Comment(
                payload.get("content"),
                payload.get("authorId"),
                postId
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(commentService.createComment(comment));
    }

    @PutMapping("/comments/{id}")
    public ResponseEntity<Comment> updateComment(@PathVariable String id, @RequestBody Map<String, String> payload) {
        if (!payload.containsKey("content")) {
            return ResponseEntity.badRequest().build();
        }
        
        Comment comment = new Comment();
        comment.setContent(payload.get("content"));
        
        return ResponseEntity.ok(commentService.updateComment(id, comment));
    }

    @DeleteMapping("/comments/{id}")
    public ResponseEntity<Map<String, String>> deleteComment(@PathVariable String id) {
        commentService.deleteComment(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Comment deleted successfully");
        response.put("commentId", id);
        return ResponseEntity.ok(response);
    }
} 