package com.example.ewaste.Utils;

import io.github.cdimascio.dotenv.Dotenv;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Utility class to handle dotenv configuration in a flexible way
 */
public class DotenvConfig {
    private static Dotenv dotenv;
    
    /**
     * Get the Dotenv instance, initializing it if necessary
     * @return The Dotenv instance
     */
    public static synchronized Dotenv getDotenv() {
        if (dotenv == null) {
            dotenv = loadDotenv();
        }
        return dotenv;
    }
    
    /**
     * Load the dotenv configuration from various possible locations
     * @return The Dotenv instance
     */
    private static Dotenv loadDotenv() {
        // Try to find .env file in various locations
        String[] possiblePaths = {
            ".",                                      // Current directory
            "./e-waste",                              // e-waste subdirectory
            System.getProperty("user.dir"),           // User's working directory
            System.getProperty("user.home") + "/Desktop/E-wasteJAVA", // User's desktop E-wasteJAVA directory
            "C:/Users/User/Documents/e-waste/e-waste" // Original hardcoded path
        };
        
        for (String path : possiblePaths) {
            if (Files.exists(Paths.get(path, ".env"))) {
                System.out.println("Found .env file at: " + path);
                return Dotenv.configure()
                        .directory(path)
                        .filename(".env")
                        .load();
            }
        }
        
        // If no .env file is found, create a dummy one with default values
        System.out.println("No .env file found. Creating a dummy one with default values.");
        return createDummyDotenv();
    }
    
    /**
     * Create a dummy dotenv with default values
     * @return The Dotenv instance
     */
    private static Dotenv createDummyDotenv() {
        // Create a temporary directory to store the dummy .env file
        try {
            Path tempDir = Files.createTempDirectory("e-waste-dotenv");
            Path envFile = tempDir.resolve(".env");
            
            // Write dummy values to the .env file
            Files.writeString(envFile, 
                "APIKEY=dummy-api-key\n" +
                "OPENAI_API_KEY=dummy-openai-api-key\n" +
                "QWEN_API_KEY=dummy-qwen-api-key\n" +
                "HUGGINGFACE_API_KEY=dummy-huggingface-api-key\n" +
                "GEMINI_KEY=dummy-gemini-key\n" +
                "TWILIO_ACCOUNT_SID=dummy-twilio-sid\n" +
                "Twilio_AUTH_TOKEN=dummy-twilio-token\n" +
                "GOOGLE_CLIENT_ID=dummy-google-client-id\n" +
                "GOOGLE_CLIENT_SECRET=dummy-google-client-secret\n" +
                "GOOGLE_REDIRECT_URI=http://localhost:8080/callback\n" +
                "GOOGLE_TOKEN_URI=https://oauth2.googleapis.com/token\n" +
                "GOOGLE_USERINFO_URI=https://www.googleapis.com/oauth2/v3/userinfo"
            );
            
            return Dotenv.configure()
                    .directory(tempDir.toString())
                    .filename(".env")
                    .load();
        } catch (Exception e) {
            System.err.println("Error creating dummy .env file: " + e.getMessage());
            // Return an empty Dotenv as a last resort
            return Dotenv.configure().ignoreIfMissing().load();
        }
    }
    
    /**
     * Get a value from the dotenv configuration
     * @param key The key to look up
     * @return The value, or null if not found
     */
    public static String get(String key) {
        return getDotenv().get(key);
    }
    
    /**
     * Get a value from the dotenv configuration with a default value
     * @param key The key to look up
     * @param defaultValue The default value to return if the key is not found
     * @return The value, or the default value if not found
     */
    public static String get(String key, String defaultValue) {
        String value = getDotenv().get(key);
        return value != null ? value : defaultValue;
    }
}
