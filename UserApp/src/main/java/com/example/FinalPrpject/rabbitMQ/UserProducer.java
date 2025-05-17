package com.example.FinalPrpject.rabbitMQ;

import com.example.FinalPrpject.DTO.Notification;
import com.example.FinalPrpject.DTO.Post;
import com.example.FinalPrpject.models.User;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserProducer {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void createPost(Long userId, String title, String content) {
        Post post = new Post(userId, content, title);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.POST_ROUTING,
                post);
        System.out.println("User " + userId + " posted " + post);
    }

    public void sendNotificationEvent(User reporter, User reported, String content, String type) {
        Notification event = new Notification(
                String.valueOf(reporter.getId()),
                String.valueOf(reported.getId()),
                reporter.getUsername(),
                reported.getUsername(),
                reported.getEmail(),
                content,
                type
        );
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.NOTIFICATION_ROUTING,
                event
        );
        System.out.println("User " + reporter.getId() + " created notification event " + type);
    }

}
