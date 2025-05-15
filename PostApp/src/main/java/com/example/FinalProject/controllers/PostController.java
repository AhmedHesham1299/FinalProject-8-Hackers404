package com.example.FinalProject.controllers;

import com.example.FinalProject.criteria.SearchCriteria;
import com.example.FinalProject.models.Comment;
import com.example.FinalProject.models.Post;
import com.example.FinalProject.services.PostService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import com.example.FinalProject.dtos.PostUpdateRequestDto;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/search")

    public List<Post> searchPosts(
            @RequestParam(required = false) String keywords,
            @RequestParam(required = false) List<String> tags,
            @RequestParam(required = false) String authorId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        SearchCriteria criteria = new SearchCriteria(keywords, tags, authorId, startDate, endDate);
        return postService.searchPosts(criteria);
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
    public Post updatePost(@PathVariable String postId, @RequestBody PostUpdateRequestDto updateRequest) {
        return postService.updatePost(postId, updateRequest);
    }

    @DeleteMapping("/{postId}")
    public void deletePost(@PathVariable String postId) {
        postService.deletePost(postId);
    }

    @PostMapping("/{postId}/comments")
    public Post addComment(@PathVariable String postId, @RequestBody Comment comment) {
        return postService.addComment(postId, comment);
    }

    @GetMapping("/{postId}/comments")
    public List<Comment> getComments(@PathVariable String postId) {
        return postService.getComments(postId);
    }

    @PutMapping("/{postId}/comments/{index}")
    public Post updateComment(@PathVariable String postId,
                                              @PathVariable int index,
                                              @RequestBody Comment comment) {
        return postService.updateComment(postId, index, comment);
    }

    @DeleteMapping("/{postId}/comments/{index}")
    public Post deleteComment(@PathVariable String postId, @PathVariable int index) {
        return postService.deleteComment(postId, index);
    }

}