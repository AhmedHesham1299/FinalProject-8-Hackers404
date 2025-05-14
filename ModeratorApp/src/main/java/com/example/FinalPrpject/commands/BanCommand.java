package com.example.FinalPrpject.commands;

import com.example.FinalPrpject.models.BanPayload;
import com.example.FinalPrpject.strategies.BanStrategy;

public class BanCommand {

    private final BanPayload banPayload;
    private final BanStrategy banStrategy;

    public BanCommand(BanPayload banPayload, BanStrategy banStrategy) {
        this.banPayload = banPayload;
        this.banStrategy = banStrategy;
    }

    public void execute() {
        banStrategy.execute(banPayload);
    }

}