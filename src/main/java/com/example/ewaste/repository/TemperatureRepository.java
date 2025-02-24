package com.example.ewaste.repository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

public class TemperatureRepository {

    private static final String API_KEY = "482ec7cd423b49848782fe48cea8eda3";
    private static final String API_URL = "https://api.openweathermap.org/data/2.5/weather";

    public String getTemperature(double latitude, double longitude) {
        try {
            // Construire l'URL de l'API avec les coordonnées
            String urlString = API_URL + "?lat=" + latitude + "&lon=" + longitude + "&appid=" + API_KEY + "&units=metric";
            URL url = new URL(urlString);

            // Ouvrir une connexion HTTP
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Lire la réponse de l'API
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // Parser la réponse JSON
            JSONObject jsonResponse = new JSONObject(response.toString());
            JSONObject main = jsonResponse.getJSONObject("main");
            double temperature = main.getDouble("temp");

            return String.format("Température: %.1f°C", temperature);

        } catch (IOException e) {
            e.printStackTrace();
            return "Erreur lors de la récupération de la température.";
        }
    }
}