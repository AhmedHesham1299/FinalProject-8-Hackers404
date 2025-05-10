package com.example.FinalPrpject.controllers;

import com.example.FinalPrpject.models.BanPayload;
import com.example.FinalPrpject.models.Moderator;
import com.example.FinalPrpject.services.BanCommandExecutor;
import com.example.FinalPrpject.services.ModerationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/moderators")
public class ModeratorController {

    private final BanCommandExecutor executor;
    private final ModerationService moderationService;

    public ModeratorController(BanCommandExecutor executor, ModerationService moderationService) {
        this.executor = executor;
        this.moderationService = moderationService;
    }

    // Warn user
    //
    //
    // To be implemented
    //
    //
    //

    // Ban user
    @PostMapping("/ban")
    public ResponseEntity<?> banUser(@RequestBody BanPayload payload) {
        payload.setBanDate(LocalDateTime.now());
        executor.executeBan(payload);
        return ResponseEntity.ok("User banned successfully.");
    }

    // UnBan user
    //
    //
    // To be implemented
    //
    //
    //

    // Create moderator
    @PostMapping
    public ResponseEntity<Moderator> create(@RequestBody Moderator moderator) {
        return ResponseEntity.ok(moderationService.createModerator(moderator));
    }

    // Get all moderators
    @GetMapping
    public ResponseEntity<List<Moderator>> getAll() {
        return ResponseEntity.ok(moderationService.getAllModerators());
    }

    // Get moderator by ID
    @GetMapping("/{id}")
    public ResponseEntity<Moderator> getById(@PathVariable Long id) {
        return moderationService.getModeratorById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Update moderator
    @PutMapping("/{id}")
    public ResponseEntity<Moderator> update(@PathVariable Long id, @RequestBody Moderator updated) {
        return ResponseEntity.ok(moderationService.updateModerator(id, updated));
    }

    // Delete moderator
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        moderationService.deleteModerator(id);
        return ResponseEntity.noContent().build();
    }

}