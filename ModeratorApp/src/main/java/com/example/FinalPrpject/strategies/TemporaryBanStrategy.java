package com.example.FinalPrpject.strategies;

import com.example.FinalPrpject.models.BanPayload;
import com.example.FinalPrpject.models.BanRequest;
import com.example.FinalPrpject.services.UserFeignClient;
import org.springframework.stereotype.Component;

@Component("TEMPORARY")
public class TemporaryBanStrategy implements BanStrategy {

    private final UserFeignClient userFeignClient;

    public TemporaryBanStrategy(UserFeignClient userFeignClient) {
        this.userFeignClient = userFeignClient;
    }

    @Override
    public void execute(BanPayload banPayload) {
        BanRequest banRequest = new BanRequest(banPayload.getBanType().name(), banPayload.getReason(), banPayload.getDurationInDays());
        userFeignClient.banUser(banPayload.getUserId(), "MODERATOR", banRequest);
        System.out.println("Temporary ban executed for user: " + banPayload.getUserId());
    }

}