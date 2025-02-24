package com.example.ewaste;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Charge le fichier FXML depuis le bon chemin
        Parent root = FXMLLoader.load(getClass().getResource("/com.example.ewaste/views/ListEvenement-view.fxml"));
        primaryStage.setTitle("Event Registration");
        primaryStage.setScene(new Scene(root, 800, 600)); // Taille de la fenêtre
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}