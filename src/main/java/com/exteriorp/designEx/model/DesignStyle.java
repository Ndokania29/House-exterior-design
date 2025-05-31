package com.exteriorp.designEx.model;

/**
 * Represents a design style available in the system.
 */
public class DesignStyle {
    private String name;
    private String description;
    
    public DesignStyle() {
    }
    
    public DesignStyle(String name, String description) {
        this.name = name;
        this.description = description;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
} 