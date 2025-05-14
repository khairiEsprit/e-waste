package com.example.ewaste.Utils;

import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class FloatingChatbotButton {

    private Button chatbotButton;
    public FloatingChatbotButton(Runnable action) {
        // Create Chatbot Floating Icon Button
        ImageView chatbotIcon = new ImageView(new Image("https://cdn-icons-png.flaticon.com/512/4712/4712034.png"));
        chatbotIcon.setFitWidth(50);
        chatbotIcon.setFitHeight(50);

        chatbotButton = new Button();
        chatbotButton.setGraphic(chatbotIcon);
        chatbotButton.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
        chatbotButton.toFront();

        setupButtonAnimations(chatbotButton);
        chatbotButton.setOnAction(e -> action.run());
    }

    public Button getButton() {
        return chatbotButton;
    }

    private void setupButtonAnimations(Button chatbotButton) {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), chatbotButton);
        chatbotButton.setOnMouseEntered(e -> playScaleAnimation(scaleTransition, 1.2));
        chatbotButton.setOnMouseExited(e -> playScaleAnimation(scaleTransition, 1.0));

        // Bounce animation for button
        TranslateTransition bounceAnimation = new TranslateTransition(Duration.millis(500), chatbotButton);
        bounceAnimation.setFromY(10);
        bounceAnimation.setToY(0);
        bounceAnimation.setCycleCount(2);
        bounceAnimation.setAutoReverse(true);
        bounceAnimation.play();

        // Continuous movement for button
        TranslateTransition moveUpDown = new TranslateTransition(Duration.millis(1000), chatbotButton);
        moveUpDown.setFromY(0);
        moveUpDown.setToY(20);
        moveUpDown.setCycleCount(TranslateTransition.INDEFINITE);
        moveUpDown.setAutoReverse(true);
        moveUpDown.play();
    }

    private void playScaleAnimation(ScaleTransition scaleTransition, double scaleFactor) {
        scaleTransition.setToX(scaleFactor);
        scaleTransition.setToY(scaleFactor);
        scaleTransition.play();
    }
}
