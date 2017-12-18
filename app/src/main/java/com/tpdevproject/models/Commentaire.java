package com.tpdevproject.models;

import com.google.firebase.database.PropertyName;

/**
 * Created by root on 18/12/17.
 */

public class Commentaire {

    @PropertyName("user_id")
    private String userId;

    @PropertyName("com")
    private String commentaire;

    @PropertyName("date_post")
    private Long datePost;

    public Commentaire(){}

    public Commentaire(String userId, String commentaire, Long datePost) {
        this.userId = userId;
        this.commentaire = commentaire;
        this.datePost = datePost;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public Long getDatePost() {
        return datePost;
    }

    public void setDatePost(Long datePost) {
        this.datePost = datePost;
    }
}
