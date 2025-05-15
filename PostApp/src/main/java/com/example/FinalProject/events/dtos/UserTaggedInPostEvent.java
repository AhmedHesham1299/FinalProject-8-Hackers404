package com.example.FinalProject.events.dtos;

import java.time.LocalDateTime;

public record UserTaggedInPostEvent(
        String eventId,
        String eventType,
        LocalDateTime eventTimestamp,
        String postId,
        String taggerId,
        String taggedUserId,
        String postAuthorId) {
}