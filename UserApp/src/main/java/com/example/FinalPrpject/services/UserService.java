package com.example.FinalPrpject.services;


import com.example.FinalPrpject.DTO.NotificationPreferences;
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
        if(userId.equals(targetId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot follow yourself");
        }
        User user = getUserById(userId);
        User target = getUserById(targetId);
        if(user.getFollowing().contains(target)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "You are already following User "+target.getId() );
        }
        user.getFollowing().add(target);
        target.getFollowers().add(user);
        userRepository.save(user);
        userRepository.save(target);
    }

    public void unfollowUser(Long userId, Long targetId) {
        if(userId.equals(targetId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot unfollow yourself");
        }
        User user = getUserById(userId);
        User target = getUserById(targetId);
        if(!user.getFollowing().contains(target)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "You are not following User "+target.getId() );
        }
        user.getFollowing().remove(target);
        target.getFollowers().remove(user);
        userRepository.save(user);
        userRepository.save(target);
    }

    public void blockUser(Long userId, Long blockedUserId) {
        if(userId.equals(blockedUserId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot block yourself");
        }
        User user = getUserById(userId);
        User blocked = getUserById(blockedUserId);
        if(user.getBlockedUsers().contains(blocked)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "You already blocked "+blocked.getId() );
        }
        user.getBlockedUsers().add(blocked);
        userRepository.save(user);
    }
    public void unBlockUser(Long userId, Long blockedUserId) {
        if(userId.equals(blockedUserId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot unblock yourself");
        }
        User user = getUserById(userId);
        User blocked = getUserById(blockedUserId);
        if(!user.getBlockedUsers().contains(blocked)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User "+blocked.getId()+" is not blocked" );
        }
        user.getBlockedUsers().remove(blocked);
        userRepository.save(user);
    }
    public void reportUser(Long reporterId, Long reportedId, String reason) {
        if(reporterId.equals(reportedId)){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot report yourself");
        }
        getUserById(reporterId);
        getUserById(reportedId);
        Report report = new Report();
        report.setReporterUserId(reporterId);
        report.setReportedUserId(reportedId);
        report.setReason(reason);

        moderationClient.reportUser(report);
    }

    public boolean updatePreferences(Long userId, NotificationPreferences preferences) {
        User user = getUserById(userId);
        user.setPushEnabled(preferences.isPushEnabled());
        user.setEmailEnabled(preferences.isEmailEnabled());
        userRepository.save(user);
        return true;
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
