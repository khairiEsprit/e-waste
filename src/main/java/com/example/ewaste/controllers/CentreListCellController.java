package com.example.ewaste.controllers;

import com.example.ewaste.entities.Centre;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class CentreListCellController extends ListCell<Centre> {

    @Override
    protected void updateItem(Centre centre, boolean empty) {
        super.updateItem(centre, empty);

        if (empty || centre == null) {
            setGraphic(null);
            setText(null);
        } else {
            // Créer des labels pour chaque attribut du Centre
            Label idLabel = new Label("ID: " + centre.getId());
            Label nomLabel = new Label("Nom: " + centre.getNom());
            Label adresseLabel = new Label("📍 Adresse: " + centre.getAdresse());
            Label telephoneLabel = new Label("📞 Téléphone: " + centre.getTelephone());
            Label emailLabel = new Label("✉️ Email: " + centre.getEmail());

            // Appliquer des styles CSS pour améliorer l'affichage
            idLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #888;");
            nomLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
            adresseLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");
            telephoneLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");
            emailLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");

            // Organiser les labels dans une VBox
            VBox vbox = new VBox(idLabel, nomLabel, adresseLabel, telephoneLabel, emailLabel);
            vbox.setSpacing(5); // Espacement entre les éléments
            vbox.setPadding(new Insets(10, 10, 10, 10)); // Espacement interne

            // Ajouter un HBox pour le conteneur global de la cellule
            HBox cellContent = new HBox(vbox);
            // Utilisation de la couleur #00693e pour les bordures et arrière-plan
            cellContent.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 10px; "
                    + "-fx-border-radius: 10px; -fx-border-color: #00693e; -fx-border-width: 2px;");
            cellContent.setPadding(new Insets(5, 5, 5, 5)); // Espacement de la cellule

            // Définir le contenu graphique de la cellule
            setGraphic(cellContent);
        }
    }
}
