package com.example.ewaste.Repository;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONObject;

public class GeminiRepository {

    public void analyzeWeatherData(double latitude, double longitude, double temperature) {
        try {
            String geminiApiKey = "AIzaSyDsEBiUTtvIIlCbvRiiNrnvN_IxeNyXUDA";

            JSONObject prompt = new JSONObject();
            prompt.put("contents", new JSONObject()
                    .put("parts", new JSONObject()
                            .put("text", "Analyse ces données : Latitude=" + latitude +
                                    ", Longitude=" + longitude + ", Température=" + temperature + "°C. " +
                                    "Donne-moi le nom de la ville et une analyse des conditions météorologiques."))
            );

            // URL de l'API Gemini
            String geminiApiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key="+geminiApiKey; // Remplace par l'URL correcte

            // Création de la requête HTTP avec la clé API dans l'en-tête
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(geminiApiUrl))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + geminiApiKey) // Clé API ajoutée ici
                    .POST(HttpRequest.BodyPublishers.ofString(prompt.toString()))
                    .build();

            // Envoi de la requête
            HttpClient client = HttpClient.newHttpClient();
            client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .thenAccept(response -> handleGeminiResponse(response))
                    .exceptionally(e -> {
                        e.printStackTrace();
                        return null;
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleGeminiResponse(String response) {
        // Implémentation de la gestion de la réponse de l'API Gemini
        System.out.println("Réponse de Gemini: " + response);
        // Vous pouvez analyser et traiter la réponse ici.
    }

    public static void main(String[] args) {
        GeminiRepository repository = new GeminiRepository();
        repository.analyzeWeatherData(37.7749, -122.4194, 20.5);

        //System.out.println(repository.analyzeWeatherData(37.7749, -122.4194, 20.5));// Exemple d'appel
    }
}
