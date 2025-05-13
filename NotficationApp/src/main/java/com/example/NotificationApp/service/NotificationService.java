package com.example.NotificationApp.service;

import com.example.NotificationApp.clients.UserClient;
import com.example.NotificationApp.factory.NotificationHandlerFactory;
import com.example.NotificationApp.model.Notification;
import com.example.NotificationApp.model.NotificationPreferences;
import com.example.NotificationApp.rabbitmq.RabbitMQConfig;
import com.example.NotificationApp.strategy.NotificationHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class NotificationService {
    private final UserClient userClient;
    private final NotificationHandlerFactory notificationHandlerFactory;

    public NotificationService(UserClient userClient, NotificationHandlerFactory notificationHandlerFactory) {
        this.userClient = userClient;
        this.notificationHandlerFactory = notificationHandlerFactory;
    }

    public NotificationPreferences getPreferences(String userId) {
        NotificationPreferences preferences = userClient.getPreferences(userId);
        if (preferences == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found");
        }
        return preferences;
    }

    public String updatePreferences(String userId, NotificationPreferences preferences) {
        if (!userClient.updatePreferences(userId, preferences)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found");
        }
        return "Preferences updated successfully";
    }

    // TODO Listens to: comment on user's post, like on a user's post & tagging a user in a post
    // TODO Listens to: following a user & reporting a user
    @RabbitListener(queues = RabbitMQConfig.NOTIFICATION_QUEUE)
    public void onNewNotification(Notification notification) {
        String type = switch (notification.getType()) {
            case "REPORT", "FOLLOW" -> "Email";
            default -> "Push";
        };

        NotificationHandler handler = notificationHandlerFactory.getHandler(type);
        handler.sendNotification(notification);
    }


}
