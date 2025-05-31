package com.exteriorp.designEx.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for the application.
 */
@Configuration
@ConfigurationProperties(prefix = "app")
public class AppConfig {
    
    private Python python = new Python();
    private StyleLibrary styleLibrary = new StyleLibrary();
    private SamModel samModel = new SamModel();
    private Output output = new Output();
    
    public Python getPython() {
        return python;
    }
    
    public void setPython(Python python) {
        this.python = python;
    }
    
    public StyleLibrary getStyleLibrary() {
        return styleLibrary;
    }
    
    public void setStyleLibrary(StyleLibrary styleLibrary) {
        this.styleLibrary = styleLibrary;
    }
    
    public SamModel getSamModel() {
        return samModel;
    }
    
    public void setSamModel(SamModel samModel) {
        this.samModel = samModel;
    }
    
    public Output getOutput() {
        return output;
    }
    
    public void setOutput(Output output) {
        this.output = output;
    }
    
    public static class Python {
        private String scriptPath;
        
        public String getScriptPath() {
            return scriptPath;
        }
        
        public void setScriptPath(String scriptPath) {
            this.scriptPath = scriptPath;
        }
    }
    
    public static class StyleLibrary {
        private String path;
        
        public String getPath() {
            return path;
        }
        
        public void setPath(String path) {
            this.path = path;
        }
    }
    
    public static class SamModel {
        private String path;
        
        public String getPath() {
            return path;
        }
        
        public void setPath(String path) {
            this.path = path;
        }
    }
    
    public static class Output {
        private String directory;
        
        public String getDirectory() {
            return directory;
        }
        
        public void setDirectory(String directory) {
            this.directory = directory;
        }
    }
} 