package com.example.FinalPrpject.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class BanPayload {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    @Enumerated(EnumType.STRING)
    private BanType banType;
    private String reason;
    private LocalDateTime banDate;
    private int durationInDays;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public BanType getBanType() { return banType; }
    public void setBanType(BanType banType) { this.banType = banType; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public LocalDateTime getBanDate() { return banDate; }
    public void setBanDate(LocalDateTime banDate) { this.banDate = banDate; }

    public int getDurationInDays() { return durationInDays; }
    public void setDurationInDays(int durationInDays) { this.durationInDays = durationInDays; }

}
