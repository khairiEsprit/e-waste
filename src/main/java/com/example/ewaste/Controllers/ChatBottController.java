package com.example.ewaste.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import com.example.ewaste.Utils.HuggingFaceChatService;

public class ChatBottController {
    @FXML private TextArea chatArea;
    @FXML private TextField userInput;

    @FXML
    private void sendMessage() {
        String userMessage = userInput.getText().trim();
        if (!userMessage.isEmpty()) {
            chatArea.appendText("👤 Toi: " + userMessage + "\n");

            new Thread(() -> {
                try {
                    String botResponse = HuggingFaceChatService.sendMessage(userMessage);
                    chatArea.appendText("🤖 Bot: " + botResponse + "\n\n");
                } catch (Exception e) {
                    chatArea.appendText("❌ Erreur: Impossible de contacter l'API Hugging Face.\n");
                    e.printStackTrace(); // Affiche l'erreur dans la console
                }
            }).start();

            userInput.clear();
        }
    }
}
