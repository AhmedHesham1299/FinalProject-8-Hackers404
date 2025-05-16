package com.example.FinalProject.controllers;

import com.example.FinalProject.controllers.BookmarkRequestPayload;
import com.example.FinalProject.models.Bookmark;
import com.example.FinalProject.services.BookmarkService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

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
    public ResponseEntity<Map<String, String>> removeBookmark(@PathVariable String bookmarkId) {
        bookmarkService.deleteBookmarkById(bookmarkId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Bookmark deleted successfully");
        response.put("bookmarkId", bookmarkId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<Bookmark>> listBookmarksForUser(@RequestParam String userId) {
        List<Bookmark> bookmarks = bookmarkService.getBookmarksByUserId(userId);
        return ResponseEntity.ok(bookmarks);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Bookmark>> listAllBookmarks() {
        List<Bookmark> bookmarks = bookmarkService.getAllBookmarks();
        return ResponseEntity.ok(bookmarks);
    }
}