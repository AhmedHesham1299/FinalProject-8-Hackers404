package com.example.FinalPrpject.repositories;

import com.example.FinalPrpject.models.Session;
import com.example.FinalPrpject.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
    Optional<Session> findByUserId(Long userId);
    Optional<Session> findBySessionId(Long sessionId);
}