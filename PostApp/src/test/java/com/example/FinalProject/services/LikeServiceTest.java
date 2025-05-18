package com.example.FinalProject.services;

import com.example.FinalProject.events.LikeEventPublisher;
import com.example.FinalProject.events.dtos.LikeEvent;
import com.example.FinalProject.models.Comment;
import com.example.FinalProject.models.Like;
import com.example.FinalProject.models.Post;
import com.example.FinalProject.observers.PostObservable;
import com.example.FinalProject.repositories.CommentRepository;
import com.example.FinalProject.repositories.LikeRepository;
import com.example.FinalProject.repositories.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LikeServiceTest {

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private LikeEventPublisher likeEventPublisher;

    @Mock
    private PostObservable postObservable;

    private LikeService likeService;
    private Post testPost;
    private Comment testComment;
    private Like testLike;

    @BeforeEach
    void setUp() {
        likeService = new LikeService(likeRepository, postRepository, commentRepository,
                likeEventPublisher, postObservable);

        // Setup test post
        testPost = new Post();
        testPost.setId("post123");
        testPost.setTitle("Test Post");
        testPost.setContent("Test Content");
        testPost.setAuthorId("author123");
        testPost.setLikes(5);
        testPost.setDislikes(2);
        testPost.setCreatedAt(LocalDateTime.now());

        // Setup test comment
        testComment = new Comment("Test comment content", "author456", "post123");
        testComment.setId("comment123");
        testComment.setLikes(3);
        testComment.setDislikes(1);
        testComment.setCreatedAt(LocalDateTime.now());

        // Setup test like
        testLike = new Like("user123", "post123", "post", true);
        testLike.setId("like123");
        testLike.setCreatedAt(LocalDateTime.now());
    }

    // POST LIKE/DISLIKE TESTS

    @Test
    void togglePostReaction_ShouldCreateNewLike_WhenNoExistingReaction() {
        // Given
        when(postRepository.findById("post123")).thenReturn(Optional.of(testPost));
        when(likeRepository.findByUserIdAndTargetIdAndTargetType("user123", "post123", "post"))
                .thenReturn(Optional.empty());
        when(likeRepository.save(any(Like.class))).thenReturn(testLike);

        // Initial counts
        int initialLikes = testPost.getLikes();

        // When
        Like result = likeService.togglePostReaction("post123", "user123", true);

        // Then
        assertNotNull(result);
        assertTrue(result.isLike());
        assertEquals("user123", result.getUserId());

        // Verify post count was incremented
        ArgumentCaptor<Post> postCaptor = ArgumentCaptor.forClass(Post.class);
        verify(postRepository).save(postCaptor.capture());

        Post savedPost = postCaptor.getValue();
        assertEquals(initialLikes + 1, savedPost.getLikes());

        // Verify event was published
        verify(likeEventPublisher).sendLikeEvent(any(LikeEvent.class));

        // Verify observer was notified
        verify(postObservable).notifyPostLiked(testPost, testLike);
    }

    @Test
    void togglePostReaction_ShouldCreateNewDislike_WhenNoExistingReaction() {
        // Given
        when(postRepository.findById("post123")).thenReturn(Optional.of(testPost));
        when(likeRepository.findByUserIdAndTargetIdAndTargetType("user123", "post123", "post"))
                .thenReturn(Optional.empty());

        Like dislike = new Like("user123", "post123", "post", false);
        dislike.setId("like123");
        when(likeRepository.save(any(Like.class))).thenReturn(dislike);

        // Initial counts
        int initialDislikes = testPost.getDislikes();

        // When
        Like result = likeService.togglePostReaction("post123", "user123", false);

        // Then
        assertNotNull(result);
        assertFalse(result.isLike());

        // Verify post count was incremented
        ArgumentCaptor<Post> postCaptor = ArgumentCaptor.forClass(Post.class);
        verify(postRepository).save(postCaptor.capture());

        Post savedPost = postCaptor.getValue();
        assertEquals(initialDislikes + 1, savedPost.getDislikes());
    }

    @Test
    void togglePostReaction_ShouldRemoveLike_WhenSameReactionExists() {
        // Given
        when(postRepository.findById("post123")).thenReturn(Optional.of(testPost));
        when(likeRepository.findByUserIdAndTargetIdAndTargetType("user123", "post123", "post"))
                .thenReturn(Optional.of(testLike)); // Existing like

        // Initial counts
        int initialLikes = testPost.getLikes();

        // When
        Like result = likeService.togglePostReaction("post123", "user123", true);

        // Then
        assertNull(result); // Should return null when removing

        // Verify like was deleted
        verify(likeRepository).delete(testLike);

        // Verify post count was decremented
        ArgumentCaptor<Post> postCaptor = ArgumentCaptor.forClass(Post.class);
        verify(postRepository).save(postCaptor.capture());

        Post savedPost = postCaptor.getValue();
        assertEquals(initialLikes - 1, savedPost.getLikes());

        // Verify no event was published
        verify(likeEventPublisher, never()).sendLikeEvent(any(LikeEvent.class));

        // Verify observer was not notified
        verify(postObservable, never()).notifyPostLiked(any(), any());
    }

    @Test
    void togglePostReaction_ShouldToggleFromLikeToDislike() {
        // Given
        when(postRepository.findById("post123")).thenReturn(Optional.of(testPost));
        when(likeRepository.findByUserIdAndTargetIdAndTargetType("user123", "post123", "post"))
                .thenReturn(Optional.of(testLike)); // Existing like (isLike=true)

        // Setup the toggled like
        Like toggledLike = new Like("user123", "post123", "post", false);
        toggledLike.setId("like123");
        when(likeRepository.save(any(Like.class))).thenReturn(toggledLike);

        // Initial counts
        int initialLikes = testPost.getLikes();
        int initialDislikes = testPost.getDislikes();

        // When
        Like result = likeService.togglePostReaction("post123", "user123", false);

        // Then
        assertNotNull(result);
        assertFalse(result.isLike()); // Should now be dislike

        // Verify like was updated
        ArgumentCaptor<Like> likeCaptor = ArgumentCaptor.forClass(Like.class);
        verify(likeRepository).save(likeCaptor.capture());

        Like savedLike = likeCaptor.getValue();
        assertFalse(savedLike.isLike());

        // Verify post counts were updated correctly
        ArgumentCaptor<Post> postCaptor = ArgumentCaptor.forClass(Post.class);
        verify(postRepository).save(postCaptor.capture());

        Post savedPost = postCaptor.getValue();
        assertEquals(initialLikes - 1, savedPost.getLikes()); // Like count decremented
        assertEquals(initialDislikes + 1, savedPost.getDislikes()); // Dislike count incremented

        // Verify event was published for the toggle
        verify(likeEventPublisher).sendLikeEvent(any(LikeEvent.class));

        // Verify observer was notified
        verify(postObservable).notifyPostLiked(any(), any());
    }

    @Test
    void togglePostReaction_ShouldThrowException_WhenPostNotFound() {
        // Given
        when(postRepository.findById("nonexistent")).thenReturn(Optional.empty());

        // When & Then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            likeService.togglePostReaction("nonexistent", "user123", true);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Post not found"));

        // Verify no interactions occurred
        verify(likeRepository, never()).save(any(Like.class));
        verify(likeRepository, never()).delete(any(Like.class));
        verify(postRepository, never()).save(any(Post.class));
    }

    // COMMENT LIKE/DISLIKE TESTS

    @Test
    void toggleCommentReaction_ShouldCreateNewLike_WhenNoExistingReaction() {
        // Given
        when(commentRepository.findById("comment123")).thenReturn(Optional.of(testComment));
        when(likeRepository.findByUserIdAndTargetIdAndTargetType("user123", "comment123", "comment"))
                .thenReturn(Optional.empty());
        // Stub saving comments to return the passed comment during creation
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0));
        // Stub post lookup for embedded comment update
        when(postRepository.findById("post123")).thenReturn(Optional.of(testPost));
        int initialLikes = testComment.getLikes();

        Like commentLike = new Like("user123", "comment123", "comment", true);
        commentLike.setId("like456");
        when(likeRepository.save(any(Like.class))).thenReturn(commentLike);

        // When
        Like result = likeService.toggleCommentReaction("comment123", "user123", true);

        // Then
        assertNotNull(result);
        assertTrue(result.isLike());
        assertEquals("comment123", result.getTargetId());

        // Verify comment count was incremented
        ArgumentCaptor<Comment> commentCaptor = ArgumentCaptor.forClass(Comment.class);
        verify(commentRepository).save(commentCaptor.capture());

        Comment savedComment = commentCaptor.getValue();
        assertEquals(initialLikes + 1, savedComment.getLikes());

        // Verify event was published
        verify(likeEventPublisher).sendLikeEvent(any(LikeEvent.class));

        // Verify observer was notified
        verify(postObservable).notifyCommentLiked(testComment, result);
    }

    @Test
    void toggleCommentReaction_ShouldToggleFromDislikeToLike() {
        // Given
        Like existingDislike = new Like("user123", "comment123", "comment", false);
        existingDislike.setId("like456");

        when(commentRepository.findById("comment123")).thenReturn(Optional.of(testComment));
        when(likeRepository.findByUserIdAndTargetIdAndTargetType("user123", "comment123", "comment"))
                .thenReturn(Optional.of(existingDislike));
        // Stub saving comments to return the passed comment during update
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0));
        // Stub post lookup for embedded comment update
        when(postRepository.findById("post123")).thenReturn(Optional.of(testPost));
        int initialLikes = testComment.getLikes();
        int initialDislikes = testComment.getDislikes();

        Like toggledLike = new Like("user123", "comment123", "comment", true);
        toggledLike.setId("like456");
        when(likeRepository.save(any(Like.class))).thenReturn(toggledLike);

        // When
        Like result = likeService.toggleCommentReaction("comment123", "user123", true);

        // Then
        assertNotNull(result);
        assertTrue(result.isLike()); // Should now be like

        // Verify like was updated
        ArgumentCaptor<Like> likeCaptor = ArgumentCaptor.forClass(Like.class);
        verify(likeRepository).save(likeCaptor.capture());

        Like savedLike = likeCaptor.getValue();
        assertTrue(savedLike.isLike());

        // Verify comment counts were updated correctly
        ArgumentCaptor<Comment> commentCaptor = ArgumentCaptor.forClass(Comment.class);
        verify(commentRepository).save(commentCaptor.capture());

        Comment savedComment = commentCaptor.getValue();
        assertEquals(initialLikes + 1, savedComment.getLikes()); // Like count incremented
        assertEquals(initialDislikes - 1, savedComment.getDislikes()); // Dislike count decremented
    }

    @Test
    void toggleCommentReaction_ShouldThrowException_WhenCommentNotFound() {
        // Given
        when(commentRepository.findById("nonexistent")).thenReturn(Optional.empty());

        // When & Then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            likeService.toggleCommentReaction("nonexistent", "user123", true);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Comment not found"));

        // Verify no interactions occurred
        verify(likeRepository, never()).save(any(Like.class));
        verify(likeRepository, never()).delete(any(Like.class));
        verify(commentRepository, never()).save(any(Comment.class));
    }
}