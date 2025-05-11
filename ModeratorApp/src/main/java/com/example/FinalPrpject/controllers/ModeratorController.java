package com.example.FinalPrpject.controllers;

import com.example.FinalPrpject.commands.UnbanCommand;
import com.example.FinalPrpject.commands.WarnCommand;
import com.example.FinalPrpject.models.BanPayload;
import com.example.FinalPrpject.models.Moderator;
import com.example.FinalPrpject.services.BanCommandExecutor;
import com.example.FinalPrpject.services.ModeratorService;
import com.example.FinalPrpject.services.UserFeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/moderators")
public class ModeratorController {

    private final BanCommandExecutor banCommandExecutor;
    private final ModeratorService moderatorService;
    private final UserFeignClient userFeignClient;

    public ModeratorController(BanCommandExecutor banCommandExecutor, ModeratorService moderatorService, UserFeignClient userFeignClient) {
        this.banCommandExecutor = banCommandExecutor;
        this.moderatorService = moderatorService;
        this.userFeignClient = userFeignClient;
    }

    // Warn user
    @PostMapping("/warn")
    public ResponseEntity<String> warnUser(@RequestParam Long userId, @RequestBody String message) {
        WarnCommand warnCommand = new WarnCommand(userId, message, userFeignClient);
        warnCommand.execute();
        return ResponseEntity.ok("User warned successfully.");
    }

    // Ban user
    @PostMapping("/ban")
    public ResponseEntity<String> banUser(@RequestBody BanPayload payload) {
        payload.setBanDate(LocalDateTime.now());
        banCommandExecutor.executeBan(payload);
        return ResponseEntity.ok("User banned successfully.");
    }

    // Unban user
    @PostMapping("/unban")
    public ResponseEntity<String> unbanUser(@RequestParam Long userId) {
        UnbanCommand unbanCommand = new UnbanCommand(userId, userFeignClient);
        unbanCommand.execute();
        return ResponseEntity.ok("User unbanned successfully.");
    }

    // Create moderator
    @PostMapping
    public ResponseEntity<Moderator> create(@RequestBody Moderator moderator) {
        return ResponseEntity.ok(moderatorService.createModerator(moderator));
    }

    // Get all moderators
    @GetMapping
    public ResponseEntity<List<Moderator>> getAll() {
        return ResponseEntity.ok(moderatorService.getAllModerators());
    }

    // Get moderator by ID
    @GetMapping("/{id}")
    public ResponseEntity<Moderator> getById(@PathVariable Long id) {
        return moderatorService.getModeratorById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Update moderator
    @PutMapping("/{id}")
    public ResponseEntity<Moderator> update(@PathVariable Long id, @RequestBody Moderator updated) {
        return ResponseEntity.ok(moderatorService.updateModerator(id, updated));
    }

    // Delete moderator
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        moderatorService.deleteModerator(id);
        return ResponseEntity.noContent().build();
    }

}