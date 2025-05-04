package com.example.ewaste;

import com.example.ewaste.Config.GoogleConfig;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {

//
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("views/mainLoginSignUp.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(scene);
        stage.setResizable(true);

        // Add a way to close the application with Escape key
        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case ESCAPE:
                    stage.close();
                    break;
                default:
                    break;
            }
        });

        stage.show();
    }

    public static void main(String[] args) {
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