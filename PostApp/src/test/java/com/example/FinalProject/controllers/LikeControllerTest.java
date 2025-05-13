package com.example.FinalProject.controllers;

import com.example.FinalProject.models.Like;
import com.example.FinalProject.services.LikeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LikeController.class)
public class LikeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LikeService likeService;

    private ObjectMapper objectMapper;
    private Like testPostLike;
    private Like testCommentLike;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        
        testPostLike = new Like("user123", "post123", "post", true);
        testPostLike.setId("postLike123");
        testPostLike.setCreatedAt(LocalDateTime.now());
        
        testCommentLike = new Like("user123", "comment123", "comment", true);
        testCommentLike.setId("commentLike123");
        testCommentLike.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void likePost_shouldReturnCreatedLike() throws Exception {
        // Given
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("userId", "user123");
        
        when(likeService.togglePostReaction(eq("post123"), eq("user123"), eq(true)))
                .thenReturn(testPostLike);

        // When/Then
        mockMvc.perform(post("/api/posts/post123/like")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("postLike123")))
                .andExpect(jsonPath("$.userId", is("user123")))
                .andExpect(jsonPath("$.targetId", is("post123")))
                .andExpect(jsonPath("$.targetType", is("post")))
                .andExpect(jsonPath("$.like", is(true)));
    }

    @Test
    void likePost_shouldReturnNullWhenRemovingLike() throws Exception {
        // Given
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("userId", "user123");
        
        when(likeService.togglePostReaction(eq("post123"), eq("user123"), eq(true)))
                .thenReturn(null); // When like is removed

        // When/Then
        mockMvc.perform(post("/api/posts/post123/like")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    void likePost_shouldReturnBadRequestWhenMissingUserId() throws Exception {
        // Given
        Map<String, String> emptyRequestBody = new HashMap<>();
        // Missing userId

        // When/Then
        mockMvc.perform(post("/api/posts/post123/like")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emptyRequestBody)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void dislikePost_shouldReturnCreatedDislike() throws Exception {
        // Given
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("userId", "user123");
        
        Like dislike = new Like("user123", "post123", "post", false);
        dislike.setId("postDislike123");
        
        when(likeService.togglePostReaction(eq("post123"), eq("user123"), eq(false)))
                .thenReturn(dislike);

        // When/Then
        mockMvc.perform(post("/api/posts/post123/dislike")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("postDislike123")))
                .andExpect(jsonPath("$.userId", is("user123")))
                .andExpect(jsonPath("$.targetId", is("post123")))
                .andExpect(jsonPath("$.targetType", is("post")))
                .andExpect(jsonPath("$.like", is(false)));
    }

    @Test
    void likeComment_shouldReturnCreatedLike() throws Exception {
        // Given
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("userId", "user123");
        
        when(likeService.toggleCommentReaction(eq("comment123"), eq("user123"), eq(true)))
                .thenReturn(testCommentLike);

        // When/Then
        mockMvc.perform(post("/api/comments/comment123/like")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("commentLike123")))
                .andExpect(jsonPath("$.userId", is("user123")))
                .andExpect(jsonPath("$.targetId", is("comment123")))
                .andExpect(jsonPath("$.targetType", is("comment")))
                .andExpect(jsonPath("$.like", is(true)));
    }

    @Test
    void likePost_shouldHandleNotFoundException() throws Exception {
        // Given
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("userId", "user123");
        
        when(likeService.togglePostReaction(eq("nonexistent"), eq("user123"), eq(true)))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));

        // When/Then
        mockMvc.perform(post("/api/posts/nonexistent/like")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isNotFound());
    }

    @Test
    void dislikeComment_shouldReturnCreatedDislike() throws Exception {
        // Given
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("userId", "user123");
        
        Like commentDislike = new Like("user123", "comment123", "comment", false);
        commentDislike.setId("commentDislike123");
        
        when(likeService.toggleCommentReaction(eq("comment123"), eq("user123"), eq(false)))
                .thenReturn(commentDislike);

        // When/Then
        mockMvc.perform(post("/api/comments/comment123/dislike")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("commentDislike123")))
                .andExpect(jsonPath("$.userId", is("user123")))
                .andExpect(jsonPath("$.targetId", is("comment123")))
                .andExpect(jsonPath("$.targetType", is("comment")))
                .andExpect(jsonPath("$.like", is(false)));
    }
} 