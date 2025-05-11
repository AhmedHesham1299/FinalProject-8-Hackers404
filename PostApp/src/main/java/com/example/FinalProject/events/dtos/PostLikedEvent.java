package com.example.FinalProject.events.dtos;

import java.time.LocalDateTime;

public record PostLikedEvent(
        String eventId,
        String eventType,
        LocalDateTime eventTimestamp,
        String postId,
        String likerId,
        String postAuthorId) {
}