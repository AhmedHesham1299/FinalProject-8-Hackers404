package com.example.FinalProject.services;

import com.example.FinalProject.controllers.BookmarkRequestPayload;
import com.example.FinalProject.models.Bookmark;
import com.example.FinalProject.repositories.BookmarkRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
public class BookmarkServiceTest {

    @Mock
    private BookmarkRepository bookmarkRepository;

    @InjectMocks
    private BookmarkService bookmarkService;

    private final String userId = "user123";
    private final String postId = "post456";
    private Bookmark sampleBookmark;

    @BeforeEach
    void setUp() {
        sampleBookmark = new Bookmark(userId, postId);
        sampleBookmark.setId("bookmark789");
        sampleBookmark.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void createBookmark_whenDoesNotExist_shouldSaveAndReturnBookmark() {
        // Arrange
        BookmarkRequestPayload payload = new BookmarkRequestPayload(userId, postId);
        when(bookmarkRepository.existsByUserIdAndPostId(userId, postId)).thenReturn(false);
        when(bookmarkRepository.save(any(Bookmark.class))).thenReturn(sampleBookmark);

        // Act
        Bookmark result = bookmarkService.createBookmark(payload);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(postId, result.getPostId());
        assertNotNull(result.getCreatedAt());
        verify(bookmarkRepository).save(any(Bookmark.class));
    }

    @Test
    void createBookmark_whenAlreadyExists_shouldThrowConflictException() {
        // Arrange
        BookmarkRequestPayload payload = new BookmarkRequestPayload(userId, postId);
        when(bookmarkRepository.existsByUserIdAndPostId(userId, postId)).thenReturn(true);

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> bookmarkService.createBookmark(payload));
        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
    }

    @Test
    void getBookmarksByUserId_shouldReturnListOfBookmarks() {
        // Arrange
        List<Bookmark> expectedBookmarks = Arrays.asList(sampleBookmark);
        when(bookmarkRepository.findByUserId(userId)).thenReturn(expectedBookmarks);

        // Act
        List<Bookmark> result = bookmarkService.getBookmarksByUserId(userId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(sampleBookmark.getId(), result.get(0).getId());
        verify(bookmarkRepository).findByUserId(userId);
    }

    @Test
    void deleteBookmark_whenExists_shouldCallRepositoryDelete() {
        // Arrange
        when(bookmarkRepository.existsByUserIdAndPostId(userId, postId)).thenReturn(true);

        // Act
        bookmarkService.deleteBookmark(userId, postId);

        // Assert
        verify(bookmarkRepository).deleteByUserIdAndPostId(userId, postId);
    }

    @Test
    void deleteBookmark_whenNotExists_shouldThrowNotFoundException() {
        // Arrange
        when(bookmarkRepository.existsByUserIdAndPostId(userId, postId)).thenReturn(false);

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> bookmarkService.deleteBookmark(userId, postId));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }
}