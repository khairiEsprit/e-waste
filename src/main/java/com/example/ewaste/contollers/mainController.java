package com.example.ewaste.contollers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class mainController {
    @FXML
    private StackPane contentArea;

    public void showDemande(ActionEvent actionEvent) throws IOException {
        loadContent("/com/example/ewaste/views/FormDemande.fxml");
    }

    public void showReclamation(ActionEvent actionEvent) {
    }
    private void loadContent(String fxmlPath) throws IOException {
        Parent content = FXMLLoader.load(getClass().getResource(fxmlPath));
        contentArea.getChildren().setAll(content);
    }

    //Cette méthode est appelée lorsqu'on clique sur le bouton "Consulter"
    public void show(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ewaste/views/listDemandesUser.fxml"));
        Parent root = loader.load();

        // Create a new stage (window)
        Stage stage = new Stage();
        stage.setTitle("Demande List");
        stage.setScene(new Scene(root, 800, 700));
        stage.show();
    }
}
