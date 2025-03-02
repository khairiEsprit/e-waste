package com.example.ewaste.utils;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class BadWordFilter {
    private static final String API_URL = "https://www.purgomalum.com/service/json?text=";


    public static String filterBadWords(String text) {
        try {
            String encodedText = text.replace(" ", "%20"); // Encode spaces
            URL url = new URL(API_URL + encodedText);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Read API Response
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // Parse JSON Response
            JSONObject jsonResponse = new JSONObject(response.toString());
            return jsonResponse.getString("result"); // Filtered text
        } catch (Exception e) {
            e.printStackTrace();
            return text; // Return original text if API fails
        }
    }
}
