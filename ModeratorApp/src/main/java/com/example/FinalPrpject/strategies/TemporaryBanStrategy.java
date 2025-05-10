package com.example.FinalPrpject.strategies;

import com.example.FinalPrpject.models.BanPayload;
import org.springframework.stereotype.Component;

@Component("TEMPORARY")
public class TemporaryBanStrategy implements BanStrategy {

    @Override
    public void execute(BanPayload payload) {
        System.out.println("Temporary ban executed for user: " + payload.getUserId());
    }

}