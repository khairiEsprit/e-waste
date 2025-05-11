package com.example.ewaste.Utils;

import com.example.ewaste.Utils.SimpleJsonParser.JSONArray;
import com.example.ewaste.Utils.SimpleJsonParser.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
public class QwenApiClient {
    // Get API key from DotenvConfig
    private static final String API_KEY = DotenvConfig.get("QWEN_API_KEY", "dummy-qwen-api-key");
    private static final String API_URL = "https://openrouter.ai/api/v1/chat/completions";

    /**
     * Calls the Qwen API with a text prompt and an image URL,
     * then extracts and returns only the "content" field from the response.
     *
     * @param promptText The text prompt (e.g., "What is in this image?")
     * @param imageUrl   The URL of the image to analyze.
     * @return The extracted content from the API response.
     */
    public static String generateTextFromImage(String promptText, String imageUrl) {
        try {
            // Setup connection
            URL url = new java.net.URI(API_URL).toURL();
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Authorization", "Bearer " + API_KEY);
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);

            // Construct JSON payload
            String jsonInputString = "{"
                    + "\"model\": \"qwen/qwen2.5-vl-72b-instruct:free\","
                    + "\"messages\": ["
                    + "  {"
                    + "    \"role\": \"user\","
                    + "    \"content\": ["
                    + "      {"
                    + "         \"type\": \"text\","
                    + "         \"text\": \"" + escapeJson(promptText) + "\""
                    + "      },"
                    + "      {"
                    + "         \"type\": \"image_url\","
                    + "         \"image_url\": {"
                    + "             \"url\": \"" + escapeJson(imageUrl) + "\""
                    + "         }"
                    + "      }"
                    + "    ]"
                    + "  }"
                    + "]"
                    + "}";

            // Send the request
            try (OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Read the response
            int responseCode = con.getResponseCode();
            BufferedReader br;
            if (responseCode >= 200 && responseCode < 300) {
                br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
            } else {
                br = new BufferedReader(new InputStreamReader(con.getErrorStream(), "utf-8"));
            }

            StringBuilder responseBuilder = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                responseBuilder.append(responseLine.trim());
            }
            String fullResponse = responseBuilder.toString();

            // Parse the JSON response and extract the "content"
            JSONObject jsonResponse = new JSONObject(fullResponse);
            JSONArray choices = jsonResponse.getJSONArray("choices");
            if (choices.length() > 0) {
                JSONObject firstChoice = choices.getJSONObject(0);
                JSONObject message = firstChoice.getJSONObject("message");
                return message.getString("content");
            } else {
                return "No choices found in response.";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * A simple helper to escape special JSON characters.
     *
     * @param text The raw text.
     * @return The escaped text.
     */
    private static String escapeJson(String text) {
        return text.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    // Example usage
//    public static void main(String[] args) {
//        String prompt = "What is in this image?";
//        String imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/dd/Gfp-wisconsin-madison-the-nature-boardwalk.jpg/2560px-Gfp-wisconsin-madison-the-nature-boardwalk.jpg";
//
//        String extractedContent = generateTextFromImage(prompt, imageUrl);
//        System.out.println("Extracted Content: " + extractedContent);
//    }
}
