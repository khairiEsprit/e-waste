package com.example.ewaste;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.Enumeration;

/**
 * A simple test class to diagnose resource loading issues
 */
public class ResourceTest extends Application {

    @Override
    public void start(Stage stage) {
        // Create a text area to display the results
        TextArea textArea = new TextArea();
        textArea.setEditable(false);
        textArea.setPrefHeight(400);
        textArea.setPrefWidth(600);
        
        // Create a label for the title
        Label titleLabel = new Label("Resource Loading Test");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        // Create a VBox to hold the components
        VBox root = new VBox(10, titleLabel, textArea);
        root.setPadding(new javafx.geometry.Insets(10));
        
        // Create the scene
        Scene scene = new Scene(root, 650, 450);
        
        // Set the stage properties
        stage.setScene(scene);
        stage.setTitle("E-Waste Resource Test");
        stage.show();
        
        // Run the tests
        StringBuilder results = new StringBuilder();
        
        // Test 1: Check if we can load a resource using getResource
        results.append("Test 1: Loading resource using getResource\n");
        results.append("----------------------------------------\n");
        
        String[] resourcePaths = {
            "/com.example.ewaste/views/Avis.fxml",
            "/com/example/ewaste/views/Avis.fxml",
            "views/Avis.fxml",
            "/views/Avis.fxml",
            "com.example.ewaste/views/Avis.fxml",
            "com/example/ewaste/views/Avis.fxml"
        };
        
        for (String path : resourcePaths) {
            URL resource = getClass().getResource(path);
            results.append("Path: ").append(path).append("\n");
            results.append("Result: ").append(resource != null ? "FOUND" : "NOT FOUND").append("\n");
            if (resource != null) {
                results.append("Full URL: ").append(resource.toString()).append("\n");
            }
            results.append("\n");
        }
        
        // Test 2: List all resources in the classpath
        results.append("Test 2: Listing resources in classpath\n");
        results.append("------------------------------------\n");
        
        try {
            Enumeration<URL> resources = getClass().getClassLoader().getResources("");
            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                results.append("Resource: ").append(url).append("\n");
                
                // If it's a file URL, list its contents
                if (url.getProtocol().equals("file")) {
                    File file = new File(url.toURI());
                    if (file.isDirectory()) {
                        results.append("Contents of ").append(file.getPath()).append(":\n");
                        File[] files = file.listFiles();
                        if (files != null) {
                            for (File f : files) {
                                results.append("  ").append(f.getName()).append("\n");
                            }
                        } else {
                            results.append("  (empty or not accessible)\n");
                        }
                    }
                }
                results.append("\n");
            }
        } catch (Exception e) {
            results.append("Error listing resources: ").append(e.getMessage()).append("\n");
        }
        
        // Test 3: Check the system properties
        results.append("Test 3: System Properties\n");
        results.append("------------------------\n");
        results.append("java.class.path: ").append(System.getProperty("java.class.path")).append("\n");
        results.append("user.dir: ").append(System.getProperty("user.dir")).append("\n");
        
        // Set the results in the text area
        textArea.setText(results.toString());
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
