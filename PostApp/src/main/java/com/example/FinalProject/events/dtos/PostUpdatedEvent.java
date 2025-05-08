package com.example.FinalProject.events.dtos;

import java.time.LocalDateTime;
import java.util.List;

public record PostUpdatedEvent(
        String eventId,
        String eventType,
        LocalDateTime eventTimestamp,
        PostEventPayload payload,
        List<String> updatedFields) {
}