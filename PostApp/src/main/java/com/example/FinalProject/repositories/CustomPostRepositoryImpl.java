package com.example.FinalProject.repositories;

import com.example.FinalProject.criteria.SearchCriteria;
import com.example.FinalProject.models.Post;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CustomPostRepositoryImpl implements CustomPostRepository {
    private final MongoTemplate mongoTemplate;

    public CustomPostRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<Post> searchPosts(SearchCriteria criteria) {
        Query query = new Query();
        List<Criteria> critList = new ArrayList<>();

        if (criteria.keywords() != null && !criteria.keywords().isBlank()) {
            String regex = ".*" + criteria.keywords() + ".*";
            critList.add(new Criteria().orOperator(
                    Criteria.where("title").regex(regex, "i"),
                    Criteria.where("content").regex(regex, "i")));
        }
        if (criteria.tags() != null && !criteria.tags().isEmpty()) {
            critList.add(Criteria.where("tags").in(criteria.tags()));
        }
        if (criteria.authorId() != null && !criteria.authorId().isBlank()) {
            critList.add(Criteria.where("authorId").is(criteria.authorId()));
        }
        if (criteria.startDate() != null) {
            LocalDateTime start = criteria.startDate().atStartOfDay();
            critList.add(Criteria.where("createdAt").gte(start));
        }
        if (criteria.endDate() != null) {
            LocalDateTime end = criteria.endDate().atTime(LocalTime.MAX);
            critList.add(Criteria.where("createdAt").lte(end));
        }
        if (!critList.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(critList.toArray(new Criteria[0])));
        }
        return mongoTemplate.find(query, Post.class);
    }
}