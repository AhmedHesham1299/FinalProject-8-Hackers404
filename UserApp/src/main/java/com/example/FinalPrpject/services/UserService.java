package com.example.FinalPrpject.services;


import com.example.FinalPrpject.models.Report;
import com.example.FinalPrpject.models.User;
import com.example.FinalPrpject.repositories.ReportRepository;
import com.example.FinalPrpject.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private ReportRepository reportRepository;


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

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    public User updateUser(Long id, User updatedUser) {
        User user = getUserById(id);
        user.setUsername(updatedUser.getUsername());
        user.setEmail(updatedUser.getEmail());
        user.setPassword(updatedUser.getPassword());
        return userRepository.save(user);
    }
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        userRepository.deleteById(id);
    }

    public User editProfile(Long userId, String newUsername, String newEmail, String newProfileImageUrl) {
        User user = getUserById(userId);
        user.setUsername(newUsername);
        user.setEmail(newEmail);
        return userRepository.save(user);
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
    public void reportUser(Long reporterId, Long reportedId, String reason) {
        User reporter = getUserById(reporterId);
        User reported = getUserById(reportedId);

        Report report = new Report(reporter, reported, reason);
        reportRepository.save(report);
    }


}
