package com.example.ewaste.Utils;

import java.io.*;
import java.nio.file.*;
import java.util.Base64;
import java.util.UUID;

/**
 * Utility class for handling images
 */
public class ImageUtils {
    
    // Directory to store images
    private static final String IMAGE_DIR = "images";
    
    /**
     * Save a base64 encoded image to a file and return the file path
     * @param base64Image The base64 encoded image
     * @param fileExtension The file extension (jpg, png, etc.)
     * @return The file path where the image was saved
     */
    public static String saveBase64Image(String base64Image, String fileExtension) {
        try {
            // Create the images directory if it doesn't exist
            Path imageDirPath = Paths.get(IMAGE_DIR);
            if (!Files.exists(imageDirPath)) {
                Files.createDirectories(imageDirPath);
            }
            
            // Generate a unique filename
            String fileName = UUID.randomUUID().toString() + "." + fileExtension;
            Path filePath = imageDirPath.resolve(fileName);
            
            // Decode the base64 image
            byte[] imageBytes = Base64.getDecoder().decode(base64Image);
            
            // Write the image to a file
            Files.write(filePath, imageBytes);
            
            // Return the relative path to the image
            return filePath.toString().replace("\\", "/");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Extract the file extension from a file name
     * @param fileName The file name
     * @return The file extension
     */
    public static String getFileExtension(String fileName) {
        if (fileName == null) {
            return "";
        }
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return fileName.substring(lastDotIndex + 1).toLowerCase();
    }
    
    /**
     * Check if a base64 string is too large for the database
     * @param base64String The base64 string
     * @param maxLength The maximum length allowed
     * @return True if the string is too large, false otherwise
     */
    public static boolean isBase64TooLarge(String base64String, int maxLength) {
        return base64String != null && base64String.length() > maxLength;
    }
    
    /**
     * Load an image from a file path and return it as a base64 string
     * @param filePath The file path
     * @return The base64 encoded image
     */
    public static String loadImageAsBase64(String filePath) {
        try {
            Path path = Paths.get(filePath);
            if (!Files.exists(path)) {
                return null;
            }
            
            byte[] imageBytes = Files.readAllBytes(path);
            return Base64.getEncoder().encodeToString(imageBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
