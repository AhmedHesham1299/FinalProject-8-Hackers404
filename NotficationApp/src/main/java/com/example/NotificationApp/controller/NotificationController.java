package com.example.NotificationApp.controller;

import com.example.NotificationApp.model.Notification;
import com.example.NotificationApp.model.NotificationPreferences;
import com.example.NotificationApp.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> getUserNotifications(@PathVariable String userId) {
        return ResponseEntity.ok(notificationService.getNotifications(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Notification> getNotificationById(@PathVariable String id) {
        return ResponseEntity.ok(notificationService.getNotificationById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Notification> updateNotification(@PathVariable String id, @RequestBody Notification updated) {
        Notification notification = notificationService.updateNotification(id, updated);
        return ResponseEntity.ok(notification);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteNotification(@PathVariable String id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.ok("Deleted successfully");
    }

    @GetMapping("/filter")
    public ResponseEntity<List<Notification>> filterNotificationsByDate(
            @RequestParam String userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) {
        return ResponseEntity.ok(notificationService.filterNotificationsByDate(userId, from, to));
    }

    @PutMapping("/mark-read/{id}")
    public ResponseEntity<Notification> markAsRead(@PathVariable String id) {
        return ResponseEntity.ok(notificationService.markAsRead(id));
    }

    @GetMapping("/preferences/{id}")
    public ResponseEntity<NotificationPreferences> getPreferences(@PathVariable String id) {
        NotificationPreferences preferences = notificationService.getPreferences(id);
        return ResponseEntity.ok(preferences);
    }

    @PostMapping("preferences/{id}")
    public ResponseEntity<String> updatePreferences(@PathVariable String id, @RequestBody NotificationPreferences preferences) {
        String updated = notificationService.updatePreferences(id, preferences);
        return ResponseEntity.ok(updated);
    }
}
