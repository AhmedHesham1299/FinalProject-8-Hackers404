package com.example.NotificationApp.repository;

import com.example.NotificationApp.model.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends MongoRepository<Notification, String> {

    List<Notification> findByReceiverID(String receiverID);

    List<Notification> findByReceiverIDAndTimestampBetween(String receiverID, LocalDateTime from, LocalDateTime to);

    List<Notification> findByReceiverIDAndTimestampAfter(String receiverID, LocalDateTime from);

    List<Notification> findByReceiverIDAndTimestampBefore(String receiverID, LocalDateTime to);

}
