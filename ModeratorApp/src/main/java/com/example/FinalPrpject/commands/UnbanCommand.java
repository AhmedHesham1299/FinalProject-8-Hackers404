package com.example.FinalPrpject.commands;

import com.example.FinalPrpject.services.UserFeignClient;

public class UnbanCommand {

    private final Long userId;
    private final UserFeignClient userFeignClient;

    public UnbanCommand(Long userId, UserFeignClient userFeignClient) {
        this.userId = userId;
        this.userFeignClient = userFeignClient;
    }

    public void execute() {
        userFeignClient.unbanUser(userId);
    }

}