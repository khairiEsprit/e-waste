package com.example.ewaste.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Simplified utility class to handle environment variables without external dependencies
 */
public class DotenvConfig {
    private static Map<String, String> envVars = null;

    /**
     * Get the environment variables, initializing if necessary
     * @return The map of environment variables
     */
    private static synchronized Map<String, String> getEnvVars() {
        if (envVars == null) {
            envVars = loadEnvVars();
        }
        return envVars;
    }

    /**
     * Load environment variables from .env file
     * @return Map of environment variables
     */
    private static Map<String, String> loadEnvVars() {
        Map<String, String> vars = new HashMap<>();

        // Try to find .env file in various locations
        String[] possiblePaths = {
            ".",                                      // Current directory
            "./e-waste",                              // e-waste subdirectory
            System.getProperty("user.dir"),           // User's working directory
            System.getProperty("user.home") + "/Desktop/E-wasteJAVA", // User's desktop E-wasteJAVA directory
            "C:/Users/User/Documents/e-waste/e-waste" // Original hardcoded path
        };

        boolean found = false;
        for (String path : possiblePaths) {
            Path envPath = Paths.get(path, ".env");
            if (Files.exists(envPath)) {
                System.out.println("Found .env file at: " + envPath.toAbsolutePath());
                try (BufferedReader reader = new BufferedReader(new FileReader(envPath.toFile()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        parseLine(line, vars);
                    }
                    found = true;
                    break;
                } catch (IOException e) {
                    System.err.println("Error reading .env file: " + e.getMessage());
                }
            }
        }

        if (!found) {
            System.out.println("No .env file found. Using default values.");
            // Add default values
            vars.put("APIKEY", "dummy-api-key");
            vars.put("OPENAI_API_KEY", "dummy-openai-api-key");
            vars.put("QWEN_API_KEY", "dummy-qwen-api-key");
            vars.put("DB_URL", "jdbc:mysql://localhost:3306/symfonymaindatabase");
            vars.put("DB_USER", "root");
            vars.put("DB_PASSWORD", "");
            vars.put("GOOGLE_CLIENT_ID", "dummy-google-client-id");
            vars.put("GOOGLE_CLIENT_SECRET", "dummy-google-client-secret");
        }

        return vars;
    }

    /**
     * Parse a line from the .env file
     * @param line The line to parse
     * @param vars The map to add the variable to
     */
    private static void parseLine(String line, Map<String, String> vars) {
        line = line.trim();
        // Skip comments and empty lines
        if (line.isEmpty() || line.startsWith("#")) {
            return;
        }

        // Split by the first equals sign
        int equalsIndex = line.indexOf('=');
        if (equalsIndex > 0) {
            String key = line.substring(0, equalsIndex).trim();
            String value = line.substring(equalsIndex + 1).trim();

            // Remove quotes if present
            if (value.startsWith("\"") && value.endsWith("\"") ||
                value.startsWith("'") && value.endsWith("'")) {
                value = value.substring(1, value.length() - 1);
            }

            vars.put(key, value);
        }
    }

    /**
     * Get a value from the dotenv configuration
     * @param key The key to look up
     * @return The value, or null if not found
     */
    public static String get(String key) {
        return getEnvVars().get(key);
    }

    /**
     * Get a value from the dotenv configuration with a default value
     * @param key The key to look up
     * @param defaultValue The default value to return if the key is not found
     * @return The value, or the default value if not found
     */
    public static String get(String key, String defaultValue) {
        String value = getEnvVars().get(key);
        return value != null ? value : defaultValue;
    }
}
