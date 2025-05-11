package com.example.FinalPrpject.models;

import jakarta.persistence.*;

@Entity
public class Moderator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;

    public Moderator() {}

    public Moderator(String username) {
        this.username = username;
    }

    public Moderator(Long id, String username) {
        this.id = id;
        this.username = username;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

}