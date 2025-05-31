import cv2
import numpy as np
import torch
import random
import json
import argparse
from PIL import Image
from segment_anything import sam_model_registry, SamAutomaticMaskGenerator
import matplotlib.pyplot as plt

# Style library will be loaded from command line argument
STYLE_LIBRARY = {}

def hex_to_bgr(hex_color):
    hex_color = hex_color.lstrip('#')
    r = int(hex_color[0:2], 16)
    g = int(hex_color[2:4], 16)
    b = int(hex_color[4:6], 16)
    return (b, g, r)

def classify_region(mask, image=None):
    """
    Enhanced region classifier for architectural elements.
    Returns: one of 'main_walls', 'side_walls', 'upper_lower_walls', 'pillars', 'balcony', 'roof', 'doors', 'windows'
    """
    ys, xs = np.where(mask)
    if len(ys) == 0 or len(xs) == 0:
        return "main_walls"  # fallback

    h, w = mask.shape
    min_y, max_y = ys.min(), ys.max()
    min_x, max_x = xs.min(), xs.max()
    box_height = max_y - min_y + 1
    box_width = max_x - min_x + 1
    box_area = box_height * box_width
    mask_area = np.sum(mask)
    area_ratio = mask_area / (h * w)
    aspect = box_width / (box_height + 1e-5)
    center_y = (min_y + max_y) / 2 / h
    center_x = (min_x + max_x) / 2 / w

    # Average color (if image is provided)
    avg_b, avg_g, avg_r = 0, 0, 0
    if image is not None:
        region_pixels = image[mask == 1]
        if len(region_pixels) > 0:
            avg_b, avg_g, avg_r = np.mean(region_pixels, axis=0)

    # --- Heuristics ---
    if center_y < 0.2 and aspect > 2.5 and box_height < 0.15 * h:
        return "roof"
    if 0.2 < center_y < 0.5 and aspect > 2.0 and 0.08 < box_height/h < 0.25:
        return "balcony"
    if aspect < 0.25 and box_height > 0.4 * h and (center_x < 0.2 or center_x > 0.8):
        return "pillars"
    if area_ratio > 0.15 and (center_x < 0.3 or center_x > 0.7):
        return "side_walls"
    if area_ratio > 0.15 and center_y < 0.5:
        return "upper_lower_walls"
    if area_ratio > 0.15 and center_y >= 0.5:
        return "main_walls"
    if center_y > 0.7 and aspect > 0.6 and box_area > 0.01 * h * w:
        if avg_r < 120 and avg_g < 120 and avg_b < 120:
            return "doors"
    if (aspect < 0.5 and box_height > 0.1 * h) or (center_y < 0.6 and box_area < 0.15 * h * w):
        if avg_b > avg_r and avg_b > avg_g and avg_b > 130:
            return "windows"
        else:
            return "windows"
    if area_ratio > 0.10:
        return "main_walls"
    return "main_walls"

def recommend_style_for_region(region_type, style, style_library):
    """
    Picks a random color/texture/material/finish for the region type from the selected style.
    """
    region_styles = style_library.get(region_type, {})
    style_list = region_styles.get(style, [])
    if not style_list:
        # fallback to main_walls if region_type not found
        style_list = style_library.get("main_walls", {}).get(style, [])
    if not style_list:
        # fallback to any available style
        for region in style_library:
            if style_library[region].get(style):
                style_list = style_library[region][style]
                break
    rec = random.choice(style_list)
    return rec["color"], rec["texture"], rec["material"], rec["finish"], rec["rating"], rec["keywords"]

def apply_style_recommendations(original_img, masks, style, style_library):
    output_img = original_img.copy()
    for idx, mask_dict in enumerate(masks):
        mask = mask_dict["segmentation"]
        region_type = classify_region(mask, original_img)
        color_hex, texture, material, finish, rating, keywords = recommend_style_for_region(region_type, style, style_library)
        print(f"[Region {idx}] {region_type.upper()} | Color: {color_hex} | Texture: {texture} | Material: {material} | Finish: {finish} | Rating: {rating} | Keywords: {keywords}")
        color_bgr = hex_to_bgr(color_hex)
        output_img[mask == 1] = color_bgr
    return output_img

