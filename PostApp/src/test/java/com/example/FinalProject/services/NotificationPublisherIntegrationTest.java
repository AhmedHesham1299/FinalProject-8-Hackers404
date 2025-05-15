package com.example.FinalProject.services;

import com.example.FinalProject.config.AppRabbitMQConfig;
import com.example.FinalProject.events.dtos.CommentAddedEvent;
import com.example.FinalProject.events.dtos.PostLikedEvent;
import com.example.FinalProject.events.dtos.UserTaggedInPostEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest
public class NotificationPublisherIntegrationTest {

    @Container
    static RabbitMQContainer rabbitContainer = new RabbitMQContainer("rabbitmq:3.8-management");

    @DynamicPropertySource
    static void rabbitProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.rabbitmq.host", rabbitContainer::getHost);
        registry.add("spring.rabbitmq.port", rabbitContainer::getAmqpPort);
    }

    @Autowired
    private NotificationEventPublisher publisher;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private RabbitAdmin rabbitAdmin;
    private Queue testQueue;

    @BeforeEach
    void setup() {
        CachingConnectionFactory connectionFactory = (CachingConnectionFactory) rabbitTemplate.getConnectionFactory();
        rabbitAdmin = new RabbitAdmin(connectionFactory);

        testQueue = new Queue("test.queue", false, false, true);
        rabbitAdmin.declareQueue(testQueue);

        TopicExchange exchange = new TopicExchange(AppRabbitMQConfig.TARGET_APP_EXCHANGE);
        Binding binding = BindingBuilder.bind(testQueue)
                .to(exchange)
                .with(AppRabbitMQConfig.TARGET_NOTIFICATION_ROUTING_KEY);
        rabbitAdmin.declareBinding(binding);
    }

    @Test
    void whenPublishCommentAddedEvent_thenMessageReceived() {
        // given
        CommentAddedEvent event = new CommentAddedEvent(
                UUID.randomUUID().toString(),
                "COMMENT_ADDED",
                LocalDateTime.now(),
                "post-123",
                "comment-abc",
                "user-xyz",
                "author-123",
                "hello preview");

        // when
        publisher.sendCommentAddedEvent(event);

        // then
        Object received = rabbitTemplate.receiveAndConvert(testQueue.getName(), 5000);
        assertThat(received).isInstanceOf(CommentAddedEvent.class);
        CommentAddedEvent receivedEvent = (CommentAddedEvent) received;
        assertThat(receivedEvent.eventId()).isEqualTo(event.eventId());
    }

    @Test
    void whenPublishPostLikedEvent_thenMessageReceived() {
        // given
        PostLikedEvent event = new PostLikedEvent(
                UUID.randomUUID().toString(),
                "POST_LIKED",
                LocalDateTime.now(),
                "post-456",
                "liker-xyz",
                "author-123");

        // when
        publisher.sendPostLikedEvent(event);

        // then
        Object received = rabbitTemplate.receiveAndConvert(testQueue.getName(), 5000);
        assertThat(received).isInstanceOf(PostLikedEvent.class);
        PostLikedEvent receivedEvent = (PostLikedEvent) received;
        assertThat(receivedEvent.eventId()).isEqualTo(event.eventId());
    }

    @Test
    void whenPublishUserTaggedEvent_thenMessageReceived() {
        // given
        UserTaggedInPostEvent event = new UserTaggedInPostEvent(
                UUID.randomUUID().toString(),
                "USER_TAGGED_IN_POST",
                LocalDateTime.now(),
                "post-789",
                "tagger-abc",
                "tagged-xyz",
                "author-123");

        // when
        publisher.sendUserTaggedEvent(event);

        // then
        Object received = rabbitTemplate.receiveAndConvert(testQueue.getName(), 5000);
        assertThat(received).isInstanceOf(UserTaggedInPostEvent.class);
        UserTaggedInPostEvent receivedEvent = (UserTaggedInPostEvent) received;
        assertThat(receivedEvent.eventId()).isEqualTo(event.eventId());
    }
}