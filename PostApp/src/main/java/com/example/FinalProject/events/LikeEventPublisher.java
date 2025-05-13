package com.example.FinalProject.events;

import com.example.FinalProject.events.dtos.LikeEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LikeEventPublisher {
    private final RabbitTemplate rabbitTemplate;
    
    // Exchange and routing key constants
    public static final String LIKE_EVENTS_EXCHANGE = "like.events.exchange";
    public static final String LIKE_CREATED_ROUTING_KEY = "like.event.created";

    @Autowired
    public LikeEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendLikeEvent(LikeEvent event) {
        rabbitTemplate.convertAndSend(LIKE_EVENTS_EXCHANGE, LIKE_CREATED_ROUTING_KEY, event);
    }
} 