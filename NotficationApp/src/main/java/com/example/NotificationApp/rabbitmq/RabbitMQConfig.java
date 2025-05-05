package com.example.NotificationApp.rabbitmq;


import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMQConfig {

    public static final String NOTIFICATION_QUEUE = "notification_queue";

    public static final String EXCHANGE = "app_exchange";

    public static final String NOTIFICATION_ROUTING = "notification_routing";

    @Bean
    public Queue queue() {return new Queue(NOTIFICATION_QUEUE);}

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(NOTIFICATION_ROUTING);
    }


}
