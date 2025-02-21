package com.example.ewaste.Controllers;

import com.example.ewaste.Entities.Demande;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import com.example.ewaste.Repository.DemandeRepository;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class DemandesController implements Initializable {
    @FXML
    private GridPane gridPane;

    private DemandeRepository demandeRepository = new DemandeRepository();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            List<Demande> demandes = demandeRepository.afficher();
            loadDemandes(demandes);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    private void loadDemandes(List<Demande> demandes) {
        int column = 0;
        int row = 0;

        for (Demande demande : demandes) {
            VBox card = createDemandeCard(demande);
            gridPane.add(card, column, row);

            column++;
            if (column == 2) { // 2 cards per row
                column = 0;
                row++;
            }
        }
    }

    private VBox createDemandeCard(Demande demande) {
        VBox card = new VBox();
        card.setSpacing(10);
        card.setStyle("-fx-background-color: #FFFFFF; -fx-padding: 15; -fx-border-radius: 10; -fx-background-radius: 10; -fx-border-color: #ddd;");
        card.setEffect(new DropShadow(10, Color.GRAY));



        Label typeLabel = new Label("Type: " + demande.getType());
        Label adresseLabel = new Label("Adresse: " + demande.getAdresse());
        Label emailLabel = new Label("Email: " + demande.getEmailUtilisateur());

        Button seeTreatmentButton = new Button("See Treatment");
        seeTreatmentButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 8 15;");
        seeTreatmentButton.setOnAction(event -> handleSeeTreatment(demande));

        HBox buttonContainer = new HBox(seeTreatmentButton);
        buttonContainer.setSpacing(10);

        card.getChildren().addAll(typeLabel, adresseLabel, emailLabel, buttonContainer);
        return card;
    }

    private void handleSeeTreatment(Demande demande) {
        System.out.println("Handling Treatment for Demande ID: " + demande.getId());
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ewaste/views/traitementCards.fxml"));
            Parent root = loader.load();


            TraitementCardsController controller = loader.getController();
            controller.setDemandeId(demande.getId());

            Stage stage = new Stage();
            stage.setTitle("Traitement Details");
            stage.setScene(new Scene(root, 600, 400));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
