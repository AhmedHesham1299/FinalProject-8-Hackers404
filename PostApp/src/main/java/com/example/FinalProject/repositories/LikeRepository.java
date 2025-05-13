package com.example.FinalProject.repositories;

import com.example.FinalProject.models.Like;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikeRepository extends MongoRepository<Like, String> {
    
    Optional<Like> findByUserIdAndTargetIdAndTargetType(String userId, String targetId, String targetType);
    
    List<Like> findByTargetIdAndTargetType(String targetId, String targetType);
    
    void deleteByTargetIdAndTargetType(String targetId, String targetType);
    
    long countByTargetIdAndTargetTypeAndIsLike(String targetId, String targetType, boolean isLike);
} 