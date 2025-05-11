package com.example.ewaste;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * A simplified main class to test if JavaFX can run
 */
public class SimpleMain extends Application {
    @Override
    public void start(Stage stage) {
        try {
            // Create a simple UI
            Label label = new Label("E-Waste Management System");
            label.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
            
            Label infoLabel = new Label("If you can see this, JavaFX is working correctly!");
            infoLabel.setStyle("-fx-font-size: 14px;");
            
            VBox root = new VBox(20, label, infoLabel);
            root.setStyle("-fx-padding: 20px; -fx-alignment: center;");
            
            Scene scene = new Scene(root, 400, 300);
            stage.setScene(scene);
            stage.setTitle("E-Waste Management System - Test");
            stage.show();
            
        } catch (Exception e) {
            System.err.println("Error starting application: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        System.out.println("Starting E-Waste Management System test application...");
        launch(args);
    }
}
