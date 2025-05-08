package com.example.FinalProject.events.dtos;

import java.time.LocalDateTime;

public record PostCreatedEvent(
        String eventId,
        String eventType,
        LocalDateTime eventTimestamp,
        PostEventPayload payload) {
}