package com.example.FinalProject.observers;

import com.example.FinalProject.events.NotificationEventPublisher;
import com.example.FinalProject.events.dtos.NotificationEvent;
import com.example.FinalProject.models.Comment;
import com.example.FinalProject.models.Like;
import com.example.FinalProject.models.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationPostObserverTest {

    @Mock
    private PostObservable postObservable;

    @Mock
    private NotificationEventPublisher notificationEventPublisher;

    private NotificationPostObserver observer;
    private Post testPost;
    private Comment testComment;
    private Like testLike;

    @BeforeEach
    void setUp() {
        observer = new NotificationPostObserver(postObservable, notificationEventPublisher);
        
        // Setup test post
        testPost = new Post();
        testPost.setId("post123");
        testPost.setTitle("Test Post Title");
        testPost.setContent("Test content");
        testPost.setAuthorId("author123");
        testPost.setCreatedAt(LocalDateTime.now());
        
        // Setup test comment
        testComment = new Comment("Test comment content", "commenter456", "post123");
        testComment.setId("comment123");
        testComment.setCreatedAt(LocalDateTime.now());
        
        // Setup test like
        testLike = new Like("liker789", "post123", "post", true);
        testLike.setId("like123");
        testLike.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void init_shouldRegisterObserverWithObservable() {
        // When
        observer.init();
        
        // Then
        verify(postObservable).addObserver(observer);
    }

    @Test
    void onCommentAdded_shouldCreateNotification_WhenCommentByDifferentUser() {
        // Given
        // Post author = author123, comment author = commenter456 (different)
        
        // When
        observer.onCommentAdded(testPost, testComment);
        
        // Then
        ArgumentCaptor<NotificationEvent> eventCaptor = ArgumentCaptor.forClass(NotificationEvent.class);
        verify(notificationEventPublisher).sendNotificationEvent(eventCaptor.capture());
        
        NotificationEvent capturedEvent = eventCaptor.getValue();
        assertEquals("author123", capturedEvent.getUserId()); // Notification sent to post author
        assertEquals("New Comment", capturedEvent.getTitle());
        assertTrue(capturedEvent.getMessage().contains("Test Post Title"));
        assertEquals("/posts/post123", capturedEvent.getLink());
        assertEquals("COMMENT", capturedEvent.getType());
    }

    @Test
    void onCommentAdded_shouldNotCreateNotification_WhenAuthorCommentsOwnPost() {
        // Given
        Comment selfComment = new Comment("Self comment", "author123", "post123");
        selfComment.setId("selfComment123");
        
        // When
        observer.onCommentAdded(testPost, selfComment);
        
        // Then
        verify(notificationEventPublisher, never()).sendNotificationEvent(any());
    }

    @Test
    void onPostLiked_shouldCreateNotification_WhenLikedByDifferentUser() {
        // Given
        // Post author = author123, liker = liker789 (different)
        
        // When
        observer.onPostLiked(testPost, testLike);
        
        // Then
        ArgumentCaptor<NotificationEvent> eventCaptor = ArgumentCaptor.forClass(NotificationEvent.class);
        verify(notificationEventPublisher).sendNotificationEvent(eventCaptor.capture());
        
        NotificationEvent capturedEvent = eventCaptor.getValue();
        assertEquals("author123", capturedEvent.getUserId()); // Notification sent to post author
        assertEquals("Post liked", capturedEvent.getTitle());
        assertTrue(capturedEvent.getMessage().contains("liked"));
        assertEquals("/posts/post123", capturedEvent.getLink());
        assertEquals("LIKE", capturedEvent.getType());
    }

    @Test
    void onPostLiked_shouldCreateNotification_WhenDislikedByDifferentUser() {
        // Given
        Like dislike = new Like("liker789", "post123", "post", false);
        dislike.setId("dislike123");
        
        // When
        observer.onPostLiked(testPost, dislike);
        
        // Then
        ArgumentCaptor<NotificationEvent> eventCaptor = ArgumentCaptor.forClass(NotificationEvent.class);
        verify(notificationEventPublisher).sendNotificationEvent(eventCaptor.capture());
        
        NotificationEvent capturedEvent = eventCaptor.getValue();
        assertEquals("Post disliked", capturedEvent.getTitle());
        assertTrue(capturedEvent.getMessage().contains("disliked"));
    }

    @Test
    void onPostLiked_shouldNotCreateNotification_WhenAuthorLikesOwnPost() {
        // Given
        Like selfLike = new Like("author123", "post123", "post", true);
        selfLike.setId("selfLike123");
        
        // When
        observer.onPostLiked(testPost, selfLike);
        
        // Then
        verify(notificationEventPublisher, never()).sendNotificationEvent(any());
    }

    @Test
    void onCommentLiked_shouldCreateNotification_WhenLikedByDifferentUser() {
        // Given
        Like commentLike = new Like("liker789", "comment123", "comment", true);
        commentLike.setId("commentLike123");
        
        // When
        observer.onCommentLiked(testComment, commentLike);
        
        // Then
        ArgumentCaptor<NotificationEvent> eventCaptor = ArgumentCaptor.forClass(NotificationEvent.class);
        verify(notificationEventPublisher).sendNotificationEvent(eventCaptor.capture());
        
        NotificationEvent capturedEvent = eventCaptor.getValue();
        assertEquals("commenter456", capturedEvent.getUserId()); // Notification sent to comment author
        assertEquals("Comment liked", capturedEvent.getTitle());
        assertTrue(capturedEvent.getMessage().contains("liked"));
        assertEquals("/posts/post123", capturedEvent.getLink());
        assertEquals("LIKE", capturedEvent.getType());
    }

    @Test
    void onCommentLiked_shouldNotCreateNotification_WhenAuthorLikesOwnComment() {
        // Given
        Like selfLike = new Like("commenter456", "comment123", "comment", true);
        selfLike.setId("selfCommentLike123");
        
        // When
        observer.onCommentLiked(testComment, selfLike);
        
        // Then
        verify(notificationEventPublisher, never()).sendNotificationEvent(any());
    }
} 