package com.example.FinalPrpject.models;

import jakarta.persistence.*;

import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Table(name = "sessions")
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long sessionId;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String role;

    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;

    public Session() {
    }

    public Session(User user, Duration sessionDuration) {
        this.user = user;
        this.createdAt = LocalDateTime.now();
        this.expiresAt = this.createdAt.plus(sessionDuration);
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
}