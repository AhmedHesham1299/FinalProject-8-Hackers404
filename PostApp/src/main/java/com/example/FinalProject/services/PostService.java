package com.example.FinalProject.services;

import com.example.FinalProject.criteria.SearchCriteria;
import com.example.FinalProject.models.Post;
import com.example.FinalProject.models.Comment;
import com.example.FinalProject.repositories.PostRepository;
import com.example.FinalProject.events.PostEventPublisher;
import com.example.FinalProject.events.dtos.PostEventPayload;
import com.example.FinalProject.events.dtos.PostCreatedEvent;
import com.example.FinalProject.events.dtos.PostUpdatedEvent;
import com.example.FinalProject.events.dtos.PostDeletedEvent;
import com.example.FinalProject.dtos.PostUpdateRequestDto;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Caching;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.ArrayList;
import java.util.Optional;

@Service
public class PostService {
    private static final Logger logger = LoggerFactory.getLogger(PostService.class);
    private final PostRepository postRepository;
    

    public PostService(PostRepository postRepository, PostEventPublisher postEventPublisher) {
        this.postRepository = postRepository;
    }
    
    @Cacheable(value = "post", key = "#postId", unless = "#result == null")
    public Post findPost(String postId) {
        logger.debug("Cache miss for post with id: {}", postId);
        return postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));
    }

    @Cacheable(value = "posts", key = "'search_' + #criteria.hashCode()", unless = "#result.isEmpty()")
    public List<Post> searchPosts(SearchCriteria criteria) {
        logger.debug("Cache miss for post search with criteria: {}", criteria);
        return postRepository.searchPosts(criteria);
    }

    @Cacheable(value = "postsByAuthor", key = "#authorId", unless = "#result.isEmpty()")
    public List<Post> findPostsByAuthor(String authorId) {
        logger.debug("Cache miss for posts by author: {}", authorId);
        return postRepository.findByAuthorId(authorId);
    }

    @Caching(
        put = { @CachePut(value = "post", key = "#result.id") },
        evict = { 
            @CacheEvict(value = "posts", allEntries = true),
            @CacheEvict(value = "postsByAuthor", key = "#result.authorId")
        }
    )
    public Post createPost(Post post) {
        Post saved = postRepository.save(post);
        PostEventPayload payload = new PostEventPayload(saved.getId(), saved.getTitle(), saved.getAuthorId(),
                saved.getCreatedAt());
        PostCreatedEvent event = new PostCreatedEvent(UUID.randomUUID().toString(), "POST_CREATED", LocalDateTime.now(),
                payload);
        
        return saved;
    }

    @Caching(
        put = { @CachePut(value = "post", key = "#postId") },
        evict = { 
            @CacheEvict(value = "posts", allEntries = true),
            @CacheEvict(value = "postsByAuthor", key = "#result.authorId")
        }
    )
    public Post updatePost(String postId, PostUpdateRequestDto updateRequest) {
        Post existing = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));
        List<String> updatedFields = new ArrayList<>();
        if (updateRequest.title() != null) {
            existing.setTitle(updateRequest.title());
            updatedFields.add("title");
        }
        if (updateRequest.content() != null) {
            existing.setContent(updateRequest.content());
            updatedFields.add("content");
        }
        if (updateRequest.tags() != null) {
            existing.setTags(updateRequest.tags());
            updatedFields.add("tags");
        }
        Post updated = postRepository.save(existing);
        PostEventPayload payload = new PostEventPayload(updated.getId(), updated.getTitle(), updated.getAuthorId(),
                updated.getCreatedAt());
        PostUpdatedEvent event = new PostUpdatedEvent(UUID.randomUUID().toString(), "POST_UPDATED", LocalDateTime.now(),
                payload, updatedFields);
        return updated;
    }

    @Caching(evict = { 
        @CacheEvict(value = "post", key = "#postId"),
        @CacheEvict(value = "posts", allEntries = true),
        @CacheEvict(value = "postsByAuthor", key = "#result.authorId", condition = "#result != null")
    })
    public void deletePost(String postId) {
        Post existing = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));
        String authorId = existing.getAuthorId(); // Save author ID before deleting
        
        postRepository.delete(existing);
        
        PostEventPayload payload = new PostEventPayload(existing.getId(), existing.getTitle(), authorId,
                existing.getCreatedAt());
        PostDeletedEvent event = new PostDeletedEvent(UUID.randomUUID().toString(), "POST_DELETED", LocalDateTime.now(),
                payload);
    }

    @Caching(evict = {
        @CacheEvict(value = "postsByAuthor", key = "#authorId"),
        @CacheEvict(value = "posts", allEntries = true)
    })
    public long deletePostsByAuthor(String authorId) {
        logger.info("Deleting all posts for author: {}", authorId);
        List<Post> authorPosts = postRepository.findByAuthorId(authorId);
        int count = authorPosts.size();
        
        if (count > 0) {
            // Evict individual post caches
            for (Post post : authorPosts) {
                evictPostCache(post.getId());
            }
            
            postRepository.deleteAll(authorPosts);
            logger.info("Deleted {} posts for author: {}", count, authorId);
        } else {
            logger.info("No posts found for author: {}", authorId);
        }
        
        return count;
    }
    
    // Helper method to evict a specific post from cache
    @CacheEvict(value = "post", key = "#postId")
    public void evictPostCache(String postId) {
        logger.debug("Evicting cache for post: {}", postId);
        // This method doesn't need a body, the annotation handles cache eviction
    }
    
    @CachePut(value = "post", key = "#postId")
    public Post addComment(String postId, String content, int likes, int dislikes) {
        Post post = findPost(postId);
        post.addComment(content, likes, dislikes);
        return postRepository.save(post);
    }
    
    @CachePut(value = "post", key = "#postId")
    public Post addComment(String postId, Comment comment) {
        Post post = findPost(postId);
        post.addComment(comment);
        return postRepository.save(post);
    }
    
    @Cacheable(value = "comment", key = "#postId + '-' + #content", unless = "#result.isEmpty()")
    public Optional<Comment> findComment(String postId, String content) {
        Post post = findPost(postId);
        return post.findCommentByContent(content);
    }
    
    @Cacheable(value = "comments", key = "#postId", unless = "#result.isEmpty()")
    public List<Comment> getComments(String postId) {
        Post post = findPost(postId);
        return post.getComments();
    }
    
    @CachePut(value = "post", key = "#postId")
    public Post updateComment(String postId, String oldContent, Comment updatedComment) {
        Post post = findPost(postId);
        boolean updated = post.updateComment(oldContent, updatedComment);
        if (updated) {
            // Evict the old comment cache entry
            evictCommentCache(postId, oldContent);
            return postRepository.save(post);
        }
        return null;
    }
    
    @CachePut(value = "post", key = "#postId")
    public Post deleteComment(String postId, String content) {
        Post post = findPost(postId);
        boolean removed = post.deleteComment(content);
        if (removed) {
            // Evict the comment cache entry
            evictCommentCache(postId, content);
            return postRepository.save(post);
        }
        return null;
    }
    
    @CacheEvict(value = "comment", key = "#postId + '-' + #content")
    public void evictCommentCache(String postId, String content) {
        logger.debug("Evicting cache for comment: {} in post: {}", content, postId);
        // Method body not needed; annotation handles cache eviction
    }
    
    @CacheEvict(value = "comments", key = "#postId")
    public void evictCommentsCache(String postId) {
        logger.debug("Evicting cache for all comments in post: {}", postId);
        // Method body not needed; annotation handles cache eviction
    }
}