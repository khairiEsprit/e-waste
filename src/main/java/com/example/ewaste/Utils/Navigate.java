package com.example.ewaste.Utils;

import com.example.ewaste.Main;
import io.github.palexdev.materialfx.controls.MFXProgressSpinner;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class Navigate {

    private static final int width = 1200;
    private static final int height = 700;
    public static void navigate(Button button, String fxmlPath, Stage stage) {
        button.setDisable(true); // Prevent multiple clicks

        // Create a progress indicator
        MFXProgressSpinner progressIndicator = new MFXProgressSpinner();
        progressIndicator.setPrefSize(70, 70);
        progressIndicator.setStyle("-fx-progress-color: #0C162C;");

        // Container for the progress indicator (centers it)
        VBox container = new VBox(progressIndicator);
        container.setAlignment(Pos.CENTER);

        // Prepare fade out transition for the current scene
        Parent currentRoot = stage.getScene().getRoot();
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.5), currentRoot);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        // Load the new FXML file
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(fxmlPath));
        Parent newRoot;
        try {
            newRoot = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
            button.setDisable(false); // Re-enable the button in case of failure
            return;
        }

        // Prepare fade in transition for the new scene
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.5), newRoot);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        // Timeline to switch the scene after fade out
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0.5), e -> {
                    stage.setScene(new Scene(newRoot, width, height));
                    fadeIn.play();
                    button.setDisable(false); // Re-enable the button after navigation
                })
        );

        // Start fade out, then run timeline after finishing
        fadeOut.setOnFinished(e -> timeline.play());
        fadeOut.play();

        // Set the progress indicator scene immediately
        stage.setScene(new Scene(container, width, height));
    }
}
