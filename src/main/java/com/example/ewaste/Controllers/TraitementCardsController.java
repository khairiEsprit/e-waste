package com.example.ewaste.Controllers;

import com.example.ewaste.Entities.Traitement;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import com.example.ewaste.Repository.TraitementRepository;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class TraitementCardsController implements Initializable {
    @FXML
    private GridPane gridPane;

    private TraitementRepository traitementRepository = new TraitementRepository();

    private int demandeId; // Will be set dynamically

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
        gridPane.getChildren().clear(); // Clear previous cards
        int column = 0;
        int row = 0;

        for (Traitement traitement : traitements) {
            VBox card = createTraitementCard(traitement);
            gridPane.add(card, column, row);

            column++;
            if (column == 2) { // 2 cards per row
                column = 0;
                row++;
            }
        }
    }

    private VBox createTraitementCard(Traitement traitement) {
        VBox card = new VBox();
        card.setSpacing(10);
        card.setStyle("-fx-background-color: #FFFFFF; -fx-padding: 15; -fx-border-radius: 10; -fx-background-radius: 10; -fx-border-color: #ddd;");
        card.setEffect(new DropShadow(10, Color.GRAY));

        Label statusLabel = new Label("Status: " + traitement.getStatus());
        statusLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Label dateLabel = new Label("Date: " + traitement.getDateTraitement());
        Label commentLabel = new Label("Comment: " + traitement.getCommentaire());

        Button closeButton = new Button("Close");
        closeButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-padding: 8 15;");
        closeButton.setOnAction(event -> closeWindow());

        HBox buttonContainer = new HBox(closeButton);
        buttonContainer.setSpacing(10);

        card.getChildren().addAll(statusLabel, dateLabel, commentLabel, buttonContainer);
        return card;
    }

    private void closeWindow() {
        gridPane.getScene().getWindow().hide();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
