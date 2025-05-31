package com.exteriorp.designEx.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.exteriorp.designEx.model.DesignStyle;
import com.exteriorp.designEx.model.RegionType;
import com.exteriorp.designEx.model.StyleRecommendation;
import com.exteriorp.designEx.service.StyleLibraryService;

/**
 * REST controller for style-related endpoints.
 */
@RestController
@RequestMapping("/api/styles")
public class StyleController {

    private final StyleLibraryService styleLibraryService;
    
    public StyleController(StyleLibraryService styleLibraryService) {
        this.styleLibraryService = styleLibraryService;
    }
    
    /**
     * Get all available design styles.
     */
    @GetMapping
    public ResponseEntity<List<DesignStyle>> getAllStyles() {
        return ResponseEntity.ok(styleLibraryService.getAllStyles());
    }
    
    /**
     * Get all available region types.
     */
    @GetMapping("/regions")
    public ResponseEntity<List<RegionType>> getAllRegionTypes() {
        return ResponseEntity.ok(styleLibraryService.getAllRegionTypes());
    }
    
    /**
     * Get recommendations for a specific region and style.
     */
    @GetMapping("/{styleName}/regions/{regionType}")
    public ResponseEntity<List<StyleRecommendation>> getRecommendationsForRegionAndStyle(
            @PathVariable String styleName, 
            @PathVariable String regionType) {
        
        List<StyleRecommendation> recommendations = 
                styleLibraryService.getRecommendationsForRegionAndStyle(regionType, styleName);
        
        if (recommendations.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(recommendations);
    }
} 