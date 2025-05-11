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
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired private SessionRepository sessionRepository;
    @Autowired
    private SessionService sessionService;

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
        if (userRepository.existsByUsername(user.getUsername()) || userRepository.existsByEmail(user.getEmail())) {
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

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        if (!Objects.equals(password, user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
        Optional<Session> existingSessionOpt = sessionRepository.findByUserId(user.getId());
        if (existingSessionOpt.isPresent()) {
            Session existingSession = existingSessionOpt.get();
            if (existingSession.getExpiresAt().isAfter(LocalDateTime.now())) {
                return ResponseEntity.ok("Already logged in. Session ID: " + existingSession.getSessionId());
            } else {
                sessionRepository.delete(existingSession);
            }
        }


        Session session = new Session(user, Duration.ofHours(2));
        sessionRepository.save(session);

        return ResponseEntity.ok("Login successful");
    }

    @PostMapping("/logout/{sessionId}")
    public ResponseEntity<String> logout(@PathVariable Long sessionId) {
        sessionRepository.deleteById(sessionId);
        return ResponseEntity.ok("Logged out successfully");
    }
    @PutMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestHeader("Authorization") Long sessionId,
                                                @RequestBody Map<String, String> body) {
        String oldPassword = body.get("oldPassword");
        String newPassword = body.get("newPassword");

        Session session = sessionRepository.findBySessionId(sessionId)
                .filter(s -> s.getExpiresAt().isAfter(LocalDateTime.now()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Session invalid or expired"));

        User user = session.getUser();

        if (!user.getPassword().equals(oldPassword)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Old password is incorrect");
        }

        user.setPassword(newPassword);
        userRepository.save(user);

        return ResponseEntity.ok("Password updated successfully");
    }
}
