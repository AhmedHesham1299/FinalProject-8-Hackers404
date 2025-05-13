package com.example.FinalProject.repositories;

import com.example.FinalProject.models.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends MongoRepository<Comment, String> {
    
    List<Comment> findByPostId(String postId);
    
    List<Comment> findByAuthorId(String authorId);
    
    void deleteByPostId(String postId);
} 