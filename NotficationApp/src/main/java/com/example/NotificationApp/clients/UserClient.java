package com.example.NotificationApp.clients;

import com.example.NotificationApp.model.NotificationPreferences;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


// TODO Change url to actual value
@FeignClient(name = "user-service", url = "http://localhost:8091/user")
public interface UserClient {

    // TODO Add getUserPreferences method in UserAppController
    @GetMapping("/users/{userId}/preferences")
    NotificationPreferences getPreferences(@PathVariable String userId);

    // TODO Add setUserPreferences method in UserAppController
    @PutMapping("/users/{userId}/preferences")
    boolean updatePreferences(@PathVariable String userId,
                           @RequestBody NotificationPreferences preferences);

}
