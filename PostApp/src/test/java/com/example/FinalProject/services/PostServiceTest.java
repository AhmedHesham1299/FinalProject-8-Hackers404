package com.example.FinalProject.services;

import com.example.FinalProject.criteria.SearchCriteria;
import com.example.FinalProject.repositories.PostRepository;
import com.example.FinalProject.repositories.CommentRepository;
import com.example.FinalProject.events.PostEventPublisher;
import com.example.FinalProject.models.Post;
import com.example.FinalProject.services.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @Mock
    private PostRepository postRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private PostEventPublisher postEventPublisher;

    private PostService postService;

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
        postService = new PostService(postRepository, commentRepository, postEventPublisher);
    }

    @Test
    void searchPosts_withKeywordsOnly_shouldCallRepositoryWithSameCriteria() {
        postService.searchPosts(criteriaKeywords);
        ArgumentCaptor<SearchCriteria> captor = ArgumentCaptor.forClass(SearchCriteria.class);
        verify(postRepository).searchPosts(captor.capture());
        assertThat(captor.getValue()).isEqualTo(criteriaKeywords);
    }

    @Test
    void searchPosts_withTagsOnly_shouldCallRepositoryWithSameCriteria() {
        postService.searchPosts(criteriaTags);
        ArgumentCaptor<SearchCriteria> captor = ArgumentCaptor.forClass(SearchCriteria.class);
        verify(postRepository).searchPosts(captor.capture());
        assertThat(captor.getValue()).isEqualTo(criteriaTags);
    }

    @Test
    void searchPosts_withAuthorOnly_shouldCallRepositoryWithSameCriteria() {
        postService.searchPosts(criteriaAuthor);
        ArgumentCaptor<SearchCriteria> captor = ArgumentCaptor.forClass(SearchCriteria.class);
        verify(postRepository).searchPosts(captor.capture());
        assertThat(captor.getValue()).isEqualTo(criteriaAuthor);
    }

    @Test
    void searchPosts_withDateRangeOnly_shouldCallRepositoryWithSameCriteria() {
        postService.searchPosts(criteriaDates);
        ArgumentCaptor<SearchCriteria> captor = ArgumentCaptor.forClass(SearchCriteria.class);
        verify(postRepository).searchPosts(captor.capture());
        assertThat(captor.getValue()).isEqualTo(criteriaDates);
    }

    @Test
    void searchPosts_withAllCriteria_shouldCallRepositoryWithSameCriteria() {
        postService.searchPosts(criteriaAll);
        ArgumentCaptor<SearchCriteria> captor = ArgumentCaptor.forClass(SearchCriteria.class);
        verify(postRepository).searchPosts(captor.capture());
        assertThat(captor.getValue()).isEqualTo(criteriaAll);
    }

    @Test
    void searchPosts_withNoCriteria_shouldCallRepositoryWithEmptyCriteria() {
        SearchCriteria empty = new SearchCriteria(null, null, null, null, null);
        postService.searchPosts(empty);
        ArgumentCaptor<SearchCriteria> captor = ArgumentCaptor.forClass(SearchCriteria.class);
        verify(postRepository).searchPosts(captor.capture());
        assertThat(captor.getValue()).isEqualTo(empty);
    }
}