package com.example.ewaste;

import com.example.ewaste.Utils.DotenvConfig;

/**
 * Simple test class to verify DotenvConfig is accessible
 */
public class DotenvTest {
    public static void main(String[] args) {
        try {
            System.out.println("Testing DotenvConfig...");
            String apiKey = DotenvConfig.get("APIKEY", "Not found");
            System.out.println("API Key: " + apiKey);
            System.out.println("DotenvConfig test successful!");
        } catch (Exception e) {
            System.err.println("Error testing DotenvConfig: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
