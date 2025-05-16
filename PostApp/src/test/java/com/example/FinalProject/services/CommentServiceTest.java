package com.example.FinalProject.services;

import com.example.FinalProject.events.CommentEventPublisher;
import com.example.FinalProject.events.dtos.CommentCreatedEvent;
import com.example.FinalProject.events.dtos.CommentUpdatedEvent;
import com.example.FinalProject.models.Comment;
import com.example.FinalProject.models.Post;
import com.example.FinalProject.observers.PostObservable;
import com.example.FinalProject.repositories.CommentRepository;
import com.example.FinalProject.repositories.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentEventPublisher commentEventPublisher;

    @Mock
    private PostObservable postObservable;

    @Mock
    private MongoTemplate mongoTemplate;

    private CommentService commentService;
    private Comment testComment;
    private Post testPost;

    @BeforeEach
    void setUp() {
        commentService = new CommentService(commentRepository, postRepository, mongoTemplate,
                commentEventPublisher, postObservable);

        // Setup test post
        testPost = new Post();
        testPost.setId("post123");
        testPost.setTitle("Test Post");
        testPost.setContent("Test Content");
        testPost.setAuthorId("author123");
        testPost.setCreatedAt(LocalDateTime.now());

        // Setup test comment
        testComment = new Comment("Test comment content", "author456", "post123");
        testComment.setId("comment123");
        testComment.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void getCommentsByPostId_ShouldReturnCommentsList() {
        // Given
        when(postRepository.existsById("post123")).thenReturn(true);
        List<Comment> comments = Arrays.asList(testComment);
        when(commentRepository.findByPostId("post123")).thenReturn(comments);

        // When
        List<Comment> result = commentService.getCommentsByPostId("post123");

        // Then
        assertEquals(1, result.size());
        assertEquals("Test comment content", result.get(0).getContent());
        verify(commentRepository).findByPostId("post123");
    }

    @Test
    void getCommentById_ShouldReturnComment_WhenExists() {
        // Given
        when(commentRepository.findById("comment123")).thenReturn(Optional.of(testComment));

        // When
        Comment result = commentService.getCommentById("comment123");

        // Then
        assertEquals("Test comment content", result.getContent());
        assertEquals("author456", result.getAuthorId());
        verify(commentRepository).findById("comment123");
    }

    @Test
    void getCommentById_ShouldThrowException_WhenNotExists() {
        // Given
        when(commentRepository.findById("nonexistent")).thenReturn(Optional.empty());

        // When & Then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            commentService.getCommentById("nonexistent");
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Comment not found"));
    }

    @Test
    void createComment_ShouldSaveAndPublish() {
        // Given
        when(postRepository.findById("post123")).thenReturn(Optional.of(testPost));
        when(commentRepository.save(any(Comment.class))).thenReturn(testComment);

        // When
        Comment result = commentService.createComment(testComment);

        // Then
        assertEquals("Test comment content", result.getContent());

        verify(postRepository).findById("post123");
        verify(commentRepository).save(testComment);

        // Verify event publishing
        ArgumentCaptor<CommentCreatedEvent> eventCaptor = ArgumentCaptor.forClass(CommentCreatedEvent.class);
        verify(commentEventPublisher).sendCommentCreatedEvent(eventCaptor.capture());

        CommentCreatedEvent capturedEvent = eventCaptor.getValue();
        assertEquals("comment123", capturedEvent.getCommentId());
        assertEquals("post123", capturedEvent.getPostId());

        // Verify observer notification
        verify(postObservable).notifyCommentAdded(testPost, testComment);
    }

    @Test
    void createComment_ShouldThrowException_WhenPostNotFound() {
        // Given
        when(postRepository.findById("nonexistent")).thenReturn(Optional.empty());
        Comment comment = new Comment("Test", "author456", "nonexistent");

        // When & Then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            commentService.createComment(comment);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Post not found"));

        verify(commentRepository, never()).save(any(Comment.class));
        verify(commentEventPublisher, never()).sendCommentCreatedEvent(any(CommentCreatedEvent.class));
        verify(postObservable, never()).notifyCommentAdded(any(Post.class), any(Comment.class));
    }

    @Test
    void updateComment_ShouldUpdateContentAndPublish() {
        // Given
        when(commentRepository.findById("comment123")).thenReturn(Optional.of(testComment));
        when(postRepository.findById("post123")).thenReturn(Optional.of(testPost));

        Comment updateRequest = new Comment();
        updateRequest.setContent("Updated content");

        Comment updatedComment = new Comment("Updated content", "author456", "post123");
        updatedComment.setId("comment123");

        when(commentRepository.save(any(Comment.class))).thenReturn(updatedComment);

        // When
        Comment result = commentService.updateComment("comment123", updateRequest);

        // Then
        assertEquals("Updated content", result.getContent());

        // Verify the right fields were updated
        ArgumentCaptor<Comment> commentCaptor = ArgumentCaptor.forClass(Comment.class);
        verify(commentRepository).save(commentCaptor.capture());

        Comment savedComment = commentCaptor.getValue();
        assertEquals("Updated content", savedComment.getContent());
        assertEquals("author456", savedComment.getAuthorId()); // Should remain unchanged

        // Verify event publishing
        ArgumentCaptor<CommentUpdatedEvent> eventCaptor = ArgumentCaptor.forClass(CommentUpdatedEvent.class);
        verify(commentEventPublisher).sendCommentUpdatedEvent(eventCaptor.capture());

        CommentUpdatedEvent capturedEvent = eventCaptor.getValue();
        assertEquals("comment123", capturedEvent.getCommentId());
    }

    @Test
    void deleteComment_ShouldRemoveComment() {
        // Given
        when(commentRepository.findById("comment123")).thenReturn(Optional.of(testComment));

        // When
        commentService.deleteComment("comment123");

        // Then
        verify(commentRepository).findById("comment123");
        verify(commentRepository).deleteById("comment123");
    }

    @Test
    void deleteComment_ShouldThrowException_WhenNotExists() {
        // Given
        when(commentRepository.findById("nonexistent")).thenReturn(Optional.empty());

        // When & Then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            commentService.deleteComment("nonexistent");
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Comment not found"));

        verify(commentRepository, never()).deleteById(anyString());
    }
}