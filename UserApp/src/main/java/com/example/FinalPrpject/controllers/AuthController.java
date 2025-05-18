package com.example.FinalPrpject.controllers;

import com.example.FinalPrpject.models.Session;
import com.example.FinalPrpject.models.User;
import com.example.FinalPrpject.repositories.SessionRepository;
import com.example.FinalPrpject.repositories.UserRepository;
import com.example.FinalPrpject.services.SessionService;
import com.example.FinalPrpject.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;
    @Autowired
    private SessionService sessionService;

    public AuthController(SessionService sessionService, UserService userService) {
        this.sessionService = sessionService;
        this.userService = userService;
    }

    @GetMapping("/session")
    public ResponseEntity<List<Session>> getAllSessions() {
        return  ResponseEntity.ok(sessionService.getAllSessions());
    }
    @GetMapping("/session/{id}")
    public ResponseEntity<Session> getSessionById(@PathVariable Long id) {
        return  ResponseEntity.ok(sessionService.getSessionById(id));
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody User user) {
        if (userService.existsByUsername(user.getUsername()) || userService.existsByEmail(user.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username or email already exists");
        }

        user.setPassword(user.getPassword());
        userService.createUser(user);

        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");

        User user = userService.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        if (user.isBanned()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Your account has been banned. Please contact support.");
        }

        if (!Objects.equals(password, user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
        Session existingSession = sessionService.getSessionByUserId(user.getId()).orElse(null);
        if (existingSession != null) {
            if (existingSession.getExpiresAt().isAfter(LocalDateTime.now())) {
                return ResponseEntity.ok("Already logged in. Session ID: " + existingSession.getSessionId());
            } else {
                sessionService.deleteSessionById(existingSession.getSessionId());
            }
        }


        Session session = new Session(user, Duration.ofHours(2));
        sessionService.createSession(session);

        return ResponseEntity.ok("Login successful");
    }

    @PostMapping("/logout/{sessionId}")
    public ResponseEntity<String> logout(@PathVariable Long sessionId) {
        sessionService.deleteSessionById(sessionId);
        return ResponseEntity.ok("Logged out successfully");
    }
    @PutMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestHeader("Authorization") Long sessionId,
                                                @RequestBody Map<String, String> body) {
        String oldPassword = body.get("oldPassword");
        String newPassword = body.get("newPassword");

        Session session = sessionService.getSessionById(sessionId);
        if (session.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Session invalid or expired");
        }

        User user = session.getUser();

        if (!user.getPassword().equals(oldPassword)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Old password is incorrect");
        }

        user.setPassword(newPassword);
        userService.createUser(user);

        return ResponseEntity.ok("Password updated successfully");
    }
}
