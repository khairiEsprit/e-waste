package com.example.ewaste.Controllers;
// ChatbotInterface.java
import com.example.ewaste.Repository.PoubelleRepository;
import io.github.cdimascio.dotenv.Dotenv;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import javafx.animation.TranslateTransition;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class ChatBotInterface {
    Dotenv dotenv = Dotenv.configure()
            .directory("C:/Users/User/Documents/e-waste/e-waste") // Adjust the path accordingly
            .filename(".env")
            .load();
    String apiKey = dotenv.get("OPENAI_API_KEY");
    private static final String OPENAI_ENDPOINT = "https://api.openai.com/v1/chat/completions";
    private VBox chatBox;
    private ListView<HBox> chatListView;
    private TextField inputField;
    private Dialog<Void> chatbotDialog;
    private boolean isChatbotVisible = false;

//    public void toggleChatbot() {
//        if (isChatbotVisible) {
//            closeChatbot();  // Close the chatbot if it's visible
//        } else {
//            openChatbot();  // Open the chatbot if it's not visible
//        }
//    }

    public void openChatbot() {
        if (chatbotDialog == null) {
            // Create a new dialog instance for the chatbot
            chatbotDialog = new Dialog<>();
            chatbotDialog.setTitle("Chat Assistant");

            // Initialize the chat components
            chatListView = new ListView<>();
            chatListView.setPrefHeight(400);
            chatListView.setFocusTraversable(false);

            inputField = new TextField();
            inputField.setPromptText("Type your message...");
            inputField.setPrefWidth(400);

            Button sendButton = new Button("Send");
            sendButton.setOnAction(e -> sendMessage());

            HBox inputBox = new HBox(10, inputField, sendButton);
            inputBox.setPadding(new Insets(10));
            inputBox.setAlignment(Pos.CENTER);

            // Set up the chat box layout
            chatBox = new VBox(10, chatListView, inputBox);
            chatBox.setPadding(new Insets(10));
            chatBox.setStyle("-fx-background-color: white; -fx-border-radius: 10; -fx-border-color: #ccc;");

            // Set the content of the dialog to the chatBox
            chatbotDialog.getDialogPane().setContent(chatBox);

            // Reset dialog reference when closed
            chatbotDialog.setOnCloseRequest(event -> {
                closeChatbot();
                event.consume(); // Prevent default close if needed
            });

            // Update button types to handle close via OS window controls
            chatbotDialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
            Node closeButton = chatbotDialog.getDialogPane().lookupButton(ButtonType.CLOSE);
            closeButton.addEventFilter(ActionEvent.ACTION, event -> {
                closeChatbot();
                event.consume();
            });

            // Show the dialog
            chatbotDialog.show();

            // Mark the chatbot as visible
            isChatbotVisible = true;

            // Add entrance animation
            animateChatboxEntrance();
        }
    }

    private void closeChatbot() {
        if (chatbotDialog != null) {
            chatbotDialog.close();
            chatbotDialog = null; // Clear reference to allow reopening
            isChatbotVisible = false;
        }
    }

    private void animateChatboxEntrance() {
        TranslateTransition slideIn = new TranslateTransition(Duration.millis(500), chatBox);
        slideIn.setFromY(600);
        slideIn.setToY(0);
        slideIn.play();
    }



    private void sendMessage() {
        String userInput = inputField.getText().trim();
        if (userInput.isEmpty()) return;

        addMessage("User: " + userInput, Sender.USER);
        inputField.clear();

        addMessage("Bot: ...", Sender.BOT); // Temporary message

        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                return callOpenAI(userInput);
            }
        };

        task.setOnSucceeded(event -> {
            removeLastMessage();
            addMessage("Bot: " + task.getValue(), Sender.BOT);
        });

        task.setOnFailed(event -> {
            removeLastMessage();
            addMessage("Bot: Error occurred.", Sender.BOT);
        });

        new Thread(task).start();
    }

    private void addMessage(String message, Sender sender) {
        Label label = new Label(message);
        label.setWrapText(true);
        label.setMaxWidth(350);
        label.setStyle("-fx-font-size: 14px; -fx-padding: 10;");

        HBox container = new HBox(label);
        container.setPadding(new Insets(5));

        if (sender == Sender.USER) {
            container.setAlignment(Pos.CENTER_RIGHT);
            container.setStyle("-fx-background-color: #DCF8C6; -fx-background-radius: 10;");
        } else {
            container.setAlignment(Pos.CENTER_LEFT);
            container.setStyle("-fx-background-color: #F1F0F0; -fx-background-radius: 10;");
        }

        chatListView.getItems().add(container);
        chatListView.scrollTo(chatListView.getItems().size() - 1);
    }

    private void removeLastMessage() {
        int size = chatListView.getItems().size();
        if (size > 0) {
            chatListView.getItems().remove(size - 1);
        }
    }

    PoubelleRepository pr = new PoubelleRepository();
    String binData = pr.getLastTenPoubellesForOpenAI(1);

    // 📡 Call OpenAI API
    private String callOpenAI(String userInput) throws Exception {
        String systemPrompt = "You are Smart Waste Assistant, a virtual assistant integrated into a waste management system's admin dashboard. "
                + "Your sole responsibility is to provide real-time data summaries for waste bins. "
                + "The following is the current data for the last 10 bins:\n" + binData
                + "\nNote: Although each bin record includes latitude and longitude, please do not use the exact coordinates in your summary. "
                + "Instead, convert the coordinates into the corresponding country name. For example, if a bin's coordinates indicate a location in tunisia,ariana, simply state 'ariana'. "
                + "Now, based on the above data, provide a summary of the bin statuses focusing on their fill levels, operational status, and the country name where each bin is located.";

        // Use Gson to build the JSON payload.
        Gson gson = new Gson();
        JsonObject payload = new JsonObject();
        payload.addProperty("model", "gpt-4o-mini");

        JsonArray messages = new JsonArray();

        JsonObject systemMessage = new JsonObject();
        systemMessage.addProperty("role", "system");
        systemMessage.addProperty("content", systemPrompt);
        messages.add(systemMessage);

        JsonObject userMessage = new JsonObject();
        userMessage.addProperty("role", "user");
        userMessage.addProperty("content", userInput);
        messages.add(userMessage);

        payload.add("messages", messages);

        String jsonPayload = gson.toJson(payload);

        // Debug: print the JSON payload
        System.out.println("JSON Payload: " + jsonPayload);

        // Create and send the HTTP request.
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(OPENAI_ENDPOINT))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Debug: print the raw response
        System.out.println("Raw response from OpenAI: " + response.body());

        JsonObject jsonResponse = gson.fromJson(response.body(), JsonObject.class);
        JsonArray choices = jsonResponse.getAsJsonArray("choices");

        if (choices != null && choices.size() > 0) {
            JsonObject firstChoice = choices.get(0).getAsJsonObject();
            JsonObject message = firstChoice.getAsJsonObject("message");
            if (message != null && message.has("content")) {
                return message.get("content").getAsString().trim();
            }
        }
        return "No response from AI.";
    }

    private enum Sender { USER, BOT }
}
