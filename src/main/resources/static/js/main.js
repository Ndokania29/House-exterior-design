/**
 * main.js - Core JavaScript for the AI House Exterior Design application
 * 
 * This file handles all client-side interactions including:
 * - Loading available design styles from the API
 * - Managing form submission and validation
 * - Processing design requests
 * - Displaying results and recommendations
 */

// API endpoint configuration
const API_BASE_URL = ''; // Empty means use relative URLs (same server)
const STYLES_URL = `${API_BASE_URL}/api/styles`; // Endpoint for retrieving available styles
const DESIGN_URL = `${API_BASE_URL}/api/designs`; // Endpoint for processing design requests

// Cache DOM elements for better performance
const uploadForm = document.getElementById('uploadForm');
const styleSelect = document.getElementById('styleSelect');
const blendAlpha = document.getElementById('blendAlpha');
const blendValue = document.getElementById('blendValue');
const processButton = document.getElementById('processButton');
const buttonText = document.getElementById('buttonText');
const loadingSpinner = document.getElementById('loadingSpinner');
const resultImages = document.getElementById('resultImages');
const placeholder = document.getElementById('placeholder');
const recommendations = document.getElementById('recommendations');
const recPlaceholder = document.getElementById('recPlaceholder');
const progressBar = document.getElementById('progressBar');
const progressPercent = document.getElementById('progressPercent');
const loadingContainer = document.getElementById('loadingContainer');

// Animation elements
let progressInterval;
let currentProgress = 0;

/**
 * Initialize the application when DOM is fully loaded
 * - Load available styles from the backend
 * - Set up event listeners
 */
document.addEventListener('DOMContentLoaded', async () => {
    await loadStyles();
    setupEventListeners();
    setupHoverAnimations();
});

/**
 * Load available design styles from the API
 * Populates the style dropdown with options from the backend
 */
async function loadStyles() {
    try {
        // Fetch styles from backend API
        const response = await fetch(STYLES_URL);
        if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}`);
        }
        
        // Parse response JSON
        const styles = await response.json();
        
        // Update the UI with style options
        populateStyleSelect(styles);
    } catch (error) {
        // Handle errors gracefully
        console.error('Error loading styles:', error);
        styleSelect.innerHTML = '<option value="" selected disabled>Error loading styles</option>';
    }
}

/**
 * Populate style dropdown with available styles
 * @param {Array} styles - Array of style objects from the backend
 */
function populateStyleSelect(styles) {
    // Clear existing options
    styleSelect.innerHTML = '';
    
    // Show message if no styles available
    if (styles.length === 0) {
        styleSelect.innerHTML = '<option value="" selected disabled>No styles available</option>';
        return;
    }
    
    // Add default placeholder option
    const defaultOption = document.createElement('option');
    defaultOption.value = '';
    defaultOption.disabled = true;
    defaultOption.selected = true;
    defaultOption.textContent = 'Select a style';
    styleSelect.appendChild(defaultOption);
    
    // Add each style as an option
    styles.forEach(style => {
        const option = document.createElement('option');
        option.value = style.name; // Value is the style name
        option.textContent = style.name; // Display text is also the style name
        styleSelect.appendChild(option);
    });
}

/**
 * Set up event listeners for interactive elements
 */
function setupEventListeners() {
    // Update blend factor value display when slider changes
    blendAlpha.addEventListener('input', function() {
        blendValue.textContent = this.value;
    });
    
    // Handle form submission
    uploadForm.addEventListener('submit', async function(event) {
        event.preventDefault(); // Prevent default form submission
        await processDesign(); // Process the design
    });
}

/**
 * Setup hover animations for UI elements
 */
function setupHoverAnimations() {
    // Add float animation to cards on hover
    document.querySelectorAll('.card').forEach(card => {
        card.addEventListener('mouseenter', function() {
            this.classList.add('float-animation');
        });
        card.addEventListener('mouseleave', function() {
            this.classList.remove('float-animation');
        });
    });
    
    // Add pulse effect to buttons on hover
    document.querySelectorAll('.btn').forEach(btn => {
        btn.addEventListener('mouseenter', function() {
            this.style.transform = 'translateY(-5px)';
            this.style.boxShadow = '0 10px 25px rgba(128, 0, 0, 0.6)';
        });
        btn.addEventListener('mouseleave', function() {
            this.style.transform = '';
            this.style.boxShadow = '';
        });
    });
}

/**
 * Process the design by sending form data to the backend
 * Handles file validation, loading states, and displays results
 */
async function processDesign() {
    // Create FormData object from the form
    const formData = new FormData(uploadForm);
    
    // Validate file input
    const fileInput = document.getElementById('houseImage');
    if (fileInput.files.length === 0) {
        alert('Please select an image file.');
        return;
    }
    
    // Validate style selection
    if (!styleSelect.value) {
        alert('Please select a design style.');
        return;
    }
    
    // Show loading state and start progress animation
    setLoadingState(true);
    startProgressAnimation();
    
    try {
        // Send POST request to backend
        const response = await fetch(DESIGN_URL, {
            method: 'POST',
            body: formData
        });
        
        // Check for errors
        if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}`);
        }
        
        // Complete progress animation
        completeProgressAnimation();
        
        // Parse response JSON
        const result = await response.json();
        
        // Display the results
        displayResults(result);
    } catch (error) {
        // Handle errors gracefully
        console.error('Error processing design:', error);
        alert('Error processing design. Please try again.');
        resetProgressAnimation();
    } finally {
        // Always reset loading state after a delay to show completed progress
        setTimeout(() => {
            setLoadingState(false);
        }, 500);
    }
}

