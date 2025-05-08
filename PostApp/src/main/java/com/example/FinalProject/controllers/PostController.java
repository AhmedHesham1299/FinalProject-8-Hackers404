package com.example.FinalProject.controllers;

import com.example.FinalProject.criteria.SearchCriteria;
import com.example.FinalProject.models.Post;
import com.example.FinalProject.services.PostService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public List<Post> searchPosts(@RequestParam String query) {
        return postService.searchPosts(query);
    }

    @GetMapping("/{postId}")
    public Post findPost(@PathVariable String postId) {
        return postService.findPost(postId);
    }

    @PostMapping
    public Post createPost(@RequestBody Post post) {
        return postService.createPost(post);
    }

    @PutMapping("/{postId}")
    public Post updatePost(@PathVariable String postId, @RequestBody Post post) {
        return postService.updatePost(postId, post);
    }

    @DeleteMapping("/{postId}")
    public void deletePost(@PathVariable String postId) {
        postService.deletePost(postId);
    }
    
}