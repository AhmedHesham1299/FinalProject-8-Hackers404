package com.example.FinalPrpject.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "moderation-service", url = "http://localhost:8091/moderation")
public interface ModerationClient {
    @PostMapping("/{reporterId}/report/{reportedId}")
    ResponseEntity<String> reportUser(@PathVariable Long reporterId, @PathVariable Long reportedId, @RequestBody Map<String, String> body);
} 