/**
 * Start animated progress bar
 */
function startProgressAnimation() {
    // Reset progress
    currentProgress = 0;
    
    // Show progress elements
    if (progressBar) progressBar.style.display = 'block';
    if (progressPercent) progressPercent.style.display = 'block';
    
    // Initial progress update
    updateProgress(0);
    
    // Create artificial progress simulation
    progressInterval = setInterval(() => {
        // Increment progress, capping at 90% (save the last 10% for completion)
        if (currentProgress < 90) {
            // Slow down as we get closer to 90%
            let increment = 0;
            if (currentProgress < 30) {
                increment = 5 + Math.random() * 5; // Faster at the beginning
            } else if (currentProgress < 60) {
                increment = 3 + Math.random() * 4; // Medium speed
            } else {
                increment = 1 + Math.random() * 2; // Slower at the end
            }
            
            currentProgress = Math.min(currentProgress + increment, 90);
            updateProgress(currentProgress);
        }
    }, 300);
    
    // Add creative loading animation
    if (loadingContainer) {
        loadingContainer.innerHTML = `
            <div class="creative-loader">
                <div class="creative-loader-ring"></div>
            </div>
            <div class="processing-text mt-3">Transforming your home...</div>
        `;
    }
}

/**
 * Complete progress animation to 100%
 */
function completeProgressAnimation() {
    // Clear the interval
    clearInterval(progressInterval);
    
    // Animate to 100%
    currentProgress = 100;
    updateProgress(100);
    
    // Update loading text
    const processingText = document.querySelector('.processing-text');
    if (processingText) {
        processingText.textContent = 'Design completed!';
    }
}

/**
 * Reset progress animation
 */
function resetProgressAnimation() {
    // Clear the interval
    clearInterval(progressInterval);
    
    // Reset progress
    currentProgress = 0;
    updateProgress(0);
    
    // Hide progress elements
    if (progressBar) progressBar.parentElement.style.display = 'none';
}

/**
 * Update progress bar and percentage display
 * @param {number} percent - Progress percentage (0-100)
 */
function updateProgress(percent) {
    if (progressBar) {
        progressBar.style.width = `${percent}%`;
        progressBar.setAttribute('aria-valuenow', percent);
    }
    
    if (progressPercent) {
        progressPercent.textContent = `${Math.round(percent)}%`;
    }
}

/**
 * Set loading state for UI elements
 * @param {boolean} isLoading - Whether the application is in loading state
 */
function setLoadingState(isLoading) {
    // Get the processing container element
    const processingContainer = document.getElementById('processingContainer');
    
    if (isLoading) {
        // Show loading state
        buttonText.textContent = 'Processing...';
        loadingSpinner.classList.remove('d-none');
        processButton.disabled = true;
        
        // Show the processing container with progress bar
        if (processingContainer) {
            processingContainer.style.display = 'block';
        }
    } else {
        // Reset to normal state
        buttonText.textContent = 'Process Design';
        loadingSpinner.classList.add('d-none');
        processButton.disabled = false;
        
        // Reset loading container
        if (loadingContainer) {
            loadingContainer.innerHTML = '';
        }
        
        // Hide the processing container
        if (processingContainer) {
            processingContainer.style.display = 'none';
        }
        
        // Reset progress bar
        if (progressBar) {
            progressBar.style.width = '0%';
            progressBar.setAttribute('aria-valuenow', 0);
        }
        
        // Reset percentage text
        if (progressPercent) {
            progressPercent.textContent = '0%';
        }
    }
}

