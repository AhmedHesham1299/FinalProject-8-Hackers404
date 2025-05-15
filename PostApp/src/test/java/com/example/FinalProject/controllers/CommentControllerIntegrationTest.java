package com.example.FinalProject.controllers;

import com.example.FinalProject.models.Comment;
import com.example.FinalProject.services.CommentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CommentController.class)
public class CommentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService commentService;

    private ObjectMapper objectMapper;
    private Comment testComment1;
    private Comment testComment2;
    private String testPostId = "post123";

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        testComment1 = new Comment("First comment content", "user1", testPostId);
        testComment1.setId("comment1");
        testComment1.setCreatedAt(LocalDateTime.now().minusDays(1));
        testComment1.setLikes(5);
        testComment1.setDislikes(1);

        testComment2 = new Comment("Second comment content", "user2", testPostId);
        testComment2.setId("comment2");
        testComment2.setCreatedAt(LocalDateTime.now());
        testComment2.setLikes(10);
        testComment2.setDislikes(2);
    }

    // Tests will be moved here

    @Test
    void createComment_shouldReturnCreatedComment() throws Exception {
        // Given
        Map<String, String> payload = new HashMap<>();
        payload.put("content", "New creative comment");
        payload.put("authorId", "userCreator");

        Comment createdComment = new Comment("New creative comment", "userCreator", testPostId);
        createdComment.setId("commentNew");
        createdComment.setCreatedAt(LocalDateTime.now());

        when(commentService.createComment(any(Comment.class))).thenReturn(createdComment);

        // When/Then
        mockMvc.perform(post("/api/posts/" + testPostId + "/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is("commentNew")))
                .andExpect(jsonPath("$.content", is("New creative comment")))
                .andExpect(jsonPath("$.authorId", is("userCreator")))
                .andExpect(jsonPath("$.postId", is(testPostId)));
    }

    @Test
    void getCommentsByPostId_shouldReturnCommentsList() throws Exception {
        // Given
        when(commentService.getCommentsByPostId(testPostId)).thenReturn(Arrays.asList(testComment1, testComment2));

        // When/Then
        mockMvc.perform(get("/api/posts/" + testPostId + "/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is("comment1")))
                .andExpect(jsonPath("$[0].content", is("First comment content")))
                .andExpect(jsonPath("$[1].id", is("comment2")))
                .andExpect(jsonPath("$[1].content", is("Second comment content")));
    }

    @Test
    void updateComment_shouldReturnUpdatedComment() throws Exception {
        // Given
        Map<String, String> payload = new HashMap<>();
        payload.put("content", "Updated comment text");

        Comment updatedComment = new Comment("Updated comment text", testComment1.getAuthorId(),
                testComment1.getPostId());
        updatedComment.setId(testComment1.getId());
        updatedComment.setCreatedAt(testComment1.getCreatedAt());
        updatedComment.setLikes(testComment1.getLikes());
        updatedComment.setDislikes(testComment1.getDislikes());

        when(commentService.updateComment(eq(testComment1.getId()), any(Comment.class))).thenReturn(updatedComment);

        // When/Then
        mockMvc.perform(put("/api/comments/" + testComment1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testComment1.getId())))
                .andExpect(jsonPath("$.content", is("Updated comment text")));
    }

    @Test
    void deleteComment_shouldReturnNoContent() throws Exception {
        // Given
        // No specific mock needed for commentService.deleteComment as it's void and we
        // verify its call

        // When/Then
        mockMvc.perform(delete("/api/comments/" + testComment1.getId()))
                .andExpect(status().isNoContent());

        verify(commentService).deleteComment(testComment1.getId());
    }
}