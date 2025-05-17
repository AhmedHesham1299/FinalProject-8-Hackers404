package com.example.NotificationApp.service;

import com.example.NotificationApp.clients.UserClient;
import com.example.NotificationApp.factory.NotificationHandlerFactory;
import com.example.NotificationApp.model.Notification;
import com.example.NotificationApp.model.NotificationPreferences;
import com.example.NotificationApp.rabbitmq.RabbitMQConfig;
import com.example.NotificationApp.repository.NotificationRepository;
import com.example.NotificationApp.strategy.NotificationHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {
    private final UserClient userClient;
    private final NotificationHandlerFactory notificationHandlerFactory;
    private final NotificationRepository notificationRepository;

    @Autowired
    public NotificationService(UserClient userClient, NotificationHandlerFactory notificationHandlerFactory, NotificationRepository notificationRepository) {
        this.userClient = userClient;
        this.notificationHandlerFactory = notificationHandlerFactory;
        this.notificationRepository = notificationRepository;
    }

    public List<Notification> getNotifications(String userId) {
        if(userId == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID can't be null");

        return notificationRepository.findByReceiverID(userId);
    }

    public Notification getNotificationById(String id) {
        Optional<Notification> optional = notificationRepository.findById(id);

        if(optional.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Notification not found");
        return optional.get();
    }

    public Notification updateNotification(String id, Notification updated) {
        if (id == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Notification ID can't be null");

        Notification notification = getNotificationById(id);
        notification.setContent(updated.getContent());
        notification.setRead(updated.isRead());

        return notificationRepository.save(notification);
    }

    public void deleteNotification(String id) {
        if(id == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Notification ID can't be null");

        notificationRepository.deleteById(id);
    }

    public List<Notification> filterNotificationsByDate(String userId, LocalDateTime from, LocalDateTime to) {
        if(userId == null || (from == null && to == null))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Fields can't be null");

        if (from != null && to != null)
            return notificationRepository.findByReceiverIDAndTimestampBetween(userId, from, to);
        else if (from != null)
            return notificationRepository.findByReceiverIDAndTimestampAfter(userId, from);
        return notificationRepository.findByReceiverIDAndTimestampBefore(userId, to);
    }

    public Notification markAsRead(String id) {
        if(id == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Notification ID can't be null");

        Notification notification = getNotificationById(id);
        if(!notification.isRead()) {
            notification.setRead(true);
            return notificationRepository.save(notification);
        }
        return notification;
    }

    public NotificationPreferences getPreferences(String userId) {
        NotificationPreferences preferences = userClient.getPreferences(userId);
        if (preferences == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found");
        }
        return preferences;
    }

    public String updatePreferences(String userId, NotificationPreferences preferences) {
        if (!userClient.updatePreferences(userId, preferences)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found");
        }
        return "Preferences updated successfully";
    }

    // TODO Listens to: comment on user's post, like on a user's post & tagging a user in a post
    // TODO Listens to: following a user & reporting a user
    @RabbitListener(queues = RabbitMQConfig.NOTIFICATION_QUEUE)
    public void onNewNotification(Notification notification) {
        NotificationPreferences preferences = null;
        try {
            preferences = userClient.getPreferences(notification.getReceiverID());
        } catch (Exception ex) {
            System.out.println("Failed to fetch notification preferences for user" + notification.getReceiverID() + ex.getMessage());
            preferences = new NotificationPreferences(false, false); // default: don't allow all notifications
        }

        String type = switch (notification.getType()) {
            case "REPORT", "FOLLOW", "UNFOLLOW", "BLOCK", "UNBLOCK" -> "Email";
            default -> "Push";
        };

        try{
            if(type.equals("Email") && preferences.isEmailEnabled() ||
                    type.equals("Push") && preferences.isPushEnabled()) {
                NotificationHandler handler = notificationHandlerFactory.getHandler(type);
                handler.sendNotification(notification);
            }
        }
        catch (Exception ex){
            System.out.println("Failed to send notification" + ex.getMessage());
        }

        notificationRepository.save(notification);
    }


}
