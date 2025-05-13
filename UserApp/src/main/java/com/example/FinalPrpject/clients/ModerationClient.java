package com.example.FinalPrpject.clients;

import com.example.FinalPrpject.DTO.Report;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "moderation-service", url = "http://localhost:8060/reports")
public interface ModerationClient {// In ModeratorApp
    @PostMapping
    ResponseEntity<Report> reportUser(@RequestBody Report report);

} 