package com.exteriorp.designEx.model;

/**
 * Represents a region type in a house exterior design.
 * 
 * This class defines different architectural elements of a house that can be
 * separately styled, such as main walls, side walls, windows, doors, roof, etc.
 * Each region type has a unique identifier (type) and a human-readable description.
 * 
 * The RegionType is used throughout the application to:
 * - Classify segments of a house image
 * - Manage style libraries specific to each region
 * - Generate appropriate design recommendations
 * 
 * Common region types include:
 * - main_walls
 * - side_walls
 * - upper_lower_walls
 * - windows
 * - doors
 * - roof
 * - balcony
 * - pillars
 */
public class RegionType {
    /**
     * The unique identifier for this region type.
     * Typically a snake_case string like "main_walls" or "windows".
     * Used as a key in the style library and for region classification.
     */
    private String type;
    
    /**
     * A human-readable description of this region type.
     * Provides more details about what this region represents.
     * Used primarily for display purposes in the UI.
     */
    private String description;
    
    /**
     * Default constructor required for JSON serialization/deserialization.
     */
    public RegionType() {
    }
    
    /**
     * Constructs a new RegionType with the specified type and description.
     * 
     * @param type The unique identifier for this region type
     * @param description A human-readable description of this region type
     */
    public RegionType(String type, String description) {
        this.type = type;
        this.description = description;
    }
    
    /**
     * Gets the unique identifier for this region type.
     * 
     * @return The region type identifier (e.g., "main_walls", "windows")
     */
    public String getType() {
        return type;
    }
    
    /**
     * Sets the unique identifier for this region type.
     * 
     * @param type The region type identifier
     */
    public void setType(String type) {
        this.type = type;
    }
    
    /**
     * Gets the human-readable description of this region type.
     * 
     * @return The description of this region type
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Sets the human-readable description of this region type.
     * 
     * @param description The description of this region type
     */
    public void setDescription(String description) {
        this.description = description;
    }
} 