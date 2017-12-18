package com.tpdevproject.models;

import com.google.firebase.database.PropertyName;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by root on 09/12/17.
 */

public class Annonce {
    private String id;
    @PropertyName("title")
    private String title;
    @PropertyName("description")
    private String description;
    @PropertyName("date_begin")
    private String dateBegin;
    @PropertyName("date_end")
    private String dateEnd;
    @PropertyName("date_post")
    private Long datePost;
    @PropertyName("commentaires")
    private Map<String, Commentaire> commentaires = new HashMap<>();

    private String image;
    private int score;
    public Annonce() {}  // Needed for Firebase


    public Annonce(String id, String title, String description, String dateBegin, String dateEnd, Long datePost, Map<String, Commentaire> commentaires) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.dateBegin = dateBegin;
        this.dateEnd = dateEnd;
        this.datePost = datePost;
        this.commentaires = commentaires;
    }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getDateBegin() {
        return dateBegin;
    }

    public void setDateBegin(String dateBegin) {
        this.dateBegin = dateBegin;
    }

    public String getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(String dateEnd) {
        this.dateEnd = dateEnd;
    }

    public Long getDatePost() {
        return datePost;
    }

    public void setDatePost(Long datePost) {
        this.datePost = datePost;
    }

    public Map<String, Commentaire> getCommentaires() {
        return commentaires;
    }

    public void setCommentaires(Map<String, Commentaire> commentaires) {
        this.commentaires = commentaires;
    }

    public int getNumberCommentaires(){
        return this.commentaires.size();
    }

}
