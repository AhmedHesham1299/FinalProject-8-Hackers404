package com.example.FinalPrpject.controllers;

import com.example.FinalPrpject.models.User;
import com.example.FinalPrpject.models.UserResponse;
import com.example.FinalPrpject.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

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
        return ResponseEntity.ok("Followed user " + targetId);
    }

    @PostMapping("/{userId}/unfollow/{targetId}")
    public ResponseEntity<String> unfollowUser(@PathVariable Long userId, @PathVariable Long targetId) {
        userService.unfollowUser(userId, targetId);
        return ResponseEntity.ok("Unfollowed user " + targetId);
    }


    @PostMapping("/{userId}/block/{blockedUserId}")
    public ResponseEntity<String> blockUser(@PathVariable Long userId, @PathVariable Long blockedUserId) {
        userService.blockUser(userId, blockedUserId);
        return ResponseEntity.ok("Blocked user " + blockedUserId);
    }

    @PostMapping("/{userId}/unblock/{blockedUserId}")
    public ResponseEntity<String> unBlockUser(@PathVariable Long userId, @PathVariable Long blockedUserId) {
        userService.unBlockUser(userId, blockedUserId);
        return ResponseEntity.ok("Unblocked user " + blockedUserId);
    }

    @PostMapping("/{reporterId}/report/{reportedId}")
    public ResponseEntity<String> reportUser(@PathVariable Long reporterId,
                                             @PathVariable Long reportedId,
                                             @RequestBody Map<String, String> body) {
        String reason = body.get("reason");
        userService.reportUser(reporterId, reportedId, reason);
        return ResponseEntity.ok("User " + reportedId + " reported for: " + reason);
    }
}
