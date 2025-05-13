package com.example.FinalProject.controllers;

import com.example.FinalProject.models.Comment;
import com.example.FinalProject.services.CommentService;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
public class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService commentService;

    private ObjectMapper objectMapper;
    private Comment testComment;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        
        testComment = new Comment("Test comment content", "author123", "post123");
        testComment.setId("comment123");
        testComment.setCreatedAt(LocalDateTime.now());
        testComment.setLikes(5);
        testComment.setDislikes(2);
    }

    @Test
    void getCommentsByPostId_shouldReturnCommentsList() throws Exception {
        // Given
        when(commentService.getCommentsByPostId("post123"))
                .thenReturn(Arrays.asList(testComment));

        // When/Then
        mockMvc.perform(get("/api/posts/post123/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is("comment123")))
                .andExpect(jsonPath("$[0].content", is("Test comment content")))
                .andExpect(jsonPath("$[0].authorId", is("author123")))
                .andExpect(jsonPath("$[0].likes", is(5)))
                .andExpect(jsonPath("$[0].dislikes", is(2)));
    }

    @Test
    void getCommentById_shouldReturnComment() throws Exception {
        // Given
        when(commentService.getCommentById("comment123"))
                .thenReturn(testComment);

        // When/Then
        mockMvc.perform(get("/api/comments/comment123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("comment123")))
                .andExpect(jsonPath("$.content", is("Test comment content")))
                .andExpect(jsonPath("$.authorId", is("author123")))
                .andExpect(jsonPath("$.postId", is("post123")));
    }

    @Test
    void getCommentById_shouldReturnNotFound_whenCommentDoesNotExist() throws Exception {
        // Given
        when(commentService.getCommentById("nonexistent"))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found"));

        // When/Then
        mockMvc.perform(get("/api/comments/nonexistent"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createComment_shouldReturnCreatedComment() throws Exception {
        // Given
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("content", "New comment content");
        requestBody.put("authorId", "author123");
        
        when(commentService.createComment(any(Comment.class)))
                .thenReturn(testComment);

        // When/Then
        mockMvc.perform(post("/api/posts/post123/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is("comment123")))
                .andExpect(jsonPath("$.content", is("Test comment content")));
    }

    @Test
    void createComment_shouldReturnBadRequest_whenMissingRequiredFields() throws Exception {
        // Given
        Map<String, String> incompleteRequestBody = new HashMap<>();
        incompleteRequestBody.put("content", "New comment content");
        // Missing authorId

        // When/Then
        mockMvc.perform(post("/api/posts/post123/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(incompleteRequestBody)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateComment_shouldReturnUpdatedComment() throws Exception {
        // Given
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("content", "Updated comment content");
        
        Comment updatedComment = new Comment("Updated comment content", "author123", "post123");
        updatedComment.setId("comment123");
        
        when(commentService.updateComment(eq("comment123"), any(Comment.class)))
                .thenReturn(updatedComment);

        // When/Then
        mockMvc.perform(put("/api/comments/comment123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("comment123")))
                .andExpect(jsonPath("$.content", is("Updated comment content")));
    }

    @Test
    void updateComment_shouldReturnBadRequest_whenMissingContent() throws Exception {
        // Given
        Map<String, String> emptyRequestBody = new HashMap<>();
        // Missing content

        // When/Then
        mockMvc.perform(put("/api/comments/comment123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emptyRequestBody)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteComment_shouldReturnNoContent() throws Exception {
        // When/Then
        mockMvc.perform(delete("/api/comments/comment123"))
                .andExpect(status().isNoContent());
        
        verify(commentService).deleteComment("comment123");
    }
} 