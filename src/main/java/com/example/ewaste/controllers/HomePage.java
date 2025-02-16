package com.example.ewaste.controllers;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HomePage extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/com/example/ewaste/views/liste_poubelle.fxml"));
        primaryStage.setTitle("Gestion des Poubelles");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
    

}
