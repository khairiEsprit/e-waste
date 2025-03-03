package com.example.ewaste.contollers;

import com.example.ewaste.entities.Demande;
import com.example.ewaste.repository.DemandeRepository;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class ListeDemandeUserController implements Initializable {
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
        card.getStyleClass().add("event-card");
        card.setSpacing(10);



        // Demande Information
        //// Affichage du type, adresse, et email de l’utilisateur ayant fait la demande.
        Label typeLabel = new Label("Type: " + demande.getType());
        typeLabel.getStyleClass().add("event-title");

        Label adresseLabel = new Label("Adresse: " + demande.getAdresse());
        adresseLabel.getStyleClass().add("event-info");

        Label emailLabel = new Label("Email: " + demande.getEmailUtilisateur());
        emailLabel.getStyleClass().add("event-description");

        // Ce bouton permet d'afficher les détails du traitement d'une demande.
        Button seeTreatmentButton = new Button("See Treatment");
        seeTreatmentButton.getStyleClass().add("button");
        seeTreatmentButton.setOnAction(event -> handleSeeTreatment(demande));

        // Adding elements to the card
        card.getChildren().addAll(typeLabel, adresseLabel, emailLabel, seeTreatmentButton);
        return card;
    }
//Affichage des détails d’un traitement

    private void handleSeeTreatment(Demande demande) {
        System.out.println("Handling Treatment for Demande ID: " + demande.getId());
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ewaste/views/traitementCards.fxml"));
            Parent root = loader.load();


            TraitementCardsController controller = loader.getController();
            controller.setDemandeId(demande.getId());

            Stage stage = new Stage();
            stage.setTitle("Traitement Details");
            stage.setScene(new Scene(root, 800, 600));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
