package com.example.ewaste.controllers;

import com.example.ewaste.entities.Contrat;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ContratListCellController extends ListCell<Contrat> {

    @Override
    protected void updateItem(Contrat contrat, boolean empty) {
        super.updateItem(contrat, empty);

        if (empty || contrat == null) {
            setGraphic(null);
            setText(null);
        } else {
            // Créer des labels pour chaque attribut du Contrat
            Label idLabel = new Label("🆔 ID Contrat: " + contrat.getId());
            Label centreLabel = new Label("🏢 ID Centre: " + contrat.getIdCentre());
            Label employeLabel = new Label("👤 ID Employé: " + contrat.getIdEmploye());
            Label dateDebutLabel = new Label("📅 Début: " + contrat.getDateDebut());
            Label dateFinLabel = new Label("📅 Fin: " + contrat.getDateFin());

            // Appliquer des styles CSS pour améliorer l'affichage
            idLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #888;");
            centreLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");
            employeLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");
            dateDebutLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");
            dateFinLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");

            // Organiser les labels dans une VBox
            VBox vbox = new VBox(idLabel, centreLabel, employeLabel, dateDebutLabel, dateFinLabel);
            vbox.setSpacing(5); // Espacement entre les éléments
            vbox.setPadding(new Insets(10, 10, 10, 10)); // Espacement interne

            // Ajouter un HBox pour le conteneur global de la cellule
            HBox cellContent = new HBox(vbox);
            // Appliquer une couleur de fond et une bordure
            cellContent.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 10px; "
                    + "-fx-border-radius: 10px; -fx-border-color: #00693e; -fx-border-width: 2px;");
            cellContent.setPadding(new Insets(5, 5, 5, 5)); // Espacement de la cellule

            // Définir le contenu graphique de la cellule
            setGraphic(cellContent);
        }
    }
}
