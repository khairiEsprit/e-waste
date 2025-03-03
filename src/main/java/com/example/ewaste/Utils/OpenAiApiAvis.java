package com.example.ewaste.Utils;

import com.example.ewaste.Entities.TextAnalysisResult;
import io.github.cdimascio.dotenv.Dotenv;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class OpenAiApiAvis {
    private static final String API_URL = "https://api.openai.com/v1/moderations";

    static Dotenv dotenv = Dotenv.configure()
            .directory("C:/Users/HP/Desktop/pidev/e-waste") // Adjust the path accordingly
            .filename(".env")
            .load();
    private static final String API_KEY =dotenv.get("APIKEY"); // Remplace par ta clé API OpenAI
    public TextAnalysisResult detectBadWords(String text) {
        try {
            // Créer la connexion HTTP
            HttpURLConnection connection = (HttpURLConnection) new URL(API_URL).openConnection();
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
