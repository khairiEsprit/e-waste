package com.example.ewaste.Utils;

import io.github.cdimascio.dotenv.Dotenv;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class HuggingFaceChatService {
    private static final String API_URL = "https://api-inference.huggingface.co/models/HuggingFaceH4/zephyr-7b-beta";
    static Dotenv dotenv = Dotenv.configure()
            .directory("C:/Users/User/Documents/e-waste/e-waste") // Adjust the path accordingly
            .filename(".env")
            .load();
    static String apiKey = dotenv.get("HUGGINGFACE_API_KEY");
    public static String sendMessage(String message) throws IOException {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS) // Timeout de connexion
                .readTimeout(60, TimeUnit.SECONDS)    // Timeout de lecture
                .writeTimeout(60, TimeUnit.SECONDS)   // Timeout d'écriture
                .build();
        ObjectMapper objectMapper = new ObjectMapper();

        // Build JSON request
        String json = "{ \"inputs\": \"" + message + "\" }";

        RequestBody body = RequestBody.create(json, MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(API_URL)
                .header("Authorization", "Bearer " + apiKey)  // ✅ Fixed: Use "Bearer " before API Key
                .header("Content-Type", "application/json")
                .post(body)
                .build();

        Response response = client.newCall(request).execute();
        String responseBody = response.body().string();

        // Parse JSON response
        JsonNode jsonResponse = objectMapper.readTree(responseBody);

        // ✅ Check if response contains valid data
        if (jsonResponse.isArray() && jsonResponse.size() > 0 && jsonResponse.get(0).has("generated_text")) {
            return jsonResponse.get(0).get("generated_text").asText();
        } else {
            return "Error: Invalid response from API → " + responseBody;
        }
    }
}