/**
 * Display design results in the UI
 * @param {Object} result - The response object from the backend
 */
function displayResults(result) {
    // Clear placeholders
    placeholder.style.display = 'none';
    recPlaceholder.style.display = 'none';
    
    // Display the three result images with labels
    resultImages.innerHTML = `
        <div class="col-md-4">
            <div class="image-container">
                <img src="${result.originalImageUrl}" alt="Original House" class="img-fluid">
                <div class="image-label">Original</div>
            </div>
        </div>
        <div class="col-md-4">
            <div class="image-container">
                <img src="${result.styledImageUrl}" alt="Styled House" class="img-fluid">
                <div class="image-label">Styled</div>
            </div>
        </div>
        <div class="col-md-4">
            <div class="image-container">
                <img src="${result.blendedImageUrl}" alt="Blended Result" class="img-fluid">
                <div class="image-label">Blended (${result.blendAlpha})</div>
            </div>
        </div>
    `;
    
    // Add animation to result images
    setTimeout(() => {
        document.querySelectorAll('#resultImages img').forEach((img, index) => {
            // Stagger the animation
            setTimeout(() => {
                img.style.opacity = '0';
                img.style.transform = 'scale(0.8)';
                img.style.transition = 'all 0.5s ease-out';
                
                // Then fade in with animation
                setTimeout(() => {
                    img.style.opacity = '1';
                    img.style.transform = 'scale(1)';
                }, 100);
            }, index * 200);
        });
    }, 100);
    
    // Display design recommendations
    displayRecommendations(result.regionRecommendations);
}

/**
 * Display design recommendations in the UI
 * @param {Object} recommendations - Object containing recommendations by region
 */
function displayRecommendations(recommendations) {
    // Get container element
    const recommendationsContainer = document.getElementById('recommendations');
    recommendationsContainer.innerHTML = '';
    
    // Check if recommendations exist
    if (!recommendations || Object.keys(recommendations).length === 0) {
        recommendationsContainer.innerHTML = '<p class="text-center">No recommendations available.</p>';
        return;
    }
    
    // Process each region type
    for (const [regionType, recs] of Object.entries(recommendations)) {
        // Create section heading for the region
        const regionTitle = document.createElement('h4');
        regionTitle.textContent = formatRegionType(regionType);
        regionTitle.classList.add('mt-4', 'mb-3');
        recommendationsContainer.appendChild(regionTitle);
        
        // Display up to 3 recommendations per region (to keep it manageable)
        recs.slice(0, 3).forEach((rec, index) => {
            // Create recommendation card
            const recCard = document.createElement('div');
            recCard.classList.add('recommendation-card');
            
            // Add staggered fade-in animation
            recCard.style.opacity = '0';
            recCard.style.transform = 'translateY(20px)';
            recCard.style.transition = 'all 0.4s ease-out';
            
            // Create color swatch element
            const colorHtml = `<span class="color-swatch" style="background-color: ${rec.color}"></span>`;
            
            // Create keyword badges
            let keywordsHtml = '';
            if (rec.keywords && rec.keywords.length > 0) {
                keywordsHtml = '<div class="mt-2">' + 
                    rec.keywords.map(kw => `<span class="keyword-badge">${kw}</span>`).join(' ') +
                    '</div>';
            }
            
            // Populate recommendation card content
            recCard.innerHTML = `
                <div class="d-flex justify-content-between align-items-start">
                    <div>
                        ${colorHtml}
                        <strong>${rec.name || 'Recommendation'}</strong>
                        <p class="mb-0 mt-2">${rec.description || 'No description available.'}</p>
                        ${keywordsHtml}
                    </div>
                </div>
            `;
            
            // Add to container
            recommendationsContainer.appendChild(recCard);
            
            // Animate in with staggered delay
            setTimeout(() => {
                recCard.style.opacity = '1';
                recCard.style.transform = 'translateY(0)';
            }, 100 + (index * 150));
        });
    }
}

/**
 * Format region type for display
 * @param {string} regionType - The raw region type from the backend
 * @returns {string} - Formatted region type for display
 */
function formatRegionType(regionType) {
    // Convert camelCase or snake_case to Title Case with spaces
    return regionType
        .replace(/([A-Z])/g, ' $1') // Insert space before capital letters
        .replace(/_/g, ' ') // Replace underscores with spaces
        .replace(/^\w/, c => c.toUpperCase()) // Capitalize first letter
        .trim(); // Remove extra spaces
} 