package com.example.FinalPrpject.services;

import com.example.FinalPrpject.commands.BanCommand;
import com.example.FinalPrpject.models.BanPayload;
import com.example.FinalPrpject.strategies.BanStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class BanCommandExecutor {

    private final ApplicationContext applicationContext;
    private final UserFeignClient userFeignClient;

    @Autowired
    public BanCommandExecutor(ApplicationContext applicationContext, UserFeignClient userFeignClient) {
        this.applicationContext = applicationContext;
        this.userFeignClient = userFeignClient;
    }

    public void executeBan(BanPayload payload) {
        BanStrategy strategy = (BanStrategy) applicationContext.getBean(payload.getBanType().name());
        BanCommand command = new BanCommand(payload, userFeignClient, strategy);
        command.execute();
    }

}