package com.example.FinalPrpject.controllers;

import com.example.FinalPrpject.DTO.NotificationPreferences;
import com.example.FinalPrpject.models.BanRequest;
import com.example.FinalPrpject.models.User;
import com.example.FinalPrpject.models.UserResponse;
import com.example.FinalPrpject.rabbitMQ.UserProducer;
import com.example.FinalPrpject.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserProducer userProducer;

    public UserController(UserService userService, UserProducer userProducer) {
        this.userService = userService;
        this.userProducer = userProducer;
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.createUser(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers().stream()
                .map(UserResponse::new)
                .toList();
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        return ResponseEntity.ok(userService.updateUser(id, updatedUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully");
    }

    @PostMapping("/{userId}/follow/{targetId}")
    public ResponseEntity<String> followUser(@PathVariable Long userId, @PathVariable Long targetId) {
        userService.followUser(userId, targetId);
        userProducer.sendNotificationEvent(userService.getUserById(userId), userService.getUserById(targetId), "followed user " + targetId, "FOLLOW");
        return ResponseEntity.ok("Followed user " + targetId);
    }

    @PostMapping("/{userId}/unfollow/{targetId}")
    public ResponseEntity<String> unfollowUser(@PathVariable Long userId, @PathVariable Long targetId) {
        userService.unfollowUser(userId, targetId);
        userProducer.sendNotificationEvent(userService.getUserById(userId), userService.getUserById(targetId), "unfollowed user " + targetId, "UNFOLLOW");
        return ResponseEntity.ok("Unfollowed user " + targetId);
    }


    @PostMapping("/{userId}/block/{blockedUserId}")
    public ResponseEntity<String> blockUser(@PathVariable Long userId, @PathVariable Long blockedUserId) {
        userService.blockUser(userId, blockedUserId);
        userProducer.sendNotificationEvent(userService.getUserById(userId), userService.getUserById(blockedUserId), "blocked user " + blockedUserId, "BLOCK");
        return ResponseEntity.ok("Blocked user " + blockedUserId);
    }

    @PostMapping("/{userId}/unblock/{blockedUserId}")
    public ResponseEntity<String> unBlockUser(@PathVariable Long userId, @PathVariable Long blockedUserId) {
        userService.unBlockUser(userId, blockedUserId);
        userProducer.sendNotificationEvent(userService.getUserById(userId), userService.getUserById(blockedUserId), "unblocked user " + blockedUserId, "UNBLOCK");
        return ResponseEntity.ok("Unblocked user " + blockedUserId);
    }

    @PostMapping("/{reporterId}/report/{reportedId}")
    public ResponseEntity<String> reportUser(@PathVariable Long reporterId,
                                             @PathVariable Long reportedId,
                                             @RequestBody Map<String, String> body) {
        String reason = body.get("reason");
        userService.reportUser(reporterId, reportedId, reason);
        userProducer.sendNotificationEvent(userService.getUserById(reporterId), userService.getUserById(reportedId), "reported user " + reportedId + " for: " + reason, "REPORT");
        return ResponseEntity.ok("User " + reportedId + " reported for: " + reason);
    }

    @GetMapping("/{userId}/preferences")
    public ResponseEntity<NotificationPreferences> getPreferences(@PathVariable Long userId) {
        User user = userService.getUserById(userId);
        return ResponseEntity.ok(new NotificationPreferences(user.isPushEnabled(), user.isEmailEnabled()));
    }

    @PutMapping("/{userId}/preferences")
    public ResponseEntity<Boolean> updatePreferences(@PathVariable Long userId,
                                                     @RequestBody NotificationPreferences preferences) {
        return ResponseEntity.ok(userService.updatePreferences(userId, preferences));
    }

    @PutMapping("/{id}/ban")
    public ResponseEntity<String> banUser(@PathVariable Long id,
                                          @RequestHeader(value = "X-Role", required = false) String role,
                                          @RequestBody BanRequest banRequest) {
        if (role == null || role.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Please log in to perform this action.");
        }

        if (!"MODERATOR".equalsIgnoreCase(role)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Access denied");
        }

        userService.banUser(id);
        return ResponseEntity.ok("User" + " " + id + " " + "has been banned");
    }
    @PutMapping("/{id}/unban")
    public ResponseEntity<String> unbanUser(@PathVariable Long id,
                                            @RequestHeader(value = "X-Role", required = false) String role) {
        if (role == null || role.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Please log in to perform this action.");
        }

        if (!"MODERATOR".equalsIgnoreCase(role)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Access denied");
        }

        userService.unbanUser(id);
        return ResponseEntity.ok("User" + " " + id + " " + "has been unbanned");
    }

    @PostMapping("/{id}/warn")
    public ResponseEntity<String> warnUser(@PathVariable Long id,
                                           @RequestHeader(value = "X-Role", required = false) String role,
                                         @RequestBody String message) {
        if (role == null || role.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Please log in to perform this action.");
        }

        if (!"MODERATOR".equalsIgnoreCase(role)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Access denied");
        }

        userService.warnUser(id, message);
        return ResponseEntity.ok("User" + " " + id + " " + "has been warned: " + message);
    }

    @PostMapping("/{userId}/post")
    public ResponseEntity<String> createPost(@PathVariable Long userId, @RequestBody Map<String, String> body) {
        String content = body.get("content");
        String title = body.get("title");

        if (content == null || content.isBlank() || (title == null || title.isBlank())) {
            return ResponseEntity.badRequest().body("Post content is required");
        }

        userService.getUserById(userId);

        userProducer.createPost(userId, content, title);

        return ResponseEntity.ok("Post request submitted successfully.");
    }

}
