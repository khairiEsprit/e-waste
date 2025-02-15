package com.example.ewaste;

import com.example.ewaste.entities.Centre;
import com.example.ewaste.entities.Contrat;
import com.example.ewaste.repository.CentreRepository;
import com.example.ewaste.repository.ContratRepository;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
       FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("views/Afficher_Centre.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 794,490 );
        stage.setScene(scene);
        stage.show();
    }}



