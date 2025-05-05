package com.example.NotificationApp.strategy;

import com.example.NotificationApp.model.Notification;
import com.example.NotificationApp.service.PushNotificationService;
import org.springframework.stereotype.Component;

@Component
public class PushNotificationHandler implements NotificationHandler {
    private final PushNotificationService pushNotificationService;

    @Au
    public PushNotificationHandler(PushNotificationService pushNotificationService) {
        this.pushNotificationService = pushNotificationService;
    }

    @Override
    public void sendNotification(Notification notification) {
        pushNotificationService.sendPushNotification(notification);

    }
}