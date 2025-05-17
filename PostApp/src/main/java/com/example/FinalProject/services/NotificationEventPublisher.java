package com.example.FinalProject.services;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import com.example.FinalProject.config.AppRabbitMQConfig;
import com.example.FinalProject.events.dtos.Notification;

@Service
public class NotificationEventPublisher {
    private final RabbitTemplate rabbitTemplate;

    public NotificationEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendNotification(Notification notification) {
        rabbitTemplate.convertAndSend(
                AppRabbitMQConfig.TARGET_APP_EXCHANGE,
                AppRabbitMQConfig.TARGET_NOTIFICATION_ROUTING_KEY,
                notification);
    }
}