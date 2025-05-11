package com.example.FinalPrpject.models;

import jakarta.persistence.*;

import java.security.PublicKey;

@Entity
public class Moderator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String role;
    private boolean active;

    public Moderator() {}

    public Moderator(String username, String role, boolean active) {
        this.username = username;
        this.role = role;
        this.active = active;
    }

    public Moderator(Long id, String username, String role, boolean active) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.active = active;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

}