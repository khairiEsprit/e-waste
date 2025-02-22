package com.example.ewaste.utils;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class OpenAiApi {
    Dotenv dotenv = Dotenv.configure()
            .directory("C:/Users/User/Documents/e-waste/e-waste") // Adjust the path accordingly
            .filename(".env")
            .load();

    public String genererRapport(String prompt) throws IOException, InterruptedException {
        String apiKey = dotenv.get("OPENAI_API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalArgumentException("API key not set");
        }
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(
                            "{\n" +
                                    "  \"model\": \"gpt-4o-mini\",\n" +
                                    "  \"messages\": [\n" +
                                    "    {\"role\": \"system\", \"content\": \"You are a helpful assistant.\"},\n" +
                                    "    {\"role\": \"user\", \"content\": \"" + prompt + "\"}\n" +
                                    "  ]\n" +
                                    "}"
                    ))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return extraireTexteReponse(response.body());
        } catch (IOException e) {
            // Handle I/O errors (connection, reading/response, etc.)
            System.err.println("I/O Error: " + e.getMessage());
            return "{\"error\": \"Error generating the report: " + e.getMessage() + "\"}";
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "Request interrupted: " + e.getMessage();
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        String prompt = "Données pour le rapport mensuel - Novembre 2024 :" +
                "- Tâches : 150 (130 terminées, taux de réussite : 87%)." +
                "- Réclamations : 45 (40 résolues, taux de résolution : 89%)." +
                "- Avis des citoyens : Note moyenne de 4.2/5." +
                "  Commentaires fréquents : Collectes ponctuelles mais quelques retards en fin de mois." +
                "- Zones problématiques : Quartier X (retards fréquents)." +
                "Analyse ces données et rédige un rapport professionnel en français." +
                "Points clés : efficacité des collectes, satisfaction citoyenne, problèmes récurrents.";
        OpenAiApi api = new OpenAiApi();
        String rapport = api.genererRapport(prompt);
        System.out.println(rapport);
    }

    private String extraireTexteReponse(String jsonResponse) {
        try {
            JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
            System.out.println(jsonObject);
            return jsonObject.getAsJsonArray("choices")
                    .get(0).getAsJsonObject()
                    .get("message").getAsJsonObject()
                    .get("content").getAsString();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error extracting the text: " + e.getMessage();
        }
    }
}
