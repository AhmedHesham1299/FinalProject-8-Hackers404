package com.example.FinalPrpject.services;

import com.example.FinalPrpject.models.Session;
import com.example.FinalPrpject.repositories.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SessionService {

    @Autowired
    private SessionRepository sessionRepository;

    public SessionService(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @CachePut(value = "sessions", key = "#session.user.id")
    public Session createSession(Session session) {
        return sessionRepository.save(session);
    }

    public Session getSessionById(Long id) {
        return sessionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    @Cacheable(value = "sessions", key = "#userId", unless = "#result == null")
    public Optional<Session> getSessionByUserId(Long userId) {
        return sessionRepository.findByUserId(userId);
    }


    public List<Session> getAllSessions() {
        return sessionRepository.findAll();
    }

    @CacheEvict(value = "sessions", key = "#sessionId", allEntries = true)
    public void deleteSessionById(Long sessionId) {
        if (!sessionRepository.existsById(sessionId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Session not found");
        }
        sessionRepository.deleteById(sessionId);
    }

}
