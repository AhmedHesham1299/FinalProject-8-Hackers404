//package com.example.FinalProject.services;
//
//import com.example.FinalProject.FinalProjectApplication;
//import com.example.FinalProject.config.TestConfig;
//import com.example.FinalProject.config.AppRabbitMQConfig;
//import com.example.FinalProject.models.Post;
//import com.example.FinalProject.models.Comment;
//import com.example.FinalProject.events.dtos.Notification;
//import com.example.FinalProject.services.TagService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.amqp.core.Binding;
//import org.springframework.amqp.core.BindingBuilder;
//import org.springframework.amqp.core.Queue;
//import org.springframework.amqp.core.TopicExchange;
//import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
//import org.springframework.amqp.rabbit.core.RabbitAdmin;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.web.client.TestRestTemplate;
//import org.springframework.boot.test.web.server.LocalServerPort;
//import org.springframework.context.annotation.Import;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.test.context.DynamicPropertyRegistry;
//import org.springframework.test.context.DynamicPropertySource;
//import org.testcontainers.containers.RabbitMQContainer;
//import org.testcontainers.junit.jupiter.Container;
//import org.testcontainers.junit.jupiter.Testcontainers;
//import com.example.FinalProject.events.CommentEventPublisher;
//import com.example.FinalProject.events.LikeEventPublisher;
//import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
//import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
//import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
//import org.springframework.test.context.ActiveProfiles;
//
//import java.util.Map;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@Testcontainers
//@SpringBootTest(classes = FinalProjectApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@EnableAutoConfiguration(exclude = { RedisAutoConfiguration.class, RedisRepositoriesAutoConfiguration.class })
//@ActiveProfiles("test")
//@Import(TestConfig.class)
//public class PostCombinedNotificationIntegrationTest {
//
//    @Container
//    static RabbitMQContainer rabbitContainer = new RabbitMQContainer("rabbitmq:3.8-management");
//
//    @DynamicPropertySource
//    static void rabbitProperties(DynamicPropertyRegistry registry) {
//        // RabbitMQ container properties
//        registry.add("spring.rabbitmq.host", rabbitContainer::getHost);
//        registry.add("spring.rabbitmq.port", rabbitContainer::getAmqpPort);
//        // MongoDB container properties from TestConfig
//        registry.add("spring.data.mongodb.uri", TestConfig.mongoDBContainer::getReplicaSetUrl);
//        registry.add("spring.data.mongodb.database", () -> "test");
//        // Disable Redis cache and use simple map cache
//        registry.add("spring.cache.type", () -> "simple");
//    }
//
//    @LocalServerPort
//    private int port; // random port assigned
//
//    @Autowired
//    private TestRestTemplate restTemplate;
//
//    @Autowired
//    private RabbitTemplate rabbitTemplate;
//
//    @Autowired
//    private TagService tagService;
//
//    private RabbitAdmin rabbitAdmin;
//    private Queue testQueue;
//
//    @BeforeEach
//    void setup() {
//        CachingConnectionFactory connectionFactory = (CachingConnectionFactory) rabbitTemplate.getConnectionFactory();
//        rabbitAdmin = new RabbitAdmin(connectionFactory);
//
//        testQueue = new Queue("test.queue", false, false, true);
//        rabbitAdmin.declareQueue(testQueue);
//
//        TopicExchange exchange = new TopicExchange(AppRabbitMQConfig.TARGET_APP_EXCHANGE);
//        Binding binding = BindingBuilder.bind(testQueue)
//                .to(exchange)
//                .with(AppRabbitMQConfig.TARGET_NOTIFICATION_ROUTING_KEY);
//        rabbitAdmin.declareBinding(binding);
//
//        // Declare comment and like event exchanges so publishing doesn't fail if no
//        // listener exists
//        rabbitAdmin.declareExchange(new TopicExchange(CommentEventPublisher.COMMENT_EVENTS_EXCHANGE));
//        rabbitAdmin.declareExchange(new TopicExchange(LikeEventPublisher.LIKE_EVENTS_EXCHANGE));
//    }
//
//    @Test
//    void whenComment_thenNotificationReceived() {
//        // Create a new post
//        Map<String, String> postReq = Map.of("title", "Post A", "content", "Body A", "authorId", "authorA");
//        String postUrl = "http://localhost:" + port + "/api/posts";
//        ResponseEntity<Post> pRes = restTemplate.postForEntity(postUrl, postReq, Post.class);
//        assertThat(pRes.getStatusCode()).isEqualTo(HttpStatus.OK);
//        String postId = pRes.getBody().getId();
//
//        // Comment on it
//        Map<String, String> cReq = Map.of("content", "Nice!", "authorId", "userB");
//        String cUrl = postUrl + "/" + postId + "/comments";
//        ResponseEntity<Comment> cRes = restTemplate.postForEntity(cUrl, cReq, Comment.class);
//        assertThat(cRes.getStatusCode()).isEqualTo(HttpStatus.CREATED);
//
//        // Expect COMMENT notification
//        Object msg1 = rabbitTemplate.receiveAndConvert(testQueue.getName(), 5000);
//        assertThat(msg1).isInstanceOf(Notification.class);
//        Notification n1 = (Notification) msg1;
//        assertThat(n1.getType()).isEqualTo("COMMENT");
//        assertThat(n1.getSenderID()).isEqualTo("userB");
//        assertThat(n1.getReceiverID()).isEqualTo("authorA");
//    }
//
//    @Test
//    void whenTag_thenNotificationReceived() {
//        // Create a new post
//        Map<String, String> postReq = Map.of("title", "Post B", "content", "Body B", "authorId", "authorX");
//        String postUrl = "http://localhost:" + port + "/api/posts";
//        ResponseEntity<Post> pRes = restTemplate.postForEntity(postUrl, postReq, Post.class);
//        assertThat(pRes.getStatusCode()).isEqualTo(HttpStatus.OK);
//        String postId = pRes.getBody().getId();
//
//        // Tag a user via service
//        tagService.tagUserInPost(postId, "userY", "userZ");
//
//        // Expect TAG notification
//        Object msg2 = rabbitTemplate.receiveAndConvert(testQueue.getName(), 5000);
//        assertThat(msg2).isInstanceOf(Notification.class);
//        Notification n2 = (Notification) msg2;
//        assertThat(n2.getType()).isEqualTo("TAG");
//        assertThat(n2.getSenderID()).isEqualTo("userY");
//        assertThat(n2.getReceiverID()).isEqualTo("userZ");
//    }
//
//    @Test
//    void whenPostLike_thenNotificationReceived() {
//        // Create a new post
//        Map<String, String> postReq = Map.of("title", "Post C", "content", "Body C", "authorId", "authorM");
//        String postUrl = "http://localhost:" + port + "/api/posts";
//        ResponseEntity<Post> pRes = restTemplate.postForEntity(postUrl, postReq, Post.class);
//        assertThat(pRes.getStatusCode()).isEqualTo(HttpStatus.OK);
//        String postId = pRes.getBody().getId();
//
//        // Like the post
//        Map<String, String> likeReq = Map.of("userId", "userL");
//        String likeUrl = postUrl + "/" + postId + "/like";
//        ResponseEntity<Void> lRes = restTemplate.postForEntity(likeUrl, likeReq, Void.class);
//        assertThat(lRes.getStatusCode()).isEqualTo(HttpStatus.OK);
//
//        // Expect LIKE notification
//        Object msg3 = rabbitTemplate.receiveAndConvert(testQueue.getName(), 5000);
//        assertThat(msg3).isInstanceOf(Notification.class);
//        Notification n3 = (Notification) msg3;
//        assertThat(n3.getType()).isEqualTo("LIKE");
//        assertThat(n3.getSenderID()).isEqualTo("userL");
//        assertThat(n3.getReceiverID()).isEqualTo("authorM");
//    }
//}