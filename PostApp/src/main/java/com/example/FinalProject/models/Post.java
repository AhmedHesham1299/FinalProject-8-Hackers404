package com.example.FinalProject.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Document(collection = "posts")
public class Post {
    @Id
    private String id;
    private String title;
    private String content;
    private String authorId;
    private List<String> tags;
    private LocalDateTime createdAt;
    private List<Comment> comments;
    private int likes;
    private int dislikes;

    public Post() {
        this.tags = new ArrayList<>();
        this.comments = new ArrayList<>();
        this.likes = 0;
        this.dislikes = 0;
    }


    private Post(String title, String content, String authorId, List<String> tags,
                 LocalDateTime createdAt, List<Comment> comments, int likes, int dislikes) {
        this.title = title;
        this.content = content;
        this.authorId = authorId;
        this.tags = (tags != null) ? tags : new ArrayList<>();
        this.createdAt = createdAt;
        this.comments = (comments != null) ? comments : new ArrayList<>();
        this.likes = likes;
        this.dislikes = dislikes;
    }

    public static PostBuilder builder(String title, String content, String authorId) {
        return new PostBuilder(title, content, authorId);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getDislikes() {
        return dislikes;
    }

    public void setDislikes(int dislikes) {
        this.dislikes = dislikes;
    }

    public static class PostBuilder {
        private String title;
        private String content;
        private String authorId;
        private List<String> tags;
        private LocalDateTime createdAt;
        private List<Comment> comments;
        private int likes;
        private int dislikes;

        public PostBuilder(String title, String content, String authorId) {
            if (title == null || title.trim().isEmpty()) {
                throw new IllegalArgumentException("Title cannot be null or empty");
            }
            if (content == null || content.trim().isEmpty()) {
                throw new IllegalArgumentException("Content cannot be null or empty");
            }
            if (authorId == null || authorId.trim().isEmpty()) {
                throw new IllegalArgumentException("AuthorId cannot be null or empty");
            }
            this.title = title;
            this.content = content;
            this.authorId = authorId;
            this.tags = new ArrayList<>();
            this.comments = new ArrayList<>();
            this.likes = 0;
            this.dislikes = 0;
        }

        public PostBuilder tags(List<String> tags) {
            this.tags = (tags != null) ? tags : new ArrayList<>();
            return this;
        }

        public PostBuilder comments(List<Comment> comments) {
            this.comments = (comments != null) ? comments : new ArrayList<>();
            return this;
        }

        public PostBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public PostBuilder likes(int likes) {
            if (likes < 0) {
                throw new IllegalArgumentException("Likes cannot be negative");
            }
            this.likes = likes;
            return this;
        }

        public PostBuilder dislikes(int dislikes) {
            if (dislikes < 0) {
                throw new IllegalArgumentException("Dislikes cannot be negative");
            }
            this.dislikes = dislikes;
            return this;
        }

        public Post build() {
            LocalDateTime finalCreatedAt = (this.createdAt != null) ? this.createdAt : LocalDateTime.now();
            return new Post(title, content, authorId, tags, finalCreatedAt, comments, likes, dislikes);
        }
    }
}