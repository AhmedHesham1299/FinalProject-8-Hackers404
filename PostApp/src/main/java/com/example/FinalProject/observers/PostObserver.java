package com.example.FinalProject.observers;

import com.example.FinalProject.models.Comment;
import com.example.FinalProject.models.Like;
import com.example.FinalProject.models.Post;

public interface PostObserver {
    void onCommentAdded(Post post, Comment comment);
    void onPostLiked(Post post, Like like);
    void onCommentLiked(Comment comment, Like like);
} 