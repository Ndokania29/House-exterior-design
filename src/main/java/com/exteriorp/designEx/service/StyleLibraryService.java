package com.exteriorp.designEx.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import com.exteriorp.designEx.config.AppConfig;
import com.exteriorp.designEx.model.DesignStyle;
import com.exteriorp.designEx.model.RegionType;
import com.exteriorp.designEx.model.StyleRecommendation;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;

/**
 * Service for handling style library operations.
 */
@Service
public class StyleLibraryService {

    private static final Logger logger = Logger.getLogger(StyleLibraryService.class.getName());
    
    private final AppConfig appConfig;
    private final ObjectMapper objectMapper;
    
    // Cache of the style library
    private Map<String, Map<String, List<StyleRecommendation>>> styleLibrary;
    
    public StyleLibraryService(AppConfig appConfig, ObjectMapper objectMapper) {
        this.appConfig = appConfig;
        this.objectMapper = objectMapper;
        this.styleLibrary = new HashMap<>();
    }
    
    @PostConstruct
    public void init() {
        loadStyleLibrary();
    }
    
    /**
     * Loads the style library from the JSON file.
     */
    private void loadStyleLibrary() {
        try {
            File libraryFile = new File(appConfig.getStyleLibrary().getPath());
            styleLibrary = objectMapper.readValue(libraryFile, 
                    new TypeReference<Map<String, Map<String, List<StyleRecommendation>>>>() {});
            logger.info("Style library loaded successfully with " + styleLibrary.size() + " region types");
        } catch (IOException e) {
            logger.severe("Failed to load style library: " + e.getMessage());
            styleLibrary = new HashMap<>();
        }
    }
    
    /**
     * Get all available design styles.
     */
    public List<DesignStyle> getAllStyles() {
        Set<String> styleNames = styleLibrary.values().stream()
                .flatMap(regionMap -> regionMap.keySet().stream())
                .distinct()
                .collect(java.util.stream.Collectors.toSet());
        
        List<DesignStyle> styles = new ArrayList<>();
        for (String styleName : styleNames) {
            DesignStyle style = new DesignStyle();
            style.setName(styleName);
            style.setDescription("Design style: " + styleName);
            styles.add(style);
        }
        
        return styles;
    }
    
    /**
     * Get all available region types.
     */
    public List<RegionType> getAllRegionTypes() {
        List<RegionType> regions = new ArrayList<>();
        for (String regionType : styleLibrary.keySet()) {
            RegionType region = new RegionType();
            region.setType(regionType);
            region.setDescription("Region type: " + regionType);
            regions.add(region);
        }
        
        return regions;
    }
    
    /**
     * Get style recommendations for a specific region and style.
     */
    public List<StyleRecommendation> getRecommendationsForRegionAndStyle(String regionType, String styleName) {
        Map<String, List<StyleRecommendation>> regionMap = styleLibrary.get(regionType);
        if (regionMap == null) {
            return new ArrayList<>();
        }
        
        List<StyleRecommendation> recommendations = regionMap.get(styleName);
        return recommendations != null ? recommendations : new ArrayList<>();
    }
    
    /**
     * Check if a style exists in the library.
     */
    public boolean styleExists(String styleName) {
        return styleLibrary.values().stream()
                .anyMatch(regionMap -> regionMap.containsKey(styleName));
    }
} 