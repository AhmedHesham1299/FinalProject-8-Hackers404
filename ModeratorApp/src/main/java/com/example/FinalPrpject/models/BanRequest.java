package com.example.FinalPrpject.models;

public record BanRequest(
     String banType,        // "PERMANENT", "TEMPORARY", "SHADOW"
     String reason,         // reason for ban
     int durationInDays     // if(banType=="TEMPORARY")
){}