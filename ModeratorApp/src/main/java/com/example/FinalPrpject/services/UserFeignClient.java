package com.example.FinalPrpject.services;

import com.example.FinalPrpject.models.BanRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "UserApp", url = "http://user-app:8080/users")
public interface UserFeignClient {

    @PutMapping("/{id}/ban")
    void banUser(@PathVariable("id") Long userId, @RequestHeader(value = "X-Role", required = false) String role, @RequestBody BanRequest banRequest);

    @PutMapping("/{id}/unban")
    void unbanUser(@PathVariable("id") Long userId, @RequestHeader(value = "X-Role", required = false) String role);

    @PostMapping("/{id}/warn")
    void warnUser(@PathVariable("id") Long userId, @RequestHeader(value = "X-Role", required = false) String role, @RequestBody String message);

}