package com.example.FinalProject.repositories;

import com.example.FinalProject.models.Bookmark;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookmarkRepository extends MongoRepository<Bookmark, String> {
    List<Bookmark> findByUserId(String userId);

    Optional<Bookmark> findByUserIdAndPostId(String userId, String postId);

    void deleteByUserIdAndPostId(String userId, String postId);

    boolean existsByUserIdAndPostId(String userId, String postId);
}