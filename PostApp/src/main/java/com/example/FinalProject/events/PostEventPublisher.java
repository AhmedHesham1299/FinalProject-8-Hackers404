package com.example.FinalProject.events;

import com.example.FinalProject.config.RabbitMQConfig;
import com.example.FinalProject.events.dtos.PostCreatedEvent;
import com.example.FinalProject.events.dtos.PostUpdatedEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class PostEventPublisher {
    private final RabbitTemplate rabbitTemplate;

    public PostEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendPostCreatedEvent(PostCreatedEvent event) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.POST_EVENTS_EXCHANGE, RabbitMQConfig.POST_CREATED_ROUTING_KEY,
                event);
    }

    public void sendPostUpdatedEvent(PostUpdatedEvent event) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.POST_EVENTS_EXCHANGE, RabbitMQConfig.POST_UPDATED_ROUTING_KEY,
                event);
    }
}