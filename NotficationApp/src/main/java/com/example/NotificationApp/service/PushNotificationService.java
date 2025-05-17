package com.example.NotificationApp.service;

import com.example.NotificationApp.model.Notification;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import org.springframework.stereotype.Service;

@Service
public class PushNotificationService {
    public void sendPushNotification(Notification notification) {
        String DEVICE_TOKEN = "TEST_TOKEN";

        String content = switch (notification.getContent()) {
            case "liked" -> "liked your post.";
            case "commented" -> "commented on your post.";
            default -> "tagged you in a post.";
        };

        Message notificationMessage = Message.builder()
                .setToken(DEVICE_TOKEN)
                .putData("title", "New " + notification.getType().toLowerCase() + " notification")
                .putData("message", "User " + notification.getSenderName() + "has " +content)
                .putData("postID", notification.getPostID())
                .build();

        try {
            String response = FirebaseMessaging.getInstance().send(notificationMessage);
            System.out.println("Successfully sent message: " + response);
        } catch (Exception e) {
            System.out.println("Failed to  send message: " + e.getMessage());
        }
    }
}