def main():
    # Parse command line arguments
    parser = argparse.ArgumentParser(description='AI House Exterior Design')
    parser.add_argument('--input', required=True, help='Path to input image')
    parser.add_argument('--output_styled', required=True, help='Path to save styled output image')
    parser.add_argument('--output_blended', required=True, help='Path to save blended output image')
    parser.add_argument('--style', required=True, help='Design style to apply')
    parser.add_argument('--model', required=True, help='Path to SAM model checkpoint')
    parser.add_argument('--style_library', required=True, help='Path to style library JSON')
    parser.add_argument('--blend_alpha', type=float, default=0.5, help='Blending factor (0.0-1.0)')
    
    args = parser.parse_args()
    
    # Load style library
    print(f"Loading style library from {args.style_library}")
    with open(args.style_library, "r") as file:
        STYLE_LIBRARY = json.load(file)
    
    # Load SAM model
    print(f"Loading SAM model from {args.model}")
    model_type = "vit_h"
    device = "cuda" if torch.cuda.is_available() else "cpu"
    print(f"Using device: {device}")
    sam_model = sam_model_registry[model_type](checkpoint=args.model)
    sam_model.to(device)
    mask_generator = SamAutomaticMaskGenerator(sam_model)
    
    # Process input image
    print(f"Processing input image: {args.input}")
    img = Image.open(args.input)
    img_np = np.array(img)
    
    # Convert image to BGR for OpenCV processing
    if img_np.shape[2] == 4:  # RGBA
        img_bgr = cv2.cvtColor(img_np, cv2.COLOR_RGBA2BGR)
    elif img_np.shape[2] == 3:  # RGB
        img_bgr = cv2.cvtColor(img_np, cv2.COLOR_RGB2BGR)
    else:
        img_bgr = cv2.cvtColor(img_np, cv2.COLOR_GRAY2BGR)
    
    # Generate masks using SAM
    print("Generating image segments with SAM...")
    masks = mask_generator.generate(img_bgr)
    print(f"Generated {len(masks)} masks")
    
    # Apply selected style
    print(f"Applying {args.style} style...")
    styled_image = apply_style_recommendations(img_bgr, masks, args.style, STYLE_LIBRARY)
    styled_image_rgb = cv2.cvtColor(styled_image, cv2.COLOR_BGR2RGB)
    
    # Save styled image
    print(f"Saving styled image to {args.output_styled}")
    Image.fromarray(styled_image_rgb).save(args.output_styled)
    
    # Create blended image
    if img_np.shape[:2] != styled_image_rgb.shape[:2]:
        print("Resizing styled image to match original image dimensions.")
        styled_image_rgb = cv2.resize(styled_image_rgb, (img_np.shape[1], img_np.shape[0]))
    
    # Convert both images to BGR for blending
    if img_np.shape[2] == 4:
        img_np_bgr = cv2.cvtColor(img_np, cv2.COLOR_RGBA2BGR)
    elif img_np.shape[2] == 3:
        img_np_bgr = cv2.cvtColor(img_np, cv2.COLOR_RGB2BGR)
    else:
        img_np_bgr = cv2.cvtColor(img_np, cv2.COLOR_GRAY2BGR)
    
    styled_image_bgr = cv2.cvtColor(styled_image_rgb, cv2.COLOR_RGB2BGR)
    
    # Blend images
    print(f"Creating blended image with alpha={args.blend_alpha}...")
    img_np_bgr = img_np_bgr.astype(np.uint8)
    styled_image_bgr = styled_image_bgr.astype(np.uint8)
    blended_bgr = cv2.addWeighted(img_np_bgr, 1 - args.blend_alpha, styled_image_bgr, args.blend_alpha, 0)
    blended_rgb = cv2.cvtColor(blended_bgr, cv2.COLOR_BGR2RGB)
    
    # Save blended image
    print(f"Saving blended image to {args.output_blended}")
    Image.fromarray(blended_rgb).save(args.output_blended)
    
    print("Processing completed successfully.")

if __name__ == "__main__":
    main()