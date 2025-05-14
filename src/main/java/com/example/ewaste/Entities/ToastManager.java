package com.example.ewaste.Entities;

import javafx.animation.PauseTransition;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class ToastManager {
    public static void showToast(Stage ownerStage, String message, String styleClass) {
        // Créer le label pour le message
        Label toastLabel = new Label(message);
        toastLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        toastLabel.getStyleClass().add("toast");

        // Ajouter une icône
        Label iconLabel = new Label("✓"); // Icône de succès
        iconLabel.getStyleClass().add("toast-icon");

        // Créer un conteneur pour le message et l'icône
        HBox toastContent = new HBox(10, iconLabel, toastLabel);
        toastContent.setAlignment(Pos.CENTER_LEFT);
        toastContent.getStyleClass().add(styleClass); // Appliquer le style spécifique (ex: "toast-success")

        // Créer la scène et afficher la notification
        StackPane root = new StackPane(toastContent);
        root.setStyle("-fx-background-color: transparent;");
        root.setAlignment(Pos.BOTTOM_CENTER);

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);

        Stage toastStage = new Stage();
        toastStage.initOwner(ownerStage);
        toastStage.initStyle(StageStyle.TRANSPARENT);
        toastStage.setScene(scene);

        // Positionner et afficher la notification
        toastStage.setX(ownerStage.getX() + (ownerStage.getWidth() / 2) - 100);
        toastStage.setY(ownerStage.getY() + ownerStage.getHeight() - 100);
        toastStage.show();

        // Fermer la notification après 3 secondes
        PauseTransition delay = new PauseTransition(Duration.seconds(3));
        delay.setOnFinished(event -> toastStage.close());
        delay.play();
    }
}