package com.example.NotificationApp.strategy;

import com.example.NotificationApp.model.Notification;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class EmailNotificationHandler implements NotificationHandler {
    private final JavaMailSender mailSender;


    public EmailNotificationHandler(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendNotification(Notification notification) {
        String userEmail = notification.getReceiverEmail();

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(userEmail);
        mailMessage.setSubject("New " + notification.getType().toLowerCase() + " notification");
        mailMessage.setText(notification.getContent());

        mailSender.send(mailMessage);
    }
}