package com.example.ewaste.Repository;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.application.Platform;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class GeminiApi {

    @FXML
    private VBox chatHistory;

    @FXML
    private TextField userInput;

    private static final String API_KEY = "AIzaSyBpqh2QEB-yiLG3VEhHyM2RyBxLSvnL7ww";

    public void handleSendMessage(ActionEvent actionEvent) {
        String message = userInput.getText().trim();
        if (message.isEmpty()) {
            return;
        }

        // Affichage du message utilisateur
        HBox userMessage = createUserMessage(message);
        chatHistory.getChildren().add(userMessage);
        userInput.clear();

        // Appel API dans un thread séparé
        new Thread(() -> {
            String response = genererAnalyse(message);
            Platform.runLater(() -> {
                HBox botMessage = createBotMessage(response);
                chatHistory.getChildren().add(botMessage);
                scrollToBottom();
            });
        }).start();
    }

    private HBox createUserMessage(String text) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.setStyle("-fx-background-color: #DCF8C6; -fx-background-radius: 15; -fx-padding: 10;");
        HBox hbox = new HBox(label);
        hbox.setAlignment(Pos.CENTER_RIGHT);
        hbox.setPadding(new Insets(5, 10, 5, 10));
        HBox.setHgrow(hbox, Priority.ALWAYS);
        return hbox;
    }

    private HBox createBotMessage(String text) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 15; -fx-padding: 10;");
        HBox hbox = new HBox(label);
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.setPadding(new Insets(5, 10, 5, 10));
        HBox.setHgrow(hbox, Priority.ALWAYS);
        return hbox;
    }

    private void scrollToBottom() {
        ScrollPane scrollPane = (ScrollPane) chatHistory.getParent().getParent();
        scrollPane.setVvalue(1.0);
    }

    public String genererAnalyse(String prompt) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + API_KEY))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(
                            "{\"contents\":[{\"parts\":[{\"text\":\"" + prompt + "\"}]}]}"
                    ))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new IOException("Erreur API : " + response.statusCode() + " - " + response.body());
            }

            return extraireTexteReponse(response.body());
        } catch (IOException | InterruptedException e) {
            return "Erreur lors de la génération : " + e.getMessage();
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
            return "Erreur de parsing : " + e.getMessage();
        }
    }
}