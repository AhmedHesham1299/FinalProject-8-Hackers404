package com.example.FinalPrpject.services;


import com.example.FinalPrpject.DTO.Report;
import com.example.FinalPrpject.clients.ModerationClient;
import com.example.FinalPrpject.models.User;
import com.example.FinalPrpject.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private ModerationClient moderationClient;


    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    public User updateUser(Long id, User updatedUser) {
        User user = getUserById(id);

        if (!user.getUsername().equals(updatedUser.getUsername()) &&
                userRepository.existsByUsername(updatedUser.getUsername())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username is already taken");
        }

        if (!user.getEmail().equals(updatedUser.getEmail()) &&
                userRepository.existsByEmail(updatedUser.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already taken");
        }

        user.setUsername(updatedUser.getUsername());
        user.setEmail(updatedUser.getEmail());
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isBlank()) {
            user.setPassword(updatedUser.getPassword());
        }
        return userRepository.save(user);
    }
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        userRepository.deleteById(id);
    }

    public void followUser(Long userId, Long targetId) {
        User user = getUserById(userId);
        User target = getUserById(targetId);
        user.getFollowing().add(target);
        target.getFollowers().add(user);
        userRepository.save(user);
        userRepository.save(target);
    }

    public void unfollowUser(Long userId, Long targetId) {
        User user = getUserById(userId);
        User target = getUserById(targetId);
        user.getFollowing().remove(target);
        target.getFollowers().remove(user);
        userRepository.save(user);
        userRepository.save(target);
    }

    public void blockUser(Long userId, Long blockedUserId) {
        User user = getUserById(userId);
        User blocked = getUserById(blockedUserId);
        user.getBlockedUsers().add(blocked);
        userRepository.save(user);
    }
    public void unBlockUser(Long userId, Long blockedUserId) {
        User user = getUserById(userId);
        User blocked = getUserById(blockedUserId);
        user.getBlockedUsers().remove(blocked);
        userRepository.save(user);
    }
    public void reportUser(Long reporterId, Long reportedId, String reason) {
        getUserById(reporterId);
        getUserById(reportedId);
        Report report = new Report();
        report.setReporterUserId(reporterId);
        report.setReportedUserId(reportedId);
        report.setReason(reason);

        moderationClient.reportUser(report);
    }

    public void banUser(Long userId) {
        User user = getUserById(userId);

        if (user.isBanned()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User is already banned");
        }

        user.setBanned(true);
        userRepository.save(user);
    }

    public void unbanUser(Long userId) {
        User user = getUserById(userId);

        if (!user.isBanned()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User is not banned");
        }

        user.setBanned(false);
        userRepository.save(user);
    }

    public void warnUser(Long userId, String message) {
        User user = getUserById(userId);

        user.getWarnings().add(message);
        userRepository.save(user);
    }



}
