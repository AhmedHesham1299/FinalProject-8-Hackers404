package com.example.FinalPrpject.commands;

import com.example.FinalPrpject.models.BanPayload;
import com.example.FinalPrpject.services.UserFeignClient;
import com.example.FinalPrpject.strategies.BanStrategy;

public class BanCommand {

    private final BanPayload banPayload;
    private final UserFeignClient userFeignClient;
    private final BanStrategy banStrategy;

    public BanCommand(BanPayload banPayload, UserFeignClient userFeignClient, BanStrategy banStrategy) {
        this.banPayload = banPayload;
        this.userFeignClient = userFeignClient;
        this.banStrategy = banStrategy;
    }

    public void execute() {
        banStrategy.execute(banPayload);
        userFeignClient.banUser(banPayload.getUserId(), "MODERATOR");
    }

}