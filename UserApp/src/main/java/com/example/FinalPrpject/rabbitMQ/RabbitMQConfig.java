package com.example.FinalPrpject.rabbitMQ;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMQConfig {
    public static final String POST_QUEUE = "post_queue";
    public static final String NOTIFICATION_QUEUE = "notification_queue";
    public static final String EXCHANGE
            = "shared_exchange";
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
    public Binding postBinding(TopicExchange exchange) {
        return BindingBuilder
                .bind(postQueue())
                .to(exchange)
                .with(POST_ROUTING);
    }

    @Bean
    public Binding notificationBinding(TopicExchange exchange) {
        return BindingBuilder
                .bind(notificationQueue())
                .to(exchange)
                .with(NOTIFICATION_ROUTING);
    }

}
