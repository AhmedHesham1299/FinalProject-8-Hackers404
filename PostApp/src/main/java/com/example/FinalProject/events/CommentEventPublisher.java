package com.example.FinalProject.events;

import com.example.FinalProject.events.dtos.CommentCreatedEvent;
import com.example.FinalProject.events.dtos.CommentUpdatedEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentEventPublisher {
    private final RabbitTemplate rabbitTemplate;
    
    // Exchange and routing key constants
    public static final String COMMENT_EVENTS_EXCHANGE = "comment.events.exchange";
    public static final String COMMENT_CREATED_ROUTING_KEY = "comment.event.created";
    public static final String COMMENT_UPDATED_ROUTING_KEY = "comment.event.updated";

    @Autowired
    public CommentEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendCommentCreatedEvent(CommentCreatedEvent event) {
        rabbitTemplate.convertAndSend(COMMENT_EVENTS_EXCHANGE, COMMENT_CREATED_ROUTING_KEY, event);
    }

    public void sendCommentUpdatedEvent(CommentUpdatedEvent event) {
        rabbitTemplate.convertAndSend(COMMENT_EVENTS_EXCHANGE, COMMENT_UPDATED_ROUTING_KEY, event);
    }
} 