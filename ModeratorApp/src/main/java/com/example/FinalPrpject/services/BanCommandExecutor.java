package com.example.FinalPrpject.services;

import com.example.FinalPrpject.commands.BanCommand;
import com.example.FinalPrpject.models.BanPayload;
import com.example.FinalPrpject.strategies.BanStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class BanCommandExecutor {

    private final ApplicationContext context;
    private final UserServiceClient userServiceClient;

    @Autowired
    public BanCommandExecutor(ApplicationContext context, UserServiceClient userServiceClient) {
        this.context = context;
        this.userServiceClient = userServiceClient;
    }

    public void executeBan(BanPayload payload) {
        BanStrategy strategy = (BanStrategy) context.getBean(payload.getBanType().name());
        BanCommand command = new BanCommand(payload, userServiceClient, strategy);
        command.execute();
    }

}