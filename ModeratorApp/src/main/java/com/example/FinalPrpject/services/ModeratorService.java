package com.example.FinalPrpject.services;

import com.example.FinalPrpject.models.Moderator;
import com.example.FinalPrpject.repositories.ModeratorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ModeratorService {

    private final ModeratorRepository moderatorRepository;

    public ModerationService(ModeratorRepository moderatorRepository) {
        this.moderatorRepository = moderatorRepository;
    }

    public Moderator createModerator(Moderator moderator) {
        return moderatorRepository.save(moderator);
    }

    public List<Moderator> getAllModerators() {
        return moderatorRepository.findAll();
    }

    public Optional<Moderator> getModeratorById(Long id) {
        return moderatorRepository.findById(id);
    }

    public Moderator updateModerator(Long id, Moderator updated) {
        return moderatorRepository.findById(id).map(moderator -> {
            moderator.setUsername(updated.getUsername());
            moderator.setRole(updated.getRole());
            moderator.setActive(updated.isActive());
            return moderatorRepository.save(moderator);
        }).orElseThrow(() -> new RuntimeException("Moderator not found"));
    }

    public void deleteModerator(Long id) {
        moderatorRepository.deleteById(id);
    }

}