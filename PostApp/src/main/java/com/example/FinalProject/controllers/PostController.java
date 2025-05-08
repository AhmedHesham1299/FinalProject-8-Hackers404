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
}