/**
 * theme.js - Handle theme customization for the House Exterior Design application
 * 
 * This file manages the theme picker functionality:
 * - Toggle theme panel open/close
 * - Change primary and accent colors
 * - Apply theme presets
 * - Save preferences to localStorage
 */

document.addEventListener('DOMContentLoaded', () => {
    // Default theme colors
    const defaultTheme = {
        primary: '#7D6E96',
        primaryDark: '#5D4C7C',
        primaryLight: '#9C8CB3',
        accent: '#D5C9E0'
    };

    // Theme presets
    const themePresets = {
        'lavender': {
            primary: '#7D6E96',
            primaryDark: '#5D4C7C',
            primaryLight: '#9C8CB3',
            accent: '#D5C9E0'
        },
        'modern-neutral': {
            primary: '#5A5A5A',
            primaryDark: '#404040',
            primaryLight: '#757575',
            accent: '#94A3B8'
        },
        'red-black': {
            primary: '#800000',
            primaryDark: '#1a0000',
            primaryLight: '#b30000',
            accent: '#ff3333'
        },
        'purple-black': {
            primary: '#580080',
            primaryDark: '#1a0030',
            primaryLight: '#8000b3',
            accent: '#b366ff'
        },
        'blue-black': {
            primary: '#000080',
            primaryDark: '#00001a',
            primaryLight: '#0000b3',
            accent: '#3399ff'
        }
    };

    // Cache DOM elements
    const themePicker = document.querySelector('.theme-picker');
    const themeToggle = document.getElementById('themeToggle');
    const resetThemeBtn = document.getElementById('resetTheme');
    const colorOptions = document.querySelectorAll('.color-option');
    const presetOptions = document.querySelectorAll('.preset-option');
    
    // Toggle theme picker panel
    themeToggle.addEventListener('click', () => {
        themePicker.classList.toggle('open');
    });

    // Load saved theme from localStorage (if any)
    loadSavedTheme();

    // Set up event listeners for color options
    colorOptions.forEach(option => {
        option.addEventListener('click', () => {
            // Remove active class from other options in the same group
            const colorType = option.getAttribute('data-color');
            document.querySelectorAll(`.color-option[data-color="${colorType}"]`).forEach(opt => {
                opt.classList.remove('active');
            });
            
            // Add active class to selected option
            option.classList.add('active');
            
            // Get the color value
            const colorValue = option.getAttribute('data-value');
            
            // Update the theme
            updateThemeColor(colorType, colorValue);
        });
    });

    // Set up event listeners for preset options
    presetOptions.forEach(preset => {
        preset.addEventListener('click', () => {
            // Remove active class from all presets
            presetOptions.forEach(p => p.classList.remove('active'));
            
            // Add active class to selected preset
            preset.classList.add('active');
            
            // Get the preset theme
            const presetName = preset.getAttribute('data-theme');
            
            // Apply the preset theme
            applyThemePreset(presetName);
            
            // Update active color options to match preset
            updateColorOptionSelections(presetName);
        });
    });

    // Reset theme to default
    resetThemeBtn.addEventListener('click', () => {
        applyThemePreset('lavender');
        updateColorOptionSelections('lavender');
        
        // Update preset selection
        presetOptions.forEach(p => p.classList.remove('active'));
        document.querySelector('.preset-lavender').classList.add('active');
        
        // Save to localStorage
        saveTheme(themePresets['lavender']);
    });

    /**
     * Update a specific theme color
     * @param {string} colorType - The type of color to update (primary, accent)
     * @param {string} colorValue - The hex color value
     */
    function updateThemeColor(colorType, colorValue) {
        // Get the root element to update CSS variables
        const root = document.documentElement;
        
        if (colorType === 'primary') {
            // Calculate darker and lighter variations of the primary color
            const primaryDark = darkenColor(colorValue, 30);
            const primaryLight = lightenColor(colorValue, 20);
            
            // Update CSS variables
            root.style.setProperty('--primary', colorValue);
            root.style.setProperty('--primary-dark', primaryDark);
            root.style.setProperty('--primary-light', primaryLight);
            
            // Update gradients that use these colors
            root.style.setProperty('--gradient-primary', `linear-gradient(135deg, ${primaryDark}, ${colorValue}, ${primaryLight})`);
            
            // Save theme
            saveTheme({
                primary: colorValue,
                primaryDark: primaryDark,
                primaryLight: primaryLight,
                accent: getComputedStyle(root).getPropertyValue('--accent').trim()
            });
        } else if (colorType === 'accent') {
            // Update accent color
            root.style.setProperty('--accent', colorValue);
            
            // Update accent gradient
            const primary = getComputedStyle(root).getPropertyValue('--primary').trim();
            root.style.setProperty('--gradient-accent', `linear-gradient(135deg, ${primary}, ${colorValue}, ${lightenColor(colorValue, 10)})`);
            
            // Save theme
            saveTheme({
                primary: getComputedStyle(root).getPropertyValue('--primary').trim(),
                primaryDark: getComputedStyle(root).getPropertyValue('--primary-dark').trim(),
                primaryLight: getComputedStyle(root).getPropertyValue('--primary-light').trim(),
                accent: colorValue
            });
        }
    }

    /**
     * Apply a theme preset
     * @param {string} presetName - The name of the preset to apply
     */
    function applyThemePreset(presetName) {
        const preset = themePresets[presetName];
        if (!preset) return;
        
        const root = document.documentElement;
        
        // Update all theme colors
        root.style.setProperty('--primary', preset.primary);
        root.style.setProperty('--primary-dark', preset.primaryDark);
        root.style.setProperty('--primary-light', preset.primaryLight);
        root.style.setProperty('--accent', preset.accent);
        
        // Update gradients
        root.style.setProperty('--gradient-primary', `linear-gradient(135deg, ${preset.primaryDark}, ${preset.primary}, ${preset.primaryLight})`);
        root.style.setProperty('--gradient-accent', `linear-gradient(135deg, ${preset.primary}, ${preset.accent}, ${lightenColor(preset.accent, 10)})`);
        
        // Save theme
        saveTheme(preset);
    }

    /**
     * Update color option selections based on preset
     * @param {string} presetName - The name of the preset
     */
    function updateColorOptionSelections(presetName) {
        const preset = themePresets[presetName];
        if (!preset) return;
        
        // Update primary color selection
        document.querySelectorAll('.color-option[data-color="primary"]').forEach(opt => {
            if (opt.getAttribute('data-value') === preset.primary) {
                opt.classList.add('active');
            } else {
                opt.classList.remove('active');
            }
        });
        
        // Update accent color selection
        document.querySelectorAll('.color-option[data-color="accent"]').forEach(opt => {
            if (opt.getAttribute('data-value') === preset.accent) {
                opt.classList.add('active');
            } else {
                opt.classList.remove('active');
            }
        });
    }

    /**
     * Save theme to localStorage
     * @param {Object} theme - The theme object to save
     */
    function saveTheme(theme) {
        localStorage.setItem('exteriorDesignTheme', JSON.stringify(theme));
    }

    /**
     * Load saved theme from localStorage
     */
    function loadSavedTheme() {
        const savedTheme = localStorage.getItem('exteriorDesignTheme');
        if (savedTheme) {
            try {
                const theme = JSON.parse(savedTheme);
                
                // Apply saved theme
                const root = document.documentElement;
                root.style.setProperty('--primary', theme.primary);
                root.style.setProperty('--primary-dark', theme.primaryDark);
                root.style.setProperty('--primary-light', theme.primaryLight);
                root.style.setProperty('--accent', theme.accent);
                
                // Update gradients
                root.style.setProperty('--gradient-primary', `linear-gradient(135deg, ${theme.primaryDark}, ${theme.primary}, ${theme.primaryLight})`);
                root.style.setProperty('--gradient-accent', `linear-gradient(135deg, ${theme.primary}, ${theme.accent}, ${lightenColor(theme.accent, 10)})`);
                
                // Find and select matching preset (if any)
                let matchingPreset = findMatchingPreset(theme);
                if (matchingPreset) {
                    // Update preset selection
                    presetOptions.forEach(p => p.classList.remove('active'));
                    document.querySelector(`.preset-${matchingPreset}`).classList.add('active');
                }
                
                // Update color option selections
                updateColorOptionsFromTheme(theme);
            } catch (error) {
                console.error('Error loading saved theme:', error);
                applyThemePreset('lavender');
            }
        } else {
            // No saved theme, apply default theme
            applyThemePreset('lavender');
            updateColorOptionSelections('lavender');
        }
    }

    /**
     * Find matching preset for a theme
     * @param {Object} theme - The theme to match
     * @returns {string|null} - The matching preset name or null
     */
    function findMatchingPreset(theme) {
        for (const [name, preset] of Object.entries(themePresets)) {
            if (preset.primary === theme.primary && 
                preset.primaryDark === theme.primaryDark && 
                preset.primaryLight === theme.primaryLight && 
                preset.accent === theme.accent) {
                return name;
            }
        }
        return null;
    }

    /**
     * Update color option selections based on theme
     * @param {Object} theme - The theme object
     */
    function updateColorOptionsFromTheme(theme) {
        // Update primary color selection
        let primaryMatch = false;
        document.querySelectorAll('.color-option[data-color="primary"]').forEach(opt => {
            if (opt.getAttribute('data-value') === theme.primary) {
                opt.classList.add('active');
                primaryMatch = true;
            } else {
                opt.classList.remove('active');
            }
        });

        // Update accent color selection
        let accentMatch = false;
        document.querySelectorAll('.color-option[data-color="accent"]').forEach(opt => {
            if (opt.getAttribute('data-value') === theme.accent) {
                opt.classList.add('active');
                accentMatch = true;
            } else {
                opt.classList.remove('active');
            }
        });
    }

    /**
     * Darken a hex color by the specified percentage
     * @param {string} color - The hex color to darken
     * @param {number} percent - The percentage to darken by
     * @returns {string} - The darkened hex color
     */
    function darkenColor(color, percent) {
        // Remove # if present
        color = color.replace('#', '');
        
        // Convert to RGB
        let r = parseInt(color.substr(0, 2), 16);
        let g = parseInt(color.substr(2, 2), 16);
        let b = parseInt(color.substr(4, 2), 16);
        
        // Darken
        r = Math.max(0, Math.floor(r * (100 - percent) / 100));
        g = Math.max(0, Math.floor(g * (100 - percent) / 100));
        b = Math.max(0, Math.floor(b * (100 - percent) / 100));
        
        // Convert back to hex
        return '#' + 
               r.toString(16).padStart(2, '0') + 
               g.toString(16).padStart(2, '0') + 
               b.toString(16).padStart(2, '0');
    }

    /**
     * Lighten a hex color by the specified percentage
     * @param {string} color - The hex color to lighten
     * @param {number} percent - The percentage to lighten by
     * @returns {string} - The lightened hex color
     */
    function lightenColor(color, percent) {
        // Remove # if present
        color = color.replace('#', '');
        
        // Convert to RGB
        let r = parseInt(color.substr(0, 2), 16);
        let g = parseInt(color.substr(2, 2), 16);
        let b = parseInt(color.substr(4, 2), 16);
        
        // Lighten
        r = Math.min(255, Math.floor(r + (255 - r) * percent / 100));
        g = Math.min(255, Math.floor(g + (255 - g) * percent / 100));
        b = Math.min(255, Math.floor(b + (255 - b) * percent / 100));
        
        // Convert back to hex
        return '#' + 
               r.toString(16).padStart(2, '0') + 
               g.toString(16).padStart(2, '0') + 
               b.toString(16).padStart(2, '0');
    }
}); 