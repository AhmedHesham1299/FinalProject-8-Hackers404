package com.example.NotificationApp.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "notifications")
public class Notification {
    // TODO Include attributes: senderID, receiverID, senderName, receiverName, receiverEmail,
    //  type, timestamp, content
    @Id
    private String id;
    private String senderID;
    private String receiverID;
    private String senderName;
    private String receiverName;
    private String receiverEmail;
    private String content;
    private String type;
    private LocalDateTime timestamp;
    private boolean read;
}
