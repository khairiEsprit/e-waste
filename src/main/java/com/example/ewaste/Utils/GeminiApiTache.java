package com.example.ewaste.Utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class GeminiApiTache {

    private static final String API_KEY = "AIzaSyDsEBiUTtvIIlCbvRiiNrnvN_IxeNyXUDA";

    public String genererAnalyse(String prompt) throws IOException, InterruptedException {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + API_KEY))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(
                            "{'contents':[{'parts':[{'text': \"" + prompt + "\"}]}]}"
                    ))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Vérifiez le code de statut HTTP
            if (response.statusCode() != 200) {
                throw new IOException("Erreur API : " + response.statusCode() + " - " + response.body());
            }

            // Extrait la réponse JSON
            return extraireTexteReponse(response.body());
        } catch (IOException e) {
            System.err.println("Erreur I/O : " + e.getMessage());
            return "Erreur lors de la génération de l'analyse : " + e.getMessage();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "Requête interrompue : " + e.getMessage();
        }
    }

    private String extraireTexteReponse(String jsonResponse) {
        try {
            JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
            return jsonObject.getAsJsonArray("candidates")
                    .get(0).getAsJsonObject()
                    .getAsJsonObject("content")
                    .getAsJsonArray("parts")
                    .get(0).getAsJsonObject()
                    .get("text").getAsString();
        } catch (Exception e) {
            e.printStackTrace();
            return "Erreur lors de l'extraction du texte : " + e.getMessage();
        }
    }
}