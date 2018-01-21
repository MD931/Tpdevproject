package com.tpdevproject.entities;

import com.google.firebase.database.PropertyName;

/**
 * Created by root on 18/12/17.
 */

public class Commentaire {

    @PropertyName("user_id")
    public String userId;

    @PropertyName("commentaire")
    private String commentaire;

    @PropertyName("date_post")
    public Long datePost;

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

    @Override
    public String toString(){
        return "{"
                +"user_id = "+userId
                +", commentaire = "+commentaire
                +", date_poste = "+datePost
                +"}";
    }
}
