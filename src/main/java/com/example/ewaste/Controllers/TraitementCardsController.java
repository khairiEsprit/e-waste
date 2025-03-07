package com.example.ewaste.Controllers;


import com.example.ewaste.Entities.Traitement;
import com.example.ewaste.Repository.TraitementRepository;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;


import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class TraitementCardsController implements Initializable {
    @FXML
    private GridPane gridPane;

    private TraitementRepository traitementRepository = new TraitementRepository();

    private int demandeId; // représente l'ID de la demande associée aux traitements.

    public void setDemandeId(int demandeId) {
        this.demandeId = demandeId;
        loadTraitements();
    }



    private void loadTraitements() {
        try {
            List<Traitement> traitements = traitementRepository.getTraitementByDemande(demandeId);
            displayTraitements(traitements);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void displayTraitements(List<Traitement> traitements) {
        gridPane.getChildren().clear(); // Effacer les anciennes cartes
        int column = 0;
        int row = 0;

        for (Traitement traitement : traitements) {
            VBox card = createTraitementCard(traitement);
            gridPane.add(card, column, row);

            column++;
            if (column == 2) { // 2 cartes par ligne
                column = 0;
                row++;
            }
        }
    }

    private VBox createTraitementCard(Traitement traitement) {

        VBox card = new VBox();
        card.getStyleClass().add("event-card"); // Applying the same style as Event
        card.setSpacing(10);


        // Ajout des informations du traitement
        Label statusLabel = new Label("Statut: " + traitement.getStatus());
        statusLabel.getStyleClass().add("event-title"); // Matching event title style

        Label dateLabel = new Label("Date: " + traitement.getDateTraitement());
        dateLabel.getStyleClass().add("event-info");

        Label commentLabel = new Label("Commentaire: " + traitement.getCommentaire());
        commentLabel.getStyleClass().add("event-description"); // Keeping consistency with event description

        // Action Button
        Button closeButton = new Button("Close");
        closeButton.getStyleClass().add("button");
        closeButton.setOnAction(event -> closeWindow());

        // Adding elements to the card
        card.getChildren().addAll(statusLabel, dateLabel, commentLabel, closeButton);

        return card;
    }

    private void closeWindow() {
        gridPane.getScene().getWindow().hide();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
