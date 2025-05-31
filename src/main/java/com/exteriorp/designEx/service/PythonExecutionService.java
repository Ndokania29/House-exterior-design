package com.exteriorp.designEx.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.exteriorp.designEx.config.AppConfig;

/**
 * Service for handling Python script execution.
 * 
 * This service is responsible for:
 * 1. Saving uploaded images to disk
 * 2. Executing the Python segmentation script (seg.py)
 * 3. Passing the appropriate arguments to the Python script
 * 4. Monitoring the execution and handling errors
 * 5. Returning the paths to the generated images
 * 
 * The service acts as a bridge between the Java application and the Python
 * script that uses Meta's SAM model for image segmentation and styling.
 */
@Service
public class PythonExecutionService {

    /** Logger for this class */
    private static final Logger logger = Logger.getLogger(PythonExecutionService.class.getName());
    
    /** Application configuration settings */
    private final AppConfig appConfig;
    
    /**
     * Constructs a new PythonExecutionService with the specified configuration.
     * 
     * @param appConfig Application configuration containing paths for images, Python script, etc.
     */
    public PythonExecutionService(AppConfig appConfig) {
        this.appConfig = appConfig;
    }
    
    /**
     * Processes the image using the Python script.
     * 
     * This method:
     * 1. Creates a unique filename for the uploaded image
     * 2. Saves the image to disk
     * 3. Builds the command to execute the Python script
     * 4. Runs the script with appropriate arguments
     * 5. Captures and logs the script's output
     * 6. Returns the paths to the original, styled, and blended images
     * 
     * @param imageFile The uploaded house image
     * @param styleName The selected style name to apply
     * @param blendAlpha The blending factor (0.0-1.0, default 0.5)
     * @return A list of file paths to the generated images (original, styled, blended)
     * @throws IOException If an error occurs during processing or script execution
     */
    public List<String> processImage(MultipartFile imageFile, String styleName, Float blendAlpha) throws IOException {
        // Create output directory if it doesn't exist
        File outputDir = new File(appConfig.getOutput().getDirectory());
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
        
        // Generate unique filenames using timestamp and random ID to prevent collisions
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        String originalFilename = timestamp + "_" + uniqueId + "_original.png";
        String styledFilename = timestamp + "_" + uniqueId + "_styled.png";
        String blendedFilename = timestamp + "_" + uniqueId + "_blended.png";
        
        // Save the uploaded image to disk
        Path originalImagePath = Paths.get(outputDir.getAbsolutePath(), originalFilename);
        Files.copy(imageFile.getInputStream(), originalImagePath);
        
        // Build command to execute Python script with all required arguments
        List<String> command = new ArrayList<>();
        command.add("python"); // Python interpreter
        command.add(appConfig.getPython().getScriptPath()); // Path to seg.py
        
        // Input and output file paths
        command.add("--input");
        command.add(originalImagePath.toString());
        command.add("--output_styled");
        command.add(Paths.get(outputDir.getAbsolutePath(), styledFilename).toString());
        command.add("--output_blended");
        command.add(Paths.get(outputDir.getAbsolutePath(), blendedFilename).toString());
        
        // Style name and model paths
        command.add("--style");
        command.add(styleName);
        command.add("--model");
        command.add(appConfig.getSamModel().getPath());
        command.add("--style_library");
        command.add(appConfig.getStyleLibrary().getPath());
        
        // Optional blend alpha parameter
        if (blendAlpha != null) {
            command.add("--blend_alpha");
            command.add(String.valueOf(blendAlpha));
        }
        
        // Create process builder and redirect error stream to standard output
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true); // Combines stdout and stderr
        
        try {
            // Start the process
            Process process = processBuilder.start();
            
            // Read and log the output from the Python script
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    logger.info("Python output: " + line);
                }
            }
            
            // Wait for the process to complete with a timeout
            boolean completed = process.waitFor(5, TimeUnit.MINUTES);
            if (!completed) {
                // Kill the process if it takes too long
                process.destroyForcibly();
                throw new IOException("Python script execution timed out after 5 minutes");
            }
            
            // Check exit code to detect script errors
            int exitCode = process.exitValue();
            if (exitCode != 0) {
                throw new IOException("Python script execution failed with exit code: " + exitCode);
            }
            
            // Return file paths to the generated images
            List<String> generatedImages = new ArrayList<>();
            generatedImages.add(originalImagePath.toString());
            generatedImages.add(Paths.get(outputDir.getAbsolutePath(), styledFilename).toString());
            generatedImages.add(Paths.get(outputDir.getAbsolutePath(), blendedFilename).toString());
            
            return generatedImages;
        } catch (InterruptedException e) {
            // Handle thread interruption
            Thread.currentThread().interrupt();
            throw new IOException("Python script execution was interrupted", e);
        }
    }
} 