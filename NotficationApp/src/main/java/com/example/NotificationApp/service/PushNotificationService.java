package com.example.NotificationApp.service;

import com.example.NotificationApp.model.Notification;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import org.springframework.stereotype.Service;

@Service
public class PushNotificationService {
    public void sendPushNotification(Notification notification) {
        String DEVICE_TOKEN = "TEST_TOKEN";
        Message notificationMessage = Message.builder()
                .setToken(DEVICE_TOKEN)
                .putData("title", "New " + notification.getType().toLowerCase() + " notification")
                .putData("message", notification.getContent())
                .build();

        try {
            String response = FirebaseMessaging.getInstance().send(notificationMessage);
            System.out.println("Successfully sent message: " + response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
