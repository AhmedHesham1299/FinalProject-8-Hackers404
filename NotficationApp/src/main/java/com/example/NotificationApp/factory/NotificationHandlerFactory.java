package com.example.NotificationApp.factory;

import com.example.NotificationApp.strategy.NotificationHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NotificationHandlerFactory {

    private final ApplicationContext context;

    @Autowired
    public NotificationHandlerFactory(ApplicationContext context) {
        this.context = context;
    }

    public NotificationHandler getHandler(String type) {
        try {
            String className = "com.example.NotificationApp.handler." + type + "NotificationHandler";
            Class<?> clazz = Class.forName(className);
            return (NotificationHandler) context.getBean(clazz);
        } catch (Exception e) {
            throw new RuntimeException("Handler type not found: " + type, e);
        }
    }
}
