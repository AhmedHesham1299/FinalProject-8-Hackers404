package com.example.FinalPrpject.services;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class UserServiceClient {

    private final RestTemplate restTemplate;

    public UserServiceClient(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    public void markUserAsBanned(Long userId) {
        String url = "http://USERAPP-SERVICE/users/" + userId + "/ban";
        restTemplate.put(url, null);
    }

    public void unBanUser(Long userId) {
        String url = "http://USERAPP-SERVICE/users/" + userId + "/unban";
        restTemplate.put(url, null);
    }

    public void sendWarningToUser(Long userId, String message) {
        String url = "http://USERAPP-SERVICE/users/" + userId + "/warn";
        restTemplate.postForEntity(url, message, Void.class);
    }

}