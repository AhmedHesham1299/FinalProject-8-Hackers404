package com.example.FinalProject.repositories;

import com.example.FinalProject.criteria.SearchCriteria;
import com.example.FinalProject.models.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@TestPropertySource(properties = "spring.mongodb.embedded.version=5.0.0")
public class CustomPostRepositoryImplTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    private CustomPostRepositoryImpl customPostRepository;
    private Post post1;
    private Post post2;
    private Post post3;

    @BeforeEach
    void setUp() {
        customPostRepository = new CustomPostRepositoryImpl(mongoTemplate);
        
        // Clear the posts collection
        mongoTemplate.dropCollection(Post.class);
        
        // Create test posts
        post1 = Post.builder("Java Programming", "Learn Java programming basics", "user123")
                .tags(Arrays.asList("java", "programming"))
                .createdAt(LocalDateTime.now().minusDays(5))
                .build();
        
        post2 = Post.builder("Spring Boot Tutorial", "Building REST APIs with Spring Boot", "user123")
                .tags(Arrays.asList("spring", "java", "api"))
                .createdAt(LocalDateTime.now().minusDays(2))
                .build();
        
        post3 = Post.builder("MongoDB for Beginners", "Introduction to MongoDB database", "user456")
                .tags(Arrays.asList("mongodb", "database", "nosql"))
                .createdAt(LocalDateTime.now().minusDays(1))
                .build();
        
        // Save posts to the embedded MongoDB
        mongoTemplate.save(post1);
        mongoTemplate.save(post2);
        mongoTemplate.save(post3);
    }

    @Test
    void searchPosts_withKeywordsOnly_shouldFindMatchingPosts() {
        // Given
        SearchCriteria criteria = new SearchCriteria("java", null, null, null, null);
        
        // When
        List<Post> results = customPostRepository.searchPosts(criteria);
        
        // Then
        assertEquals(2, results.size());
        assertTrue(results.stream().anyMatch(p -> p.getTitle().equals("Java Programming")));
        assertTrue(results.stream().anyMatch(p -> p.getTitle().equals("Spring Boot Tutorial")));
    }

    @Test
    void searchPosts_withTagsOnly_shouldFindMatchingPosts() {
        // Given
        SearchCriteria criteria = new SearchCriteria(null, Arrays.asList("java"), null, null, null);
        
        // When
        List<Post> results = customPostRepository.searchPosts(criteria);
        
        // Then
        assertEquals(2, results.size());
        assertTrue(results.stream().anyMatch(p -> p.getTitle().equals("Java Programming")));
        assertTrue(results.stream().anyMatch(p -> p.getTitle().equals("Spring Boot Tutorial")));
    }

    @Test
    void searchPosts_withAuthorIdOnly_shouldFindMatchingPosts() {
        // Given
        SearchCriteria criteria = new SearchCriteria(null, null, "user456", null, null);
        
        // When
        List<Post> results = customPostRepository.searchPosts(criteria);
        
        // Then
        assertEquals(1, results.size());
        assertEquals("MongoDB for Beginners", results.get(0).getTitle());
    }

    @Test
    void searchPosts_withDateRangeOnly_shouldFindMatchingPosts() {
        // Given
        SearchCriteria criteria = new SearchCriteria(null, null, null, 
                LocalDate.now().minusDays(3), LocalDate.now());
        
        // When
        List<Post> results = customPostRepository.searchPosts(criteria);
        
        // Then
        assertEquals(2, results.size());
        assertTrue(results.stream().anyMatch(p -> p.getTitle().equals("Spring Boot Tutorial")));
        assertTrue(results.stream().anyMatch(p -> p.getTitle().equals("MongoDB for Beginners")));
    }

    @Test
    void searchPosts_withMultipleCriteria_shouldFindMatchingPosts() {
        // Given
        SearchCriteria criteria = new SearchCriteria("api", Arrays.asList("spring"), "user123", 
                LocalDate.now().minusDays(3), LocalDate.now());
        
        // When
        List<Post> results = customPostRepository.searchPosts(criteria);
        
        // Then
        assertEquals(1, results.size());
        assertEquals("Spring Boot Tutorial", results.get(0).getTitle());
    }

    @Test
    void searchPosts_withNoCriteria_shouldReturnAllPosts() {
        // Given
        SearchCriteria criteria = new SearchCriteria(null, null, null, null, null);
        
        // When
        List<Post> results = customPostRepository.searchPosts(criteria);
        
        // Then
        assertEquals(3, results.size());
    }

    @Test
    void searchPosts_withNoMatchingCriteria_shouldReturnEmptyList() {
        // Given
        SearchCriteria criteria = new SearchCriteria("nonexistent", null, null, null, null);
        
        // When
        List<Post> results = customPostRepository.searchPosts(criteria);
        
        // Then
        assertEquals(0, results.size());
    }
} 