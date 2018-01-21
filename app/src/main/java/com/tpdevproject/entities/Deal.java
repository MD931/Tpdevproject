package com.tpdevproject.entities;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.PropertyName;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by root on 09/12/17.
 */

public class Deal {

    private String id;

    @PropertyName("title")
    private String title;

    @PropertyName("description")
    private String description;

    @PropertyName("price_deal")
    public Double priceDeal;

    @PropertyName("price")
    private Double price;

    @PropertyName("link")
    private String link;

    @PropertyName("date_begin")
    public String dateBegin;

    @PropertyName("date_end")
    public String dateEnd;

    @PropertyName("date_post")
    public Long datePost;

    /*@PropertyName("commentaires")
    private Map<String, Commentaire> commentaires = new HashMap<>();*/

    @PropertyName("order")
    private Integer order;

    @PropertyName("address")
    private String address;

    @PropertyName("votes")
    private Map<String, Integer> votes = new HashMap<>();

    @PropertyName("favoris")
    private Map<String, Integer> favoris = new HashMap<>();

    @PropertyName("user_id")
    public String userId;

    private String username;
    private String image;
    private int score;
    private long numberCommentaires = 0;

    public Deal() {}


    public Deal(String id, String title, String description, Double price,
                String link, String dateBegin, String dateEnd, Long datePost,
                   /*Map<String, Commentaire> commentaires,*/
                   Integer order, String address, Map<String, Integer> votes, Map<String, Integer> favoris) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.link = link;
        this.dateBegin = dateBegin;
        this.dateEnd = dateEnd;
        this.datePost = datePost;
        //this.commentaires = commentaires;
        this.order = order;
        this.address = address;
        this.votes = votes;
        this.favoris = favoris;
    }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) { this.title = title; }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() { return price; }

    public void setPrice(Double price) { this.price = price; }

    public String getLink() { return link; }

    public void setLink(String link) { this.link = link; }

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

    /*public Map<String, Commentaire> getCommentaires() {
        return commentaires;
    }

    public void setCommentaires(Map<String, Commentaire> commentaires) {
        this.commentaires = commentaires;
    }*/

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Map<String, Integer> getVotes() {
        return votes;
    }

    public void setVotes(Map<String, Integer> votes) {
        this.votes = votes;
    }

    public Map<String, Integer> getFavoris() {
        return favoris;
    }

    public void setFavoris(Map<String, Integer> favoris) {
        this.favoris = favoris;
    }

    public void setNumberCommentaires(long numberCommentaires){
        this.numberCommentaires = numberCommentaires;
    }

    public long getNumberCommentaires(){
        return numberCommentaires;
    }

    @Exclude
    public String getUserId() {
        return userId;
    }

    @Exclude
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Double getPriceDeal() {
        return priceDeal;
    }

    public void setPriceDeal(Double priceDeal) {
        this.priceDeal = priceDeal;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    @Override
    public String toString(){
        return "{"
                +"id = "+id
                +", title = "+title
                +", description = "+description
                +", priceDeal = "+priceDeal
                +", price = "+price
                +", link = "+link
                +", dateBegin ="+dateBegin
                +", dateEnd ="+dateEnd
                +", datePost ="+datePost
                +", numberCommentaires ="+numberCommentaires
                +", order = "+order
                +", address = "+address
                +", votes = "+votes.toString()
                +", favoris = "+favoris.toString()
                +", userId = "+userId
                +", score = "+score
                +", image = "+image
                +"}";
    }
}
