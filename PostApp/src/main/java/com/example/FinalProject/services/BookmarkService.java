package com.example.FinalProject.services;

import com.example.FinalProject.controllers.BookmarkRequestPayload;
import com.example.FinalProject.models.Bookmark;
import com.example.FinalProject.repositories.BookmarkRepository;
import com.example.FinalProject.repositories.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final PostRepository postRepository;

    @Autowired
    public BookmarkService(BookmarkRepository bookmarkRepository, PostRepository postRepository) {
        this.bookmarkRepository = bookmarkRepository;
        this.postRepository = postRepository;
    }

    public Bookmark createBookmark(BookmarkRequestPayload payload) {
        if (!postRepository.existsById(payload.postId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found");
        }
        if (bookmarkRepository.existsByUserIdAndPostId(payload.userId(), payload.postId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Bookmark already exists");
        }
        Bookmark bookmark = new Bookmark(payload.userId(), payload.postId());
        bookmark.setCreatedAt(LocalDateTime.now());
        return bookmarkRepository.save(bookmark);
    }

    public List<Bookmark> getBookmarksByUserId(String userId) {
        return bookmarkRepository.findByUserId(userId);
    }

    public Optional<Bookmark> getBookmark(String userId, String postId) {
        return bookmarkRepository.findByUserIdAndPostId(userId, postId);
    }

    public void deleteBookmark(String userId, String postId) {
        if (!bookmarkRepository.existsByUserIdAndPostId(userId, postId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Bookmark not found");
        }
        bookmarkRepository.deleteByUserIdAndPostId(userId, postId);
    }

    public void deleteBookmarkById(String id) {
        if (!bookmarkRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Bookmark not found");
        }
        bookmarkRepository.deleteById(id);
    }

    public List<Bookmark> getAllBookmarks() {
        return bookmarkRepository.findAll();
    }
}