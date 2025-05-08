package com.example.FinalProject.controllers;

import com.example.FinalProject.controllers.BookmarkRequestPayload;
import com.example.FinalProject.models.Bookmark;
import com.example.FinalProject.services.BookmarkService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookmarks")
public class BookmarkController {

    private final BookmarkService bookmarkService;

    public BookmarkController(BookmarkService bookmarkService) {
        this.bookmarkService = bookmarkService;
    }

    @PostMapping
    public ResponseEntity<Bookmark> addBookmark(@RequestBody BookmarkRequestPayload payload) {
        Bookmark created = bookmarkService.createBookmark(payload);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @DeleteMapping("/{bookmarkId}")
    public ResponseEntity<Void> removeBookmark(@PathVariable String bookmarkId) {
        bookmarkService.deleteBookmarkById(bookmarkId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<Bookmark>> listBookmarksForUser(@RequestParam String userId) {
        List<Bookmark> bookmarks = bookmarkService.getBookmarksByUserId(userId);
        return ResponseEntity.ok(bookmarks);
    }
}