package com.example.FinalProject.events.dtos;

import java.time.LocalDateTime;

public record NotificationEvent(
        String notificationId,
        String recipientUserId,
        String message,
        String sourceEventId,
        LocalDateTime timestamp) {
}