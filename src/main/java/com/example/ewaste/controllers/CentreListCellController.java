package com.example.ewaste.controllers;

import com.example.ewaste.entities.Centre;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class CentreListCellController extends ListCell<Centre> {

    @Override
    protected void updateItem(Centre centre, boolean empty) {
        super.updateItem(centre, empty);

        if (empty || centre == null) {
            setGraphic(null);
            setText(null);
        } else {
            // Création des labels pour les informations du centre
            Label nomLabel = new Label("🏢 Nom: " + centre.getNom());
            Label idLabel = new Label("ID: " + centre.getId());
            Label longitudeLabel = new Label("📍 Longitude: " + centre.getLongitude());
            Label latitudeLabel = new Label("📍 Latitude: " + centre.getLatitude());
            Label telephoneLabel = new Label("📞 Téléphone: " + centre.getTelephone());
            Label emailLabel = new Label("✉️ Email: " + centre.getEmail());

            // Appliquer des styles CSS pour améliorer l'affichage
            nomLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #00693e;");
            idLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #888;");
            longitudeLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #444;");
            latitudeLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #444;");
            telephoneLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");
            emailLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");

            // Création du WebView pour afficher la carte Leaflet
            WebView webView = new WebView();
            webView.setPrefSize(300, 200); // Taille de la carte
            WebEngine webEngine = webView.getEngine();

            // Code HTML pour afficher la carte avec Leaflet.js
            String mapHtml = "<html>\n" +
                    "<head>\n" +
                    "    <meta charset='utf-8' />\n" +
                    "    <meta name='viewport' content='width=device-width, initial-scale=1.0'>\n" +
                    "    <link rel='stylesheet' href='https://unpkg.com/leaflet/dist/leaflet.css' />\n" +
                    "    <script src='https://unpkg.com/leaflet/dist/leaflet.js'></script>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "    <div id='map' style='width: 100%; height: 200px;'></div>\n" +
                    "    <script>\n" +
                    "        var map = L.map('map').setView([" + centre.getLatitude() + ", " + centre.getLongitude() + "], 13);\n" +
                    "        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {\n" +
                    "            attribution: '© OpenStreetMap contributors'\n" +
                    "        }).addTo(map);\n" +
                    "        L.marker([" + centre.getLatitude() + ", " + centre.getLongitude() + "]).addTo(map)\n" +
                    "            .bindPopup('<b>" + centre.getNom() + "</b><br>Latitude: " + centre.getLatitude() + "<br>Longitude: " + centre.getLongitude() + "');\n" +
                    "    </script>\n" +
                    "</body>\n" +
                    "</html>";

            // Charger la carte dans le WebView
            webEngine.loadContent(mapHtml);

            // Organiser les labels dans une VBox
            VBox vbox = new VBox(nomLabel, idLabel, latitudeLabel, longitudeLabel, telephoneLabel, emailLabel, webView);
            vbox.setSpacing(5); // Espacement entre les éléments
            vbox.setPadding(new Insets(10, 10, 10, 10)); // Espacement interne

            // Ajouter un HBox pour le conteneur global de la cellule
            HBox cellContent = new HBox(vbox);
            cellContent.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 10px; "
                    + "-fx-border-radius: 10px; -fx-border-color: #00693e; -fx-border-width: 2px;");
            cellContent.setPadding(new Insets(10, 10, 10, 10)); // Espacement de la cellule

            // Définir le contenu graphique de la cellule
            setGraphic(cellContent);
        }
    }
}
