package com.example.ewaste.Utils;

import io.github.cdimascio.dotenv.Dotenv;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class QwenApiClientEvent {
    static Dotenv dotenv = Dotenv.configure()
            .directory("C:/Users/User/Documents/e-waste/e-waste") // Adjust the path accordingly
            .filename(".env")
            .load();
    // Replace with your actual API key
    private static final String API_KEY = dotenv.get("QWEN_API_KEY");
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
            URL url = new URL(API_URL);
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
                    + "             \"url\": \"data:image/jpeg;base64," + imageUrl + "\"" // Use base64-encoded image
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

            // Print the full response for debugging
            System.out.println("API Response: " + fullResponse);

            // Parse the JSON response
            JSONObject jsonResponse = new JSONObject(fullResponse);

            // Check if the response contains an error
            if (jsonResponse.has("error")) {
                JSONObject errorObject = jsonResponse.getJSONObject("error");
                String errorMessage = errorObject.getString("message"); // Extract the error message
                return "API Error: " + errorMessage;
            }

            // Check if the response contains "choices"
            if (jsonResponse.has("choices")) {
                JSONArray choices = jsonResponse.getJSONArray("choices");
                if (choices.length() > 0) {
                    JSONObject firstChoice = choices.getJSONObject(0);
                    JSONObject message = firstChoice.getJSONObject("message");
                    return message.getString("content");
                } else {
                    return "No choices found in response.";
                }
            } else {
                return "Unexpected API response: " + fullResponse;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error calling API: " + e.getMessage();
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
    public static void main(String[] args) {
        String prompt = "What is in this image?";
        String imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/dd/Gfp-wisconsin-madison-the-nature-boardwalk.jpg/2560px-Gfp-wisconsin-madison-the-nature-boardwalk.jpg";

        String extractedContent = generateTextFromImage(prompt, imageUrl);
        System.out.println("Extracted Content: " + extractedContent);
    }
}