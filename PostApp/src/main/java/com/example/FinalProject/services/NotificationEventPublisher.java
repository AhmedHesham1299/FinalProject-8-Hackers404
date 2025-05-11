package com.example.FinalProject.services;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import com.example.FinalProject.config.AppRabbitMQConfig;
import com.example.FinalProject.events.dtos.CommentAddedEvent;
import com.example.FinalProject.events.dtos.PostLikedEvent;
import com.example.FinalProject.events.dtos.UserTaggedInPostEvent;

@Service
public class NotificationEventPublisher {
    private final RabbitTemplate rabbitTemplate;

    public NotificationEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendCommentAddedEvent(CommentAddedEvent event) {
        rabbitTemplate.convertAndSend(
                AppRabbitMQConfig.TARGET_APP_EXCHANGE,
                AppRabbitMQConfig.TARGET_NOTIFICATION_ROUTING_KEY,
                event);
    }

    public void sendPostLikedEvent(PostLikedEvent event) {
        rabbitTemplate.convertAndSend(
                AppRabbitMQConfig.TARGET_APP_EXCHANGE,
                AppRabbitMQConfig.TARGET_NOTIFICATION_ROUTING_KEY,
                event);
    }

    public void sendUserTaggedEvent(UserTaggedInPostEvent event) {
        rabbitTemplate.convertAndSend(
                AppRabbitMQConfig.TARGET_APP_EXCHANGE,
                AppRabbitMQConfig.TARGET_NOTIFICATION_ROUTING_KEY,
                event);
    }
}