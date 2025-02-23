package com.example.ewaste.Controllers;
// ChatbotInterface.java
import com.example.ewaste.Entities.Message;
import com.example.ewaste.Entities.Sender;
import com.example.ewaste.Main;
import com.example.ewaste.Repository.PoubelleRepository;
import io.github.cdimascio.dotenv.Dotenv;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;
import javafx.animation.TranslateTransition;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;


public class ChatBotInterface {


    PoubelleRepository pr = new PoubelleRepository();
    String binData = pr.getLastTenPoubellesForOpenAI(1);
    private static final String CSS_FILE = "styles/chat-style.css";
    private static final double INITIAL_WIDTH = 450;
    private static final double INITIAL_HEIGHT = 450;
    Dotenv dotenv = Dotenv.configure()
            .directory("C:/Users/User/Documents/e-waste/e-waste") // Adjust the path accordingly
            .filename(".env")
            .load();
    String apiKey = dotenv.get("OPENAI_API_KEY");
    private static final String OPENAI_ENDPOINT = "https://api.openai.com/v1/chat/completions";
    private Dialog<Void> chatbotDialog;
    private ListView<Message> chatListView;
    private TextField inputField;
    private Button sendButton;
    private ExecutorService executorService;
    private void setupExecutorService() {
        executorService = Executors.newFixedThreadPool(2);
    }
    public void openChatbot() {
        if (chatbotDialog == null) {
            initializeDialog();
            setupExecutorService();
            animateEntrance();
        }
    }


    private void addMessage(Message message) {
        Platform.runLater(() -> {
            chatListView.getItems().add(message);
            chatListView.scrollTo(chatListView.getItems().size() - 1);
        });
    }

    private void initializeDialog() {
        chatbotDialog = new Dialog<>();
        chatbotDialog.setTitle("AI Assistant");
        chatbotDialog.getDialogPane().getStylesheets().add(Main.class.getResource(CSS_FILE).toExternalForm());

        createChatInterface();
        setupDialogCloseHandler();
        chatbotDialog.show();
    }

    private void createChatInterface() {
        VBox mainContainer = new VBox(10);
        mainContainer.setPrefSize(INITIAL_WIDTH, INITIAL_HEIGHT);
        mainContainer.setMaxHeight(Double.MAX_VALUE); // Allow vertical expansion
        mainContainer.getStyleClass().add("main-container");

        // Chat History
        chatListView = new ListView<>();
        chatListView.setCellFactory(this::createMessageCell);
        VBox.setVgrow(chatListView, Priority.ALWAYS); // Critical for vertical expansion
        chatListView.setMaxHeight(Double.MAX_VALUE); // Allow unlimited vertical growth
        chatListView.getStyleClass().add("chat-list");

        // Input Area
        HBox inputContainer = new HBox(30);
        inputContainer.getStyleClass().add("input-container");
        inputContainer.setMaxHeight(60); // Fixed height for input area


        inputField = new TextField();
        inputField.setPromptText("Type your message...");
        inputField.setOnAction(e -> sendMessage());
        HBox.setHgrow(inputField, Priority.ALWAYS);  // Make text field expand
        inputField.setMaxWidth(Double.MAX_VALUE);     // Allow unlimited horizontal expansion

        sendButton = new Button();
        sendButton.setGraphic(createSendIcon());
        sendButton.setOnAction(e -> sendMessage());
        sendButton.setPrefSize(40, 40);  // Fixed size for button

        inputContainer.getChildren().addAll(inputField, sendButton);
        mainContainer.getChildren().addAll(chatListView, inputContainer);

        chatbotDialog.getDialogPane().setContent(mainContainer);
    }

