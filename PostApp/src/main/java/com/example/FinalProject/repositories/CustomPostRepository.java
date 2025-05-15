package com.example.FinalProject.repositories;

import com.example.FinalProject.criteria.SearchCriteria;
import com.example.FinalProject.models.Post;
import java.util.List;

public interface CustomPostRepository {
    List<Post> searchPosts(SearchCriteria criteria);
}