package com.example.FinalPrpject.commands;

import com.example.FinalPrpject.services.UserServiceClient;

public class WarnCommand {

    private final Long userId;
    private final String message;
    private final UserServiceClient userServiceClient;

    public WarnCommand(Long userId, String message, UserServiceClient userServiceClient) {
        this.userId = userId;
        this.message = message;
        this.userServiceClient = userServiceClient;
    }

    public void execute() {
        userServiceClient.sendWarningToUser(userId, message);
    }

}