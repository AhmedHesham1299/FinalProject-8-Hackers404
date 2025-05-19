package com.example.FinalProject.services;

import com.example.FinalProject.criteria.SearchCriteria;
import com.example.FinalProject.dtos.PostUpdateRequestDto;
import com.example.FinalProject.events.PostEventPublisher;
import com.example.FinalProject.events.dtos.PostCreatedEvent;
import com.example.FinalProject.services.TagService;
import com.example.FinalProject.events.dtos.PostUpdatedEvent;
import com.example.FinalProject.models.Comment;
import com.example.FinalProject.models.Post;
import com.example.FinalProject.repositories.PostRepository;
import com.example.FinalProject.repositories.CommentRepository;
import com.example.FinalProject.repositories.BookmarkRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostServiceCompleteTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private BookmarkRepository bookmarkRepository;

    @Mock
    private PostEventPublisher eventPublisher;

    private PostService postService;

    private Post testPost;
    private SearchCriteria testCriteria;
    private TagService tagService;
    private SearchCriteria criteriaKeywords;
    private SearchCriteria criteriaTags;
    private SearchCriteria criteriaAuthor;
    private SearchCriteria criteriaDates;
    private SearchCriteria criteriaAll;

    @BeforeEach
    void setUp() {
        criteriaKeywords = new SearchCriteria("key", null, null, null, null);
        criteriaTags = new SearchCriteria(null, Arrays.asList("tag1", "tag2"), null, null, null);
        criteriaAuthor = new SearchCriteria(null, null, "author123", null, null);
        criteriaDates = new SearchCriteria(null, null, null,
                LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31));
        criteriaAll = new SearchCriteria("k", Arrays.asList("t"), "a",
                LocalDate.now().minusDays(5), LocalDate.now());
        testPost = new Post();
        testPost.setId("post123");
        testPost.setTitle("Test Title");
        testPost.setContent("Test Content");
        testPost.setAuthorId("author123");
        testPost.setCreatedAt(LocalDateTime.now());
        Comment comment = new Comment("Test comment", "author123", "post123");
        testPost.setComments(new ArrayList<>(Arrays.asList(comment)));
        testCriteria = criteriaKeywords;
        postService = new PostService(postRepository, commentRepository, bookmarkRepository, eventPublisher,tagService);
    }

    @Test
    void searchPosts_shouldCallRepositoryWithCorrectCriteria() {
        // Given
        when(postRepository.searchPosts(any(SearchCriteria.class))).thenReturn(Arrays.asList(testPost));

        // When
        List<Post> result = postService.searchPosts(testCriteria);

        // Then
        ArgumentCaptor<SearchCriteria> criteriaCaptor = ArgumentCaptor.forClass(SearchCriteria.class);
        verify(postRepository).searchPosts(criteriaCaptor.capture());

        SearchCriteria capturedCriteria = criteriaCaptor.getValue();
        assertEquals(testCriteria, capturedCriteria);
        assertEquals(1, result.size());
        assertEquals(testPost, result.get(0));
    }

    @Test
    void createPost_shouldSavePostAndPublishEvent() {
        // Given
        when(postRepository.save(any(Post.class))).thenReturn(testPost);

        // When
        Post result = postService.createPost(testPost);

        // Then
        verify(postRepository).save(testPost);
        verify(eventPublisher).sendPostCreatedEvent(any(PostCreatedEvent.class));
        assertEquals(testPost, result);
    }

    @Test
    void updatePost_shouldUpdateExistingPostAndPublishEvent() {
        // Given
        PostUpdateRequestDto updateRequest = new PostUpdateRequestDto("Updated Title", "Updated Content",
                Arrays.asList("updated", "tag"));
        when(postRepository.findById("post123")).thenReturn(Optional.of(testPost));
        when(postRepository.save(any(Post.class))).thenReturn(testPost);

        // When
        Post result = postService.updatePost("post123", updateRequest);

        // Then
        verify(postRepository).findById("post123");
        verify(postRepository).save(any(Post.class));
        verify(eventPublisher).sendPostUpdatedEvent(any(PostUpdatedEvent.class));

        assertEquals("Updated Title", result.getTitle());
        assertEquals("Updated Content", result.getContent());
        assertEquals(Arrays.asList("updated", "tag"), result.getTags());
    }

    @Test
    void updatePost_shouldThrowExceptionWhenPostNotFound() {
        // Given
        PostUpdateRequestDto updateRequest = new PostUpdateRequestDto("Updated Title", "Updated Content", null);
        when(postRepository.findById("nonexistent")).thenReturn(Optional.empty());

        // When & Then
        Exception exception = assertThrows(RuntimeException.class, () -> {
            postService.updatePost("nonexistent", updateRequest);
        });

        assertTrue(exception.getMessage().contains("Post not found"));
        verify(postRepository, never()).save(any(Post.class));
        verify(eventPublisher, never()).sendPostUpdatedEvent(any(PostUpdatedEvent.class));
    }

    @Test
    void findPost_shouldReturnPostWhenFound() {
        // Given
        when(postRepository.findById("post123")).thenReturn(Optional.of(testPost));

        // When
        Post result = postService.findPost("post123");

        // Then
        verify(postRepository).findById("post123");
        assertEquals(testPost, result);
    }

    @Test
    void findPost_shouldThrowExceptionWhenNotFound() {
        // Given
        when(postRepository.findById("nonexistent")).thenReturn(Optional.empty());

        // When & Then
        Exception exception = assertThrows(RuntimeException.class, () -> {
            postService.findPost("nonexistent");
        });

        assertTrue(exception.getMessage().contains("Post not found"));
    }

    @Test
    void deletePost_shouldDeleteWhenPostExists() {
        // Given
        when(postRepository.existsById("post123")).thenReturn(true);

        // When
        postService.deletePost("post123");

        // Then
        verify(postRepository).existsById("post123");
        verify(postRepository).deleteById("post123");
        verify(commentRepository).deleteByPostId("post123");
        verify(bookmarkRepository).deleteByPostId("post123");
    }

    @Test
    void deletePost_shouldThrowExceptionWhenPostNotFound() {
        // Given
        when(postRepository.existsById("nonexistent")).thenReturn(false);

        // When & Then
        Exception exception = assertThrows(RuntimeException.class, () -> {
            postService.deletePost("nonexistent");
        });

        assertTrue(exception.getMessage().contains("Post not found"));
        verify(postRepository, never()).deleteById(anyString());
    }

}