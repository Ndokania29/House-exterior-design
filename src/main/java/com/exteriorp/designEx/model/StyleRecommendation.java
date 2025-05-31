package com.exteriorp.designEx.model;

import java.util.List;

/**
 * Represents a style recommendation for a specific region.
 */
public class StyleRecommendation {
    private String color;
    private String texture;
    private String material;
    private String finish;
    private double rating;
    private List<String> keywords;
    
    public StyleRecommendation() {
    }
    
    public StyleRecommendation(String color, String texture, String material, String finish,
                              double rating, List<String> keywords) {
        this.color = color;
        this.texture = texture;
        this.material = material;
        this.finish = finish;
        this.rating = rating;
        this.keywords = keywords;
    }
    
    public String getColor() {
        return color;
    }
    
    public void setColor(String color) {
        this.color = color;
    }
    
    public String getTexture() {
        return texture;
    }
    
    public void setTexture(String texture) {
        this.texture = texture;
    }
    
    public String getMaterial() {
        return material;
    }
    
    public void setMaterial(String material) {
        this.material = material;
    }
    
    public String getFinish() {
        return finish;
    }
    
    public void setFinish(String finish) {
        this.finish = finish;
    }
    
    public double getRating() {
        return rating;
    }
    
    public void setRating(double rating) {
        this.rating = rating;
    }
    
    public List<String> getKeywords() {
        return keywords;
    }
    
    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }
} 