package com.exteriorp.designEx.model;

import java.util.List;
import java.util.Map;

/**
 * Represents a design response to a client request.
 */
public class DesignResponse {
    private String originalImageUrl;
    private String styledImageUrl;
    private String blendedImageUrl;
    private String selectedStyle;
    private Float blendAlpha;
    private Map<String, List<StyleRecommendation>> regionRecommendations;
    
    // Default constructor
    public DesignResponse() {
    }
    
    // Constructor with all fields
    public DesignResponse(String originalImageUrl, String styledImageUrl, String blendedImageUrl,
                         String selectedStyle, Float blendAlpha,
                         Map<String, List<StyleRecommendation>> regionRecommendations) {
        this.originalImageUrl = originalImageUrl;
        this.styledImageUrl = styledImageUrl;
        this.blendedImageUrl = blendedImageUrl;
        this.selectedStyle = selectedStyle;
        this.blendAlpha = blendAlpha;
        this.regionRecommendations = regionRecommendations;
    }
    
    // Getters and setters
    public String getOriginalImageUrl() {
        return originalImageUrl;
    }
    
    public void setOriginalImageUrl(String originalImageUrl) {
        this.originalImageUrl = originalImageUrl;
    }
    
    public String getStyledImageUrl() {
        return styledImageUrl;
    }
    
    public void setStyledImageUrl(String styledImageUrl) {
        this.styledImageUrl = styledImageUrl;
    }
    
    public String getBlendedImageUrl() {
        return blendedImageUrl;
    }
    
    public void setBlendedImageUrl(String blendedImageUrl) {
        this.blendedImageUrl = blendedImageUrl;
    }
    
    public String getSelectedStyle() {
        return selectedStyle;
    }
    
    public void setSelectedStyle(String selectedStyle) {
        this.selectedStyle = selectedStyle;
    }
    
    public Float getBlendAlpha() {
        return blendAlpha;
    }
    
    public void setBlendAlpha(Float blendAlpha) {
        this.blendAlpha = blendAlpha;
    }
    
    public Map<String, List<StyleRecommendation>> getRegionRecommendations() {
        return regionRecommendations;
    }
    
    public void setRegionRecommendations(Map<String, List<StyleRecommendation>> regionRecommendations) {
        this.regionRecommendations = regionRecommendations;
    }
    
    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private String originalImageUrl;
        private String styledImageUrl;
        private String blendedImageUrl;
        private String selectedStyle;
        private Float blendAlpha;
        private Map<String, List<StyleRecommendation>> regionRecommendations;
        
        public Builder originalImageUrl(String originalImageUrl) {
            this.originalImageUrl = originalImageUrl;
            return this;
        }
        
        public Builder styledImageUrl(String styledImageUrl) {
            this.styledImageUrl = styledImageUrl;
            return this;
        }
        
        public Builder blendedImageUrl(String blendedImageUrl) {
            this.blendedImageUrl = blendedImageUrl;
            return this;
        }
        
        public Builder selectedStyle(String selectedStyle) {
            this.selectedStyle = selectedStyle;
            return this;
        }
        
        public Builder blendAlpha(Float blendAlpha) {
            this.blendAlpha = blendAlpha;
            return this;
        }
        
        public Builder regionRecommendations(Map<String, List<StyleRecommendation>> regionRecommendations) {
            this.regionRecommendations = regionRecommendations;
            return this;
        }
        
        public DesignResponse build() {
            return new DesignResponse(originalImageUrl, styledImageUrl, blendedImageUrl, 
                                     selectedStyle, blendAlpha, regionRecommendations);
        }
    }
} 