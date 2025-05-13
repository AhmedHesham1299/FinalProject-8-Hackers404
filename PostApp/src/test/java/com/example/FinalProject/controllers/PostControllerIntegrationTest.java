package com.example.FinalProject.controllers;

import com.example.FinalProject.criteria.SearchCriteria;
import com.example.FinalProject.dtos.PostUpdateRequestDto;
import com.example.FinalProject.models.Comment;
import com.example.FinalProject.models.Post;
import com.example.FinalProject.services.PostService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PostController.class)
public class PostControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostService postService;

    private ObjectMapper objectMapper;
    private Post testPost;
    private Post testPost2;
    private Comment testComment;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // Set up test posts
        testPost = Post.builder("Test Post", "Test Content", "user123")
                .tags(Arrays.asList("test", "unit"))
                .createdAt(LocalDateTime.now())
                .build();
        testPost.setId("post123");

        testPost2 = Post.builder("Another Test", "More Content", "user456")
                .tags(Arrays.asList("spring", "mongodb"))
                .createdAt(LocalDateTime.now().minusDays(1))
                .build();
        testPost2.setId("post456");

        // Create test comment
        testComment = new Comment("Test comment", "user123", "post123");
        testComment.setLikes(5);
        testComment.setDislikes(2);
        testPost.setComments(Collections.singletonList(testComment));
    }

    @Test
    void searchPosts_shouldReturnMatchingPosts() throws Exception {
        // Given
        when(postService.searchPosts(any(SearchCriteria.class)))
                .thenReturn(Arrays.asList(testPost, testPost2));

        // When/Then
        mockMvc.perform(get("/api/posts/search")
                .param("keywords", "test")
                .param("tags", "unit", "spring")
                .param("authorId", "user123")
                .param("startDate", LocalDate.now().minusDays(7).toString())
                .param("endDate", LocalDate.now().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is("post123")))
                .andExpect(jsonPath("$[0].title", is("Test Post")))
                .andExpect(jsonPath("$[1].id", is("post456")))
                .andExpect(jsonPath("$[1].title", is("Another Test")));
    }

    @Test
    void findPost_shouldReturnPostById() throws Exception {
        // Given
        when(postService.findPost("post123")).thenReturn(testPost);

        // When/Then
        mockMvc.perform(get("/api/posts/post123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("post123")))
                .andExpect(jsonPath("$.title", is("Test Post")))
                .andExpect(jsonPath("$.content", is("Test Content")))
                .andExpect(jsonPath("$.authorId", is("user123")))
                .andExpect(jsonPath("$.tags", hasSize(2)))
                .andExpect(jsonPath("$.tags[0]", is("test")))
                .andExpect(jsonPath("$.tags[1]", is("unit")));
    }

    @Test
    void createPost_shouldReturnCreatedPost() throws Exception {
        // Given
        when(postService.createPost(any(Post.class))).thenReturn(testPost);

        // When/Then
        mockMvc.perform(post("/api/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testPost)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("post123")))
                .andExpect(jsonPath("$.title", is("Test Post")))
                .andExpect(jsonPath("$.content", is("Test Content")))
                .andExpect(jsonPath("$.authorId", is("user123")));
    }

    @Test
    void updatePost_shouldReturnUpdatedPost() throws Exception {
        // Given
        PostUpdateRequestDto updateRequest = new PostUpdateRequestDto("Updated Title", "Updated Content", Arrays.asList("updated", "tag"));
        
        Post updatedPost = Post.builder("Updated Title", "Updated Content", "user123")
                .tags(Arrays.asList("updated", "tag"))
                .createdAt(LocalDateTime.now())
                .build();
        updatedPost.setId("post123");
        
        when(postService.updatePost(eq("post123"), any(PostUpdateRequestDto.class))).thenReturn(updatedPost);

        // When/Then
        mockMvc.perform(put("/api/posts/post123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("post123")))
                .andExpect(jsonPath("$.title", is("Updated Title")))
                .andExpect(jsonPath("$.content", is("Updated Content")))
                .andExpect(jsonPath("$.tags[0]", is("updated")))
                .andExpect(jsonPath("$.tags[1]", is("tag")));
    }

    @Test
    void deletePost_shouldReturnNoContent() throws Exception {
        // Given/When/Then
        mockMvc.perform(delete("/api/posts/post123"))
                .andExpect(status().isOk());
        
        verify(postService).deletePost("post123");
    }

    @Test
    void addComment_shouldReturnUpdatedPost() throws Exception {
        // Given
        Comment newComment = new Comment("New comment", "user456", "post123");
        Post postWithComment = Post.builder("Test Post", "Test Content", "user123")
                .tags(Arrays.asList("test", "unit"))
                .createdAt(LocalDateTime.now())
                .build();
        postWithComment.setId("post123");
        postWithComment.setComments(Arrays.asList(testComment, newComment));
        
        when(postService.addComment(eq("post123"), any(Comment.class))).thenReturn(postWithComment);

        // When/Then
        mockMvc.perform(post("/api/posts/post123/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newComment)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("post123")))
                .andExpect(jsonPath("$.comments", hasSize(2)))
                .andExpect(jsonPath("$.comments[1].content", is("New comment")));
    }

    @Test
    void getComments_shouldReturnCommentsList() throws Exception {
        // Given
        when(postService.getComments("post123")).thenReturn(Collections.singletonList(testComment));

        // When/Then
        mockMvc.perform(get("/api/posts/post123/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].content", is("Test comment")))
                .andExpect(jsonPath("$[0].likes", is(5)))
                .andExpect(jsonPath("$[0].dislikes", is(2)));
    }

    @Test
    void updateComment_shouldReturnUpdatedPost() throws Exception {
        // Given
        Comment updatedComment = new Comment("Updated comment", "user123", "post123");
        updatedComment.setLikes(10);
        updatedComment.setDislikes(3);
        
        Post postWithUpdatedComment = Post.builder("Test Post", "Test Content", "user123")
                .tags(Arrays.asList("test", "unit"))
                .createdAt(LocalDateTime.now())
                .build();
        postWithUpdatedComment.setId("post123");
        postWithUpdatedComment.setComments(Collections.singletonList(updatedComment));
        
        when(postService.updateComment(eq("post123"), eq(0), any(Comment.class))).thenReturn(postWithUpdatedComment);

        // When/Then
        mockMvc.perform(put("/api/posts/post123/comments/0")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedComment)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("post123")))
                .andExpect(jsonPath("$.comments[0].content", is("Updated comment")))
                .andExpect(jsonPath("$.comments[0].likes", is(10)))
                .andExpect(jsonPath("$.comments[0].dislikes", is(3)));
    }

    @Test
    void deleteComment_shouldReturnUpdatedPost() throws Exception {
        // Given
        Post postWithNoComments = Post.builder("Test Post", "Test Content", "user123")
                .tags(Arrays.asList("test", "unit"))
                .createdAt(LocalDateTime.now())
                .build();
        postWithNoComments.setId("post123");
        postWithNoComments.setComments(Collections.emptyList());
        
        when(postService.deleteComment("post123", 0)).thenReturn(postWithNoComments);

        // When/Then
        mockMvc.perform(delete("/api/posts/post123/comments/0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("post123")))
                .andExpect(jsonPath("$.comments", hasSize(0)));
    }
} 