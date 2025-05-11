package com.example.FinalPrpject.controllers;

import com.example.FinalPrpject.commands.BanCommand;
import com.example.FinalPrpject.commands.UnbanCommand;
import com.example.FinalPrpject.commands.WarnCommand;
import com.example.FinalPrpject.models.BanPayload;
import com.example.FinalPrpject.models.Moderator;
import com.example.FinalPrpject.repositories.BanPayloadRepository;
import com.example.FinalPrpject.services.ModeratorService;
import com.example.FinalPrpject.services.UserFeignClient;
import com.example.FinalPrpject.strategies.BanStrategy;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/moderators")
public class ModeratorController {

    private final ModeratorService moderatorService;
    private final UserFeignClient userFeignClient;
    private final ApplicationContext applicationContext;
    private final BanPayloadRepository banPayloadRepository;

    public ModeratorController(ModeratorService moderatorService, UserFeignClient userFeignClient, ApplicationContext applicationContext, BanPayloadRepository banPayloadRepository) {
        this.moderatorService = moderatorService;
        this.userFeignClient = userFeignClient;
        this.applicationContext = applicationContext;
        this.banPayloadRepository = banPayloadRepository;
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
    public ResponseEntity<String> banUser(@RequestBody BanPayload banPayload) {
        banPayload.setBanDate(LocalDateTime.now());
        BanStrategy banStrategy = (BanStrategy) applicationContext.getBean(banPayload.getBanType().name());
        BanCommand banCommand = new BanCommand(banPayload, userFeignClient, banStrategy);
        banCommand.execute();
        banPayloadRepository.save(banPayload);
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