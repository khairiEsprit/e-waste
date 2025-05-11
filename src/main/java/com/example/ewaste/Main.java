package com.example.ewaste;

import com.example.ewaste.Utils.DotenvConfig;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        try {
            // Try to load the dashboard view instead of Avis.fxml
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("views/ListEvenement-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            stage.setScene(scene);
            stage.setResizable(true);
            stage.setTitle("E-Waste Management System");

            // Add a way to close the application with Escape key
            scene.setOnKeyPressed(event -> {
                if (event.getCode() == javafx.scene.input.KeyCode.ESCAPE) {
                    stage.close();
                }
            });

            stage.show();
        } catch (Exception e) {
            System.err.println("Error loading view: " + e.getMessage());
            e.printStackTrace();

            // Fallback to a simple scene if FXML loading fails
            Scene fallbackScene = new Scene(new javafx.scene.layout.VBox(
                new javafx.scene.control.Label("Failed to load application view. Please check the logs.")
            ), 400, 300);
            stage.setScene(fallbackScene);
            stage.show();
        }
    }

    public static void main(String[] args) {
        // Initialize DotenvConfig to load environment variables
        try {
            // This will initialize the DotenvConfig and load the .env file
            System.out.println("API Key: " + DotenvConfig.get("APIKEY", "Not found"));
            System.out.println("Environment variables loaded successfully");
        } catch (Exception e) {
            System.err.println("Error loading environment variables: " + e.getMessage());
            // Continue execution even if environment variables fail to load
            // as we have fallback values
        }

        // Register a shutdown hook to ensure clean exit
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Application shutting down...");
            // Add any cleanup code here if needed
        }));

        launch();
    }
}
//
//public class Main extends Application {
//    public static void main(String[] args) {
//        // Validate environment variables on startup
//        try {
//            GoogleConfig.getClientId();
//            GoogleConfig.getClientSecret();
//        } catch (IllegalStateException e) {
//            System.err.println("Configuration error: " + e.getMessage());
//            System.exit(1);
//        }
//
//        launch(args);
//    }
//
//    @Override
//    public void start(Stage stage) throws Exception {
//        // Load the FXML file from resources
//        FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/Google.fxml"));
//        Parent root = loader.load();
//
//        // Set up the main scene
//        Scene scene = new Scene(root, 800, 600);
//
//        // Configure the primary stage
//        stage.setTitle("E-Waste Management System - Login");
//        stage.setScene(scene);
//        stage.setResizable(false);
//
//        // Show the window
//        stage.show();
//    }
//}


//
//public class com.example.ewaste.Main extends Application {
//
//    @Override
//    public void start(Stage stage) {
//        // Create a WebView and load the local HTML file
//        WebView webView = new WebView();
//        String url = getClass().getResource("views/maps.html").toExternalForm();
//        webView.getEngine().load(url);
//
//        // Create and set the scene
//        Scene scene = new Scene(webView, 800, 600);
//        stage.setScene(scene);
//        stage.setTitle("Mapbox in JavaFX");
//        stage.show();
//    }
//
//    public static void main(String[] args) {
//        launch(args);
//    }
//}

// This commented-out code has been refactored into the MapBox class
// to better organize the application architecture and avoid license issues.