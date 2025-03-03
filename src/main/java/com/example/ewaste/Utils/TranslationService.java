package com.example.ewaste.Utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class TranslationService {
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String MODEL = "gpt-4o-mini"; // Modèle OpenAI utilisé

    // Chargement de la clé API depuis le fichier .env
    static Dotenv dotenv = Dotenv.configure()
            .directory("C:/Users/User/Documents/e-waste/e-waste") // Ajuste ce chemin si nécessaire
            .filename(".env")
            .load();
    private static final String API_KEY = dotenv.get("APIKEY");

    public String translateText(String text, String targetLanguage) {
        try {
            HttpClient client = HttpClient.newHttpClient();

            // Création du JSON pour la requête
            String jsonRequest = "{\n" +
                    "  \"model\": \"" + MODEL + "\",\n" +
                    "  \"messages\": [\n" +
                    "    {\"role\": \"system\", \"content\": \"You are a translator. Translate all input to " + targetLanguage + ".\"},\n" +
                    "    {\"role\": \"user\", \"content\": \"" + text + "\"}\n" +
                    "  ]\n" +
                    "}";

            // Construction de la requête HTTP
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + API_KEY)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonRequest))
                    .build();

            // Envoi de la requête et réception de la réponse
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            return extractTranslation(response.body());

        } catch (IOException e) {
            System.err.println("I/O Error: " + e.getMessage());
            return "{\"error\": \"Error during translation: " + e.getMessage() + "\"}";
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "Request interrupted: " + e.getMessage();
        }
    }

    private String extractTranslation(String jsonResponse) {
        try {
            JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
            System.out.println(jsonObject); // Debug : afficher la réponse complète

            // Vérifier si une erreur est présente dans la réponse
            if (jsonObject.has("error")) {
                String errorMessage = jsonObject.getAsJsonObject("error").get("message").getAsString();
                return "Erreur API : " + errorMessage;
            }

            // Vérifier si "choices" est présent et contient au moins un élément
            if (jsonObject.has("choices") && jsonObject.getAsJsonArray("choices").size() > 0) {
                return jsonObject.getAsJsonArray("choices")
                        .get(0).getAsJsonObject()
                        .getAsJsonObject("message")
                        .get("content").getAsString().trim();
            } else {
                return "Aucune réponse valide de l'API.";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Erreur d'extraction : " + e.getMessage();
        }
    }
    // Test rapide
    public static void main(String[] args) throws IOException, InterruptedException {
        TranslationService api = new TranslationService();
        String translation = api.translateText("Hello, how are you?", "fr");
        System.out.println("Traduction : " + translation);
    }
}
