package com.example.FinalProject.config;

import com.example.FinalProject.events.CommentEventPublisher;
import com.example.FinalProject.events.LikeEventPublisher;
import com.example.FinalProject.events.NotificationEventPublisher;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;

@Configuration
public class RabbitMQConfig {

    public static final String USER_EVENTS_EXCHANGE = "user.events.exchange";
    public static final String USER_DELETED_ROUTING_KEY = "user.event.deleted";
    public static final String USER_BANNED_ROUTING_KEY = "user.event.banned";
    public static final String USER_EVENTS_QUEUE = "user.events.queue";

    public static final String POST_EVENTS_EXCHANGE = "post.events.exchange";
    public static final String POST_CREATED_ROUTING_KEY = "post.event.created";
    public static final String POST_UPDATED_ROUTING_KEY = "post.event.updated";

    @Bean
    public TopicExchange postEventsExchange() {
        return new TopicExchange(POST_EVENTS_EXCHANGE);
    }

    public static final String POST_QUEUE = "post_queue";
    public static final String NOTIFICATION_QUEUE = "notification_queue";
    public static final String EXCHANGE = "shared_exchange";
    public static final String POST_ROUTING = "post_routing";
    public static final String NOTIFICATION_ROUTING = "notification_routing";

    @Bean
    public Queue postQueue() {
        return new Queue(POST_QUEUE);
    }

    @Bean
    public Queue notificationQueue() {
        return new Queue(NOTIFICATION_QUEUE);
    }

    @Bean
    public TopicExchange sharedExchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Binding postBinding(TopicExchange sharedExchange) {
        return BindingBuilder
                .bind(postQueue())
                .to(sharedExchange)
                .with(POST_ROUTING);
    }

    @Bean
    public Binding notificationBinding(TopicExchange sharedExchange) {
        return BindingBuilder
                .bind(notificationQueue())
                .to(sharedExchange)
                .with(NOTIFICATION_ROUTING);
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

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

}