package com.example.FinalProject.events.dtos;

import java.time.LocalDateTime;

public record PostEventPayload(
        String postId,
        String title,
        String authorId,
        LocalDateTime createdAt) {
}