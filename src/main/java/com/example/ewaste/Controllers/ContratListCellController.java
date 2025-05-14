package com.example.ewaste.Controllers;

import com.example.ewaste.Entities.Contrat;
import com.example.ewaste.Repository.ContratRepository;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.io.File;
import java.sql.SQLException;
import java.util.List;

public class ContratListCellController extends ListCell<Contrat> {

    private ContratRepository contratRepository = new ContratRepository() {
        @Override
        public List<Contrat> afficher(int id_centre) throws SQLException {
            return List.of();
        }
    };

    @Override
    protected void updateItem(Contrat contrat, boolean empty) {
        super.updateItem(contrat, empty);

        if (empty || contrat == null) {
            setGraphic(null);
            setText(null);
        } else {
            try {
                // 🔹 Récupérer les noms du centre et de l'employé
                String centreNom = contratRepository.getCentreNameById(contrat.getCentreId());
                String employeNom = contratRepository.getEmployeNameById(contrat.getEmployeId());
                String employePrenom = contratRepository.getEmployePrenameById(contrat.getEmployeId());

                // 🔹 Création des labels
                Label idLabel = new Label("🆔 ID Contrat: " + contrat.getId());
                Label centreLabel = new Label("🏢 Centre: " + (centreNom != null ? centreNom : "Inconnu"));
                Label employeLabel = new Label("👤 Employé: " + (employePrenom != null ? employePrenom : "") + " " + (employeNom != null ? employeNom : ""));
                Label dateDebutLabel = new Label("📅 Début: " + contrat.getDateDebut());
                Label dateFinLabel = new Label("📅 Fin: " + contrat.getDateFin());

                // 🔹 Style des labels
                idLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #888;");
                centreLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");
                employeLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");
                dateDebutLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");
                dateFinLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");

                // 🔹 Vérification et chargement de la signature
                ImageView signatureImageView = new ImageView();
                Label signatureLabel = new Label("✍️ Signature");

                String signaturePath = contrat.getSignaturePath();
                if (signaturePath != null && !signaturePath.isEmpty()) {
                    File file = new File(signaturePath);
                    if (file.exists()) {
                        Image signatureImage = new Image(file.toURI().toString());
                        signatureImageView.setImage(signatureImage);
                        signatureImageView.setFitWidth(80);
                        signatureImageView.setFitHeight(50);
                    }
                }

                // 🔹 Espacement flexible entre le texte et l'image
                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                // 🔹 Organisation des éléments
                VBox vbox = new VBox(idLabel, centreLabel, employeLabel, dateDebutLabel, dateFinLabel);
                vbox.setSpacing(5);
                vbox.setPadding(new Insets(10));

                VBox signatureBox = new VBox(signatureImageView, signatureLabel);
                signatureBox.setSpacing(5);
                signatureBox.setPadding(new Insets(10));

                HBox cellContent = new HBox(vbox, spacer, signatureBox);
                cellContent.setSpacing(15);
                cellContent.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 10px; " +
                        "-fx-border-radius: 10px; -fx-border-color: #00693e; -fx-border-width: 2px;");
                cellContent.setPadding(new Insets(5));

                // 🔹 Définir le contenu graphique
                setGraphic(cellContent);

            } catch (SQLException e) {
                e.printStackTrace();
                setGraphic(null);
                setText("❌ Erreur lors du chargement des données");
            }
        }
    }
}