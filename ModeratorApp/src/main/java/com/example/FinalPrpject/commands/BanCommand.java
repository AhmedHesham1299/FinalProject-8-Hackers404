package com.example.FinalPrpject.commands;

import com.example.FinalPrpject.models.BanPayload;
import com.example.FinalPrpject.services.UserFeignClient;
import com.example.FinalPrpject.strategies.BanStrategy;

public class BanCommand {

    private final BanPayload payload;
    private final UserFeignClient userFeignClient;
    private final BanStrategy banStrategy;

    public BanCommand(BanPayload payload, UserFeignClient userFeignClient, BanStrategy banStrategy) {
        this.payload = payload;
        this.userFeignClient = userFeignClient;
        this.banStrategy = banStrategy;
    }

    public void execute() {
        banStrategy.execute(payload);
        userFeignClient.banUser(payload.getUserId());
    }

}