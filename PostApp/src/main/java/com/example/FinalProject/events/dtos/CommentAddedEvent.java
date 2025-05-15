package com.example.FinalProject.events.dtos;

import java.time.LocalDateTime;

public record CommentAddedEvent(
        String eventId,
        String eventType,
        LocalDateTime eventTimestamp,
        String postId,
        String commentId,
        String commenterId,
        String postAuthorId,
        String commentTextPreview) {
}