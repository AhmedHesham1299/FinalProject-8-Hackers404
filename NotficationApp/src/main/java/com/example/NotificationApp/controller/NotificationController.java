package com.example.NotificationApp.controller;

import com.example.NotificationApp.model.NotificationPreferences;
import com.example.NotificationApp.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
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
