package com.example.ewaste.Utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class GeminiApi {
    private final Dotenv dotenv = Dotenv.load();

    public String genererRapport(String prompt) throws IOException, InterruptedException {
        String apiKey = dotenv.get("GEMINI_KEY");
        if(apiKey == null || apiKey.isEmpty()){
            throw new IllegalArgumentException("Api key not set");
        }
        try {

//            String escapedPrompt = prompt.replace("\"", "\\\"");
//
//            String requestBody = String.format(
//                    "{\"contents\":[{\"parts\":[{\"text\":\"%s\"}]}]}",
//                    escapedPrompt
//            );
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key="+apiKey))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(
                            "{'contents':[{'parts':[{'text': \"" + prompt + "\"}]}]}"
                    ))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return extraireTexteReponse(response.body());
        } catch (IOException e) {
            // Gestion des erreurs d'I/O (connexion, lecture/réponse, etc.)
            System.err.println("Erreur I/O : " + e.getMessage());
            return "{\"error\": \"Erreur lors de la génération du rapport : " + e.getMessage() + "\"}";
        }
    catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        return "Requête interrompue : " + e.getMessage();
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
        GeminiApi api = new GeminiApi();
        String rapport =  api.genererRapport(prompt);
        System.out.println(rapport);
    }

    private String extraireTexteReponse(String jsonResponse) {
        try {
            JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
//            System.out.println(jsonObject);
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


