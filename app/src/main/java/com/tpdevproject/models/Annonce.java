package com.tpdevproject.models;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by root on 09/12/17.
 */

public class Annonce {
    private String id;
    private String title;
    private String description;
    private String image;
    private int score;
    private Map<String, String> coms = new HashMap<>();

    public Annonce() {}  // Needed for Firebase

    public Annonce(String id, String title, String description, String image, HashMap<String, String> coms) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.image = image;
        this.coms = coms;
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

    public void setComs(HashMap<String, String> coms) {
        this.coms = coms;
    }

    public Map<String, String> getComs(){
        return coms;
    }

    public int getNumberComs(){
        return coms.size();
    }
}
