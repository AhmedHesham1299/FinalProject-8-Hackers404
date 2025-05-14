package com.example.FinalPrpject.strategies;

import com.example.FinalPrpject.models.BanPayload;
import org.springframework.stereotype.Component;

@Component("SHADOW")
public class ShadowBanStrategy implements BanStrategy {

    @Override
    public void execute(BanPayload banPayload) {
        System.out.println("Shadow ban executed for user: " + banPayload.getUserId());
    }

}