package com.example.ewaste.contollers;


import com.example.ewaste.entities.Traitement;
import com.example.ewaste.repository.TraitementRepository;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;


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

        // Image for the treatment status
        ImageView imageView = new ImageView();
        try {
            String imagePath = traitement.getStatus().equalsIgnoreCase("Validé")
                    ? "/assets/valid-icon.png"
                    : "/assets/pending-icon.png";

            // ✅ Correct way to load the image from `resources/assets`
            Image image = new Image(getClass().getResourceAsStream(imagePath));

            if (image.isError()) {
                throw new Exception("Image not found: " + imagePath);
            }

            imageView.setImage(image);
        } catch (Exception e) {
            System.err.println("Erreur de chargement de l'image: " + e.getMessage());
        }

        imageView.setFitWidth(80);
        imageView.setFitHeight(80);
        imageView.setPreserveRatio(true);

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
        card.getChildren().addAll(imageView, statusLabel, dateLabel, commentLabel, closeButton);

        return card;
    }

    private void closeWindow() {
        gridPane.getScene().getWindow().hide();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
