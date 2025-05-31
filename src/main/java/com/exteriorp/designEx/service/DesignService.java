package com.exteriorp.designEx.service;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.exteriorp.designEx.config.AppConfig;
import com.exteriorp.designEx.model.DesignRequest;
import com.exteriorp.designEx.model.DesignResponse;
import com.exteriorp.designEx.model.RegionType;
import com.exteriorp.designEx.model.StyleRecommendation;

/**
 * Service for handling house exterior design operations.
 * 
 * This service is the core component of the design processing pipeline and is responsible for:
 * 1. Validating design requests
 * 2. Coordinating the Python script execution for image segmentation and styling
 * 3. Retrieving style recommendations for each region type
 * 4. Building the response with image URLs and detailed recommendations
 * 
 * The design process combines the SAM-based image segmentation with the style library
 * to create a comprehensive design recommendation for house exteriors.
 */
@Service
public class DesignService {

    /** Logger for this class */
    private static final Logger logger = Logger.getLogger(DesignService.class.getName());
    
    /** Application configuration settings */
    private final AppConfig appConfig;
    
    /** Service for accessing the style library */
    private final StyleLibraryService styleLibraryService;
    
    /** Service for executing the Python segmentation script */
    private final PythonExecutionService pythonExecutionService;
    
    /**
     * Constructs a new DesignService with the required dependencies.
     * 
     * @param appConfig Application configuration settings
     * @param styleLibraryService Service for accessing the style library
     * @param pythonExecutionService Service for executing Python scripts
     */
    public DesignService(AppConfig appConfig, StyleLibraryService styleLibraryService,
                        PythonExecutionService pythonExecutionService) {
        this.appConfig = appConfig;
        this.styleLibraryService = styleLibraryService;
        this.pythonExecutionService = pythonExecutionService;
    }
    
    /**
     * Process a house image and generate design recommendations.
     * 
     * This method orchestrates the entire design process:
     * 1. Validates that the requested style exists
     * 2. Processes the image with the Python script using Meta's SAM model
     * 3. Converts the generated image file paths to client-accessible URLs
     * 4. Retrieves detailed style recommendations for each region type
     * 5. Builds a comprehensive response with images and recommendations
     * 
     * @param imageFile The uploaded house image
     * @param request The design request parameters (style name, blend factor)
     * @return The design response with generated images and recommendations
     * @throws IOException If an error occurs during file processing
     * @throws IllegalArgumentException If the requested style doesn't exist
     */
    public DesignResponse processHouseImage(MultipartFile imageFile, DesignRequest request) throws IOException {
        // Validate that the requested style exists in the style library
        if (!styleLibraryService.styleExists(request.getStyleName())) {
            throw new IllegalArgumentException("Style not found: " + request.getStyleName());
        }
        
        // Process image with Python script that uses SAM for segmentation
        // Returns paths to the three generated images (original, styled, blended)
        List<String> generatedImages = pythonExecutionService.processImage(
                imageFile, request.getStyleName(), request.getBlendAlpha());
        
        // Convert file paths to URLs that can be accessed by the client
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        String originalImageUrl = convertPathToUrl(baseUrl, generatedImages.get(0));
        String styledImageUrl = convertPathToUrl(baseUrl, generatedImages.get(1));
        String blendedImageUrl = convertPathToUrl(baseUrl, generatedImages.get(2));
        
        // Get region-specific recommendations for all available region types
        Map<String, List<StyleRecommendation>> regionRecommendations = new HashMap<>();
        
        // Get a list of all region types (main_walls, windows, doors, etc.)
        List<String> regionTypes = styleLibraryService.getAllRegionTypes()
                .stream()
                .map(RegionType::getType)
                .collect(Collectors.toList());
        
        // For each region type, retrieve style recommendations for the selected style
        for (String regionType : regionTypes) {
            List<StyleRecommendation> recommendations = 
                    styleLibraryService.getRecommendationsForRegionAndStyle(regionType, request.getStyleName());
            // Only include regions that have recommendations
            if (!recommendations.isEmpty()) {
                regionRecommendations.put(regionType, recommendations);
            }
        }
        
        // Build the comprehensive response with all images and recommendations
        DesignResponse response = DesignResponse.builder()
                .originalImageUrl(originalImageUrl)
                .styledImageUrl(styledImageUrl)
                .blendedImageUrl(blendedImageUrl)
                .selectedStyle(request.getStyleName())
                .blendAlpha(request.getBlendAlpha())
                .regionRecommendations(regionRecommendations)
                .build();
        
        return response;
    }
    
    /**
     * Convert a file path to a URL for client access.
     * 
     * This utility method transforms a local file system path into a URL that
     * can be accessed by the client through the DesignController's image endpoint.
     * 
     * @param baseUrl The base URL of the application
     * @param filePath The local file path to convert
     * @return A fully qualified URL for client access
     */
    private String convertPathToUrl(String baseUrl, String filePath) {
        Path path = Paths.get(filePath);
        String filename = path.getFileName().toString();
        return baseUrl + "/api/designs/images/" + filename;
    }
} 