//package com.example.FinalProject.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.amqp.core.TopicExchange;
//
//@Configuration
//public class AppRabbitMQConfig {
//    public static final String TARGET_APP_EXCHANGE = "shared_exchange";
//    public static final String TARGET_NOTIFICATION_ROUTING_KEY = "notification_routing";
//
//    @Bean
//    public TopicExchange targetAppExchange() {
//        return new TopicExchange(TARGET_APP_EXCHANGE);
//    }
//}