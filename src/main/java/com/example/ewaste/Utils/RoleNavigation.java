package com.example.ewaste.Utils;

import com.example.ewaste.Main;
import com.example.ewaste.Entities.UserRole;
import io.github.palexdev.materialfx.controls.MFXProgressSpinner;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class RoleNavigation {
   static int width = 1200;
   static int height = 700;

    public static void navigateUser(Stage stage, UserRole role) throws IOException {
        // Create a progress indicator
        MFXProgressSpinner progressIndicator = new MFXProgressSpinner();
        progressIndicator.setPrefSize(70, 70);
        progressIndicator.setStyle("-fx-progress-color: #0C162C;");

        // Container for the progress indicator (centers it)
        VBox container = new VBox(progressIndicator);
        container.setAlignment(Pos.CENTER);

        // Prepare fade out transition from the current scene
        Parent currentRoot = stage.getScene().getRoot();
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.5), currentRoot);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        // Choose the FXML resource based on the role.
        // For example, if admin, load the admin view; else load a different view.
        String fxmlResource;
        if (role == UserRole.ADMIN) {
            fxmlResource = "views/Dashboard.fxml";
        } else if(role == UserRole.CITOYEN) {
            // Add more roles as needed; here is a default:
            fxmlResource = "views/UserAccount.fxml";
        }else {
            // Add more roles as needed; here is a default:
            fxmlResource = "views/EmployeeInterface.fxml";
        }

        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(fxmlResource));
        Parent newRoot = fxmlLoader.load();

        // Prepare fade in transition for the new scene
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.5), newRoot);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        // Timeline to switch the scene after fade out
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0.5), e -> {
                    // Replace the scene with the new root
                    stage.setScene(new Scene(newRoot, width, height));
                    fadeIn.play();
                })
        );

        // Start fade out, then run timeline after finishing
        fadeOut.setOnFinished(e -> timeline.play());
        fadeOut.play();

        // Set the progress indicator scene immediately
        stage.setScene(new Scene(container, width, height));
    }

}
