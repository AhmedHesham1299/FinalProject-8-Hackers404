package com.example.FinalProject.config;

import com.example.FinalProject.events.CommentEventPublisher;
import com.example.FinalProject.events.LikeEventPublisher;
import com.example.FinalProject.events.NotificationEventPublisher;
import com.example.FinalProject.events.PostEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.TopicExchange;

@Configuration
public class RabbitMQConfig {
    // Post events
    public static final String POST_EVENTS_EXCHANGE = "post.events.exchange";
    public static final String POST_CREATED_ROUTING_KEY = "post.event.created";
    public static final String POST_UPDATED_ROUTING_KEY = "post.event.updated";

    // User events that we'll listen to
    public static final String USER_EVENTS_EXCHANGE = "user.events.exchange";
    public static final String USER_DELETED_ROUTING_KEY = "user.event.deleted";
    public static final String USER_BANNED_ROUTING_KEY = "user.event.banned";
    public static final String USER_EVENTS_QUEUE = "user.events.queue";

    @Bean
    public TopicExchange postEventsExchange() {
        return new TopicExchange(POST_EVENTS_EXCHANGE);
    }

    @Bean
    public TopicExchange notificationEventsExchange() {
        return new TopicExchange(NotificationEventPublisher.NOTIFICATION_EXCHANGE);
    }

    @Bean
    public TopicExchange commentEventsExchange() {
        return new TopicExchange(CommentEventPublisher.COMMENT_EVENTS_EXCHANGE);
    }

    @Bean
    public TopicExchange likeEventsExchange() {
        return new TopicExchange(LikeEventPublisher.LIKE_EVENTS_EXCHANGE);
    }
}