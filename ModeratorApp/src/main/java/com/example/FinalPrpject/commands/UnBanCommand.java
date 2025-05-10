package com.example.FinalPrpject.commands;

import com.example.FinalPrpject.services.UserServiceClient;

public class UnBanCommand {

    private final Long userId;
    private final UserServiceClient userServiceClient;

    public UnBanCommand(Long userId, UserServiceClient userServiceClient) {
        this.userId = userId;
        this.userServiceClient = userServiceClient;
    }

    public void execute() {
        userServiceClient.unBanUser(userId);
    }

}