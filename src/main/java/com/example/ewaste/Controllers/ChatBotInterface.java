package com.example.ewaste.Controllers;
// ChatbotInterface.java
import com.example.ewaste.Entities.Message;
import com.example.ewaste.Entities.Sender;
import com.example.ewaste.Main;
import com.example.ewaste.Utils.DotenvConfig;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Using simple string manipulation instead of JSON libraries
import java.util.HashMap;
import java.util.Map;


public class ChatBotInterface {

    // Sample bin data for testing
    String binData = "Bin ID: 1, Fill Level: 75%, Status: Active, Location: Ariana, Tunisia\n" +
                     "Bin ID: 2, Fill Level: 45%, Status: Active, Location: Tunis, Tunisia\n" +
                     "Bin ID: 3, Fill Level: 90%, Status: Full, Location: Sousse, Tunisia";
    private static final String CSS_FILE = "styles/chat-style.css";
    private static final double INITIAL_WIDTH = 450;
    private static final double INITIAL_HEIGHT = 450;
    // Get API key from DotenvConfig
    String apiKey = DotenvConfig.get("OPENAI_API_KEY", "dummy-openai-api-key");
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
                    content.setText(message.getContent());
                    container.getStyleClass().setAll(getSenderStyleClass(message.getSender()));
                    setGraphic(container);
                }
            }

            private String getSenderStyleClass(Sender sender) {
                switch (sender) {
                    case USER:
                        return "user-message";
                    case BOT:
                        return "bot-message";
                    case BOT_LOADING:
                        return "bot-loading";
                    case BOT_ERROR:
                        return "bot-error";
                    default:
                        return "bot-message";
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

        // Use simple string building for JSON payload
        String jsonPayload = "{"
            + "\"model\": \"gpt-4o-mini\","
            + "\"messages\": ["
            + "  {"
            + "    \"role\": \"system\","
            + "    \"content\": " + escapeJsonString(systemPrompt)
            + "  },"
            + "  {"
            + "    \"role\": \"user\","
            + "    \"content\": " + escapeJsonString(userInput)
            + "  }"
            + "]"
            + "}";

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

        // Parse the response using simple string manipulation
        String responseBody = response.body();

        // Extract content from the response
        String content = extractContentFromResponse(responseBody);
        if (content != null) {
            return content.trim();
        }
        return "No response from AI.";
    }


    /**
     * Escapes a string for use in JSON
     * @param input The string to escape
     * @return The escaped string with quotes
     */
    private String escapeJsonString(String input) {
        if (input == null) {
            return "null";
        }

        StringBuilder sb = new StringBuilder("\"");
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            switch (c) {
                case '\\': sb.append("\\\\"); break;
                case '\"': sb.append("\\\""); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default: sb.append(c);
            }
        }
        sb.append("\"");
        return sb.toString();
    }

    /**
     * Extracts the content from an OpenAI API response
     * @param jsonResponse The JSON response from OpenAI
     * @return The extracted content or null if not found
     */
    private String extractContentFromResponse(String jsonResponse) {
        try {
            // Find the content field in the response
            int contentIndex = jsonResponse.indexOf("\"content\":");
            if (contentIndex == -1) {
                return null;
            }

            // Move past "content":
            contentIndex += 10;

            // Find the opening quote
            int startQuote = jsonResponse.indexOf("\"", contentIndex);
            if (startQuote == -1) {
                return null;
            }

            // Find the closing quote (accounting for escaped quotes)
            int endQuote = startQuote + 1;
            boolean escaped = false;
            while (endQuote < jsonResponse.length()) {
                char c = jsonResponse.charAt(endQuote);
                if (c == '\\') {
                    escaped = !escaped;
                } else if (c == '"' && !escaped) {
                    break;
                } else {
                    escaped = false;
                }
                endQuote++;
            }

            if (endQuote >= jsonResponse.length()) {
                return null;
            }

            // Extract the content
            String content = jsonResponse.substring(startQuote + 1, endQuote);

            // Unescape JSON escapes
            return content.replace("\\\"", "\"")
                         .replace("\\n", "\n")
                         .replace("\\r", "\r")
                         .replace("\\t", "\t")
                         .replace("\\\\", "\\");
        } catch (Exception e) {
            System.err.println("Error parsing OpenAI response: " + e.getMessage());
            return null;
        }
    }
}