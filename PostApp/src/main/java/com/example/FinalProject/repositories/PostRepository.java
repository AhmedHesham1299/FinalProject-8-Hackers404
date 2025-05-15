package com.example.FinalProject.repositories;

import com.example.FinalProject.models.Post;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface PostRepository extends MongoRepository<Post, String>, CustomPostRepository {
    List<Post> findByAuthorId(String authorId);
}