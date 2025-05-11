package com.example.FinalPrpject.services;


import com.example.FinalPrpject.models.Session;
import com.example.FinalPrpject.models.User;
import com.example.FinalPrpject.repositories.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class SessionService {
    @Autowired
    private SessionRepository sessionRepository;

    public SessionService(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    public Session getSessionById(Long id) {
        return sessionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    public List<Session> getAllSessions() {
        return sessionRepository.findAll();
    }
}