package com.example.NotificationApp.strategy;

import com.example.NotificationApp.model.Notification;

public interface NotificationHandler {
    void sendNotification(Notification notification);
}