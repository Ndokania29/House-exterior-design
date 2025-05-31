package com.exteriorp.designEx.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.exteriorp.designEx.config.AppConfig;
import com.exteriorp.designEx.model.DesignRequest;
import com.exteriorp.designEx.model.DesignResponse;
import com.exteriorp.designEx.service.DesignService;

/**
 * REST controller for design-related endpoints.
 * 
 * This controller handles all HTTP requests related to house exterior design processing,
 * including image uploads, design processing, and retrieving result images.
 * 
 * The main workflow is:
 * 1. Client uploads a house image with a selected style
 * 2. Controller passes the request to the DesignService for processing
 * 3. Python script segments and styles the image
 * 4. Styled and blended images are saved to disk
 * 5. Response with image URLs and recommendations is returned
 * 6. Client can then retrieve the result images using the image endpoint
 */
@RestController
@RequestMapping("/api/designs")
public class DesignController {

    /** Logger for this class */
    private static final Logger logger = Logger.getLogger(DesignController.class.getName());
    
    /** Service that handles all design processing logic */
    private final DesignService designService;
    
    /** Application configuration for paths and settings */
    private final AppConfig appConfig;
    
    /**
     * Constructs a new DesignController with the specified services.
     * 
     * @param designService Service for processing design requests
     * @param appConfig Application configuration settings
     */
    public DesignController(DesignService designService, AppConfig appConfig) {
        this.designService = designService;
        this.appConfig = appConfig;
    }
    
    /**
     * Process a house image and generate design recommendations.
     * 
     * This endpoint accepts a multipart form submission with:
     * - An image file of a house exterior
     * - A design style name to apply
     * - An optional blend factor (0.0-1.0) to control style application strength
     * 
     * The processing involves:
     * 1. Saving the uploaded image
     * 2. Running the SAM-based Python script for segmentation
     * 3. Applying the selected style to each segment
     * 4. Generating design recommendations
     * 5. Creating both fully styled and blended result images
     * 
     * @param imageFile The uploaded house image file
     * @param styleName The name of the design style to apply
     * @param blendAlpha The blend factor (0.0-1.0, default 0.5) 
     * @return A response containing URLs for result images and recommendations
     */
    @PostMapping
    public ResponseEntity<DesignResponse> processHouseImage(
            @RequestParam("image") MultipartFile imageFile,
            @RequestParam("style") String styleName,
            @RequestParam(value = "blendAlpha", required = false) Float blendAlpha) {
        
        try {
            // Create a design request object with the parameters
            DesignRequest request = new DesignRequest(styleName, blendAlpha);
            
            // Process the image and get the response
            DesignResponse response = designService.processHouseImage(imageFile, request);
            
            // Return the successful response
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            // Handle invalid parameters (bad style name, invalid blend factor, etc.)
            logger.severe("Invalid request parameters: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (IOException e) {
            // Handle file processing errors (can't save file, Python script fails, etc.)
            logger.severe("Error processing image: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Get an image from the output directory.
     * 
     * This endpoint serves result images (original, styled, blended) from the 
     * application's output directory. The image filename is specified in the URL path.
     * 
     * Security note: This endpoint should have proper validation in a production 
     * environment to prevent directory traversal attacks.
     * 
     * @param filename The name of the image file to retrieve
     * @return The requested image file as a ByteArrayResource
     */
    @GetMapping("/images/{filename:.+}")
    public ResponseEntity<ByteArrayResource> getImage(@PathVariable String filename) {
        try {
            // Construct the full path to the requested image
            Path imagePath = Paths.get(appConfig.getOutput().getDirectory(), filename);
            File imageFile = imagePath.toFile();
            
            // Check if the file exists
            if (!imageFile.exists()) {
                return ResponseEntity.notFound().build();
            }
            
            // Read the image data into a byte array
            byte[] imageData = Files.readAllBytes(imagePath);
            ByteArrayResource resource = new ByteArrayResource(imageData);
            
            // Determine the correct content type based on file extension
            String contentType = determineContentType(filename);
            
            // Return the image with appropriate headers
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .body(resource);
        } catch (IOException e) {
            // Handle file reading errors
            logger.severe("Error reading image file: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Determine content type based on file extension.
     * 
     * This helper method returns the appropriate MIME type for common image formats.
     * 
     * @param filename The filename to analyze
     * @return The corresponding MIME type string
     */
    private String determineContentType(String filename) {
        if (filename.toLowerCase().endsWith(".png")) {
            return "image/png";
        } else if (filename.toLowerCase().endsWith(".jpg") || filename.toLowerCase().endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (filename.toLowerCase().endsWith(".gif")) {
            return "image/gif";
        } else {
            return "application/octet-stream"; // Default binary type
        }
    }
} 