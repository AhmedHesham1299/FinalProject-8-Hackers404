package com.example.FinalPrpject.commands;

import com.example.FinalPrpject.services.UserFeignClient;

public class WarnCommand {

    private final Long userId;
    private final String message;
    private final UserFeignClient userFeignClient;

    public WarnCommand(Long userId, String message, UserFeignClient userFeignClient) {
        this.userId = userId;
        this.message = message;
        this.userFeignClient = userFeignClient;
    }

    public void execute() {
        userFeignClient.warnUser(userId, message);
    }

}