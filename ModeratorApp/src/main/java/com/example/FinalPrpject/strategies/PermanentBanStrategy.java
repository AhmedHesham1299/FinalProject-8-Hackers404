package com.example.FinalPrpject.strategies;

import com.example.FinalPrpject.models.BanPayload;
import org.springframework.stereotype.Component;

@Component("PERMANENT")
public class PermanentBanStrategy implements BanStrategy {

    @Override
    public void execute(BanPayload banPayload) {
        System.out.println("Permanent ban executed for user: " + banPayload.getUserId());
    }

}