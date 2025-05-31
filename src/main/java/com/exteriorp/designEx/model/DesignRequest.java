package com.exteriorp.designEx.model;

/**
 * Represents a design request from a client.
 */
public class DesignRequest {
    private String styleName;
    private Float blendAlpha; // Optional blending factor between 0.0 and 1.0
    
    public DesignRequest() {
    }
    
    public DesignRequest(String styleName, Float blendAlpha) {
        this.styleName = styleName;
        this.blendAlpha = blendAlpha;
    }
    
    public String getStyleName() {
        return styleName;
    }
    
    public void setStyleName(String styleName) {
        this.styleName = styleName;
    }
    
    public Float getBlendAlpha() {
        return blendAlpha;
    }
    
    public void setBlendAlpha(Float blendAlpha) {
        this.blendAlpha = blendAlpha;
    }
} 