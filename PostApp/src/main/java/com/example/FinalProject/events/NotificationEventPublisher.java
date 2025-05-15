package com.example.FinalProject.events;

import com.example.FinalProject.events.dtos.NotificationEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("baseNotificationEventPublisher")
public class NotificationEventPublisher {
    private final RabbitTemplate rabbitTemplate;

    // Exchange and routing key constants
    public static final String NOTIFICATION_EXCHANGE = "notification.events.exchange";
    public static final String NOTIFICATION_ROUTING_KEY = "notification.event.created";

    @Autowired
    public NotificationEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendNotificationEvent(NotificationEvent event) {
        rabbitTemplate.convertAndSend(NOTIFICATION_EXCHANGE, NOTIFICATION_ROUTING_KEY, event);
    }
}