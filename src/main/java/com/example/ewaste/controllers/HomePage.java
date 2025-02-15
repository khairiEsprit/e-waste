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
    public void start(Stage primaryStage) {
        //load les interface
        try{
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/ewaste/views/views/views/inscription.fxml"));
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.show();
        }
        catch (IOException e){
            System.out.println(e.getMessage());
        }

    }

    public void savePersonne(ActionEvent actionEvent) {
    }
}
