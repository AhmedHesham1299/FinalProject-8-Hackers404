package com.example.FinalProject.observers;

import com.example.FinalProject.models.Comment;
import com.example.FinalProject.models.Like;
import com.example.FinalProject.models.Post;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PostObservable {
    private final List<PostObserver> observers = new ArrayList<>();

    public void addObserver(PostObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(PostObserver observer) {
        observers.remove(observer);
    }

    public void notifyCommentAdded(Post post, Comment comment) {
        for (PostObserver observer : observers) {
            observer.onCommentAdded(post, comment);
        }
    }

    public void notifyPostLiked(Post post, Like like) {
        for (PostObserver observer : observers) {
            observer.onPostLiked(post, like);
        }
    }

    public void notifyCommentLiked(Comment comment, Like like) {
        for (PostObserver observer : observers) {
            observer.onCommentLiked(comment, like);
        }
    }
} 