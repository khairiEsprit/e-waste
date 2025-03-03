package com.example.ewaste.utils;

import javafx.scene.control.Alert;

public class AlertUtil {
    public static void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);    // Crée une nouvelle alerte avec le type donné (ERREUR, INFO, AVERTISSEMENT)
        alert.setTitle(title);     // Définit le titre de la fenêtre
        alert.setHeaderText(null);    // Supprime le texte d'en-tête (optionnel)
        alert.setContentText(message);   // Définit le message à afficher
        alert.showAndWait();    // Affiche l'alerte et attend que l'utilisateur la ferme
    }
}
//au lieu de recréer une Alert à chaque fois, on appelle simplement AlertUtil.showAlert().