package com.example.FinalPrpject.commands;

import com.example.FinalPrpject.models.BanPayload;
import com.example.FinalPrpject.services.UserServiceClient;
import com.example.FinalPrpject.strategies.BanStrategy;

public class BanCommand {

    private final BanPayload payload;
    private final UserServiceClient userServiceClient;
    private final BanStrategy banStrategy;

    public BanCommand(BanPayload payload, UserServiceClient userServiceClient, BanStrategy banStrategy) {
        this.payload = payload;
        this.userServiceClient = userServiceClient;
        this.banStrategy = banStrategy;
    }

    public void execute() {
        banStrategy.execute(payload);
        userServiceClient.banUser(payload.getUserId());
    }

}