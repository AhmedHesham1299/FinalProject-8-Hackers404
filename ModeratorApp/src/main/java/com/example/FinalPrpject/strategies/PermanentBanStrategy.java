package com.example.FinalPrpject.strategies;

import com.example.FinalPrpject.models.BanPayload;
import com.example.FinalPrpject.models.BanRequest;
import com.example.FinalPrpject.services.UserFeignClient;
import org.springframework.stereotype.Component;

@Component("PERMANENT")
public class PermanentBanStrategy implements BanStrategy {

    private final UserFeignClient userFeignClient;

    public PermanentBanStrategy(UserFeignClient userFeignClient) {
        this.userFeignClient = userFeignClient;
    }

    @Override
    public void execute(BanPayload banPayload) {
        BanRequest banRequest = new BanRequest(banPayload.getBanType().name(), banPayload.getReason(), 0);
        userFeignClient.banUser(banPayload.getUserId(), "MODERATOR", banRequest);
        System.out.println("Permanent ban executed for user: " + banPayload.getUserId());
    }

}