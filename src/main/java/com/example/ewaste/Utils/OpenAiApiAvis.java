package com.example.ewaste.Utils;

import com.example.ewaste.Entities.TextAnalysisResult;
import com.example.ewaste.Utils.SimpleJsonParser.JSONArray;
import com.example.ewaste.Utils.SimpleJsonParser.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.Scanner;

public class OpenAiApiAvis {
    private static final String API_URL = "https://api.openai.com/v1/moderations";

    // Use the DotenvConfig utility to get the API key
    private static final String API_KEY = DotenvConfig.get("APIKEY", "dummy-api-key"); // Fallback to dummy key if not found
    public TextAnalysisResult detectBadWords(String text) {
        try {
            // Créer la connexion HTTP
            HttpURLConnection connection = (HttpURLConnection) new java.net.URI(API_URL).toURL().openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + API_KEY);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // Préparer le JSON à envoyer
            String jsonInputString = "{\"input\": \"" + text + "\"}";

            // Envoyer la requête
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Lire la réponse
            Scanner scanner = new Scanner(connection.getInputStream(), "utf-8");
            String responseBody = scanner.useDelimiter("\\A").next();
            scanner.close();

            // Analyser la réponse JSON
            return parseResponse(responseBody);
        } catch (Exception e) {
            e.printStackTrace();
            return new TextAnalysisResult(false, "Erreur lors de la détection");
        }
    }

    private TextAnalysisResult parseResponse(String responseBody) {
        JSONObject jsonResponse = new JSONObject(responseBody);
        JSONArray results = jsonResponse.getJSONArray("results");

        if (results.length() > 0) {
            JSONObject firstResult = results.getJSONObject(0);
            boolean flagged = firstResult.getBoolean("flagged");
            JSONObject categories = firstResult.getJSONObject("categories");

            return new TextAnalysisResult(flagged, categories.toString());
        }

        return new TextAnalysisResult(false, "Aucune catégorie détectée");
    }
}
