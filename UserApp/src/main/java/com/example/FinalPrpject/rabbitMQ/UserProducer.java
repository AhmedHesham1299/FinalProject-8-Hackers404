package com.example.FinalPrpject.rabbitMQ;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserProducer {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void createPost(Long userId, String post) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.POST_ROUTING,
                post);
        System.out.println("User " + userId + " posted " + post);
    }

    public void sendNotificationEvent(Long userId, String action) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.NOTIFICATION_ROUTING,
                "User " + userId + " performed: " + action
        );
        System.out.println("User " + userId + " created notification event " + action);
    }

}