    private ListCell<Message> createMessageCell(ListView<Message> listView) {
        return new ListCell<>() {
            private final Label content = new Label();
            private final HBox container = new HBox();

            {
                content.setWrapText(true);
                content.setMaxWidth(INITIAL_WIDTH * 0.8);
                container.setPadding(new Insets(5));
                container.getChildren().add(content);
            }

            @Override
            protected void updateItem(Message message, boolean empty) {
                super.updateItem(message, empty);
                if (empty || message == null) {
                    setGraphic(null);
                } else {
                    content.setText(message.text());
                    container.getStyleClass().setAll(message.sender().styleClass);
                    setGraphic(container);
                }
            }
        };
    }

    private Node createSendIcon() {
        SVGPath icon = new SVGPath();
        icon.setContent("M 0 0 L 10 5 L 0 10 Z");
        icon.setFill(Color.web("#4A90E2"));
        return icon;
    }

    private void setupDialogCloseHandler() {
        // Add hidden close button type
        ButtonType closeButtonType = new ButtonType("", ButtonBar.ButtonData.CANCEL_CLOSE);
        chatbotDialog.getDialogPane().getButtonTypes().add(closeButtonType);

        // Get reference to the invisible close button
        Node closeButton = chatbotDialog.getDialogPane().lookupButton(closeButtonType);

        // Hide the button completely
        closeButton.setVisible(false);
        closeButton.setManaged(false);

        // Handle all close scenarios
        chatbotDialog.setOnCloseRequest(e -> {
            closeChatbot();
            e.consume();
        });
    }
    private void sendMessage() {
        String userInput = inputField.getText().trim();
        if (userInput.isEmpty()) return;

        addMessage(new Message(userInput, Sender.USER));
        inputField.clear();
        disableInput(true);

        Message loadingMessage = new Message("...", Sender.BOT_LOADING);
        addMessage(loadingMessage);

        executorService.execute(createChatTask(userInput, loadingMessage));
    }

    private Task<Message> createChatTask(String userInput, Message loadingMessage) {
        return new Task<>() {
            @Override
            protected Message call() throws Exception {
                String response = callOpenAI(userInput);
                return new Message(response, Sender.BOT);
            }

            @Override
            protected void succeeded() {
                replaceMessage(loadingMessage, getValue());
                disableInput(false);
            }

            @Override
            protected void failed() {
                replaceMessage(loadingMessage, new Message("Sorry, I couldn't process your request.", Sender.BOT_ERROR));
                disableInput(false);
            }
        };
    }

    private void replaceMessage(Message oldMessage, Message newMessage) {
        int index = chatListView.getItems().indexOf(oldMessage);
        if (index >= 0) {
            chatListView.getItems().set(index, newMessage);
            chatListView.scrollTo(index);
        }
    }

    private void disableInput(boolean disable) {
        inputField.setDisable(disable);
        sendButton.setDisable(disable);
    }

    private void animateEntrance() {
        ScaleTransition scale = new ScaleTransition(Duration.millis(1000), chatbotDialog.getDialogPane());
        scale.setFromX(0.9);
        scale.setFromY(0.9);
        scale.setToX(1);
        scale.setToY(1);

        FadeTransition fade = new FadeTransition(Duration.millis(1000), chatbotDialog.getDialogPane());
        fade.setFromValue(0);
        fade.setToValue(1);

        ParallelTransition animation = new ParallelTransition(scale, fade);
        animation.play();
    }

    private void closeChatbot() {
        if (chatbotDialog != null) {
            animateExit(() -> {
                chatbotDialog.close();
                chatbotDialog = null;
                executorService.shutdownNow();
            });
        }
    }


    private void animateExit(Runnable onFinished) {
        ScaleTransition scale = new ScaleTransition(Duration.millis(200), chatbotDialog.getDialogPane());
        scale.setToX(0.9);
        scale.setToY(0.9);

        FadeTransition fade = new FadeTransition(Duration.millis(200), chatbotDialog.getDialogPane());
        fade.setToValue(0);

        ParallelTransition animation = new ParallelTransition(scale, fade);
        animation.setOnFinished(e -> onFinished.run());
        animation.play();
    }







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


}
