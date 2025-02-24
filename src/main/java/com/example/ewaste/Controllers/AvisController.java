package com.example.ewaste.Controllers;

import com.example.ewaste.Entities.Avis;
import com.example.ewaste.Repository.AvisRepository;
import com.example.ewaste.Utils.DataBaseConn;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class AvisController {

    @FXML
    private TextField nameField; // Champ pour le nom

    @FXML
    private TextArea descriptionField; // Champ pour la description

    @FXML
    private ToggleButton star1, star2, star3, star4, star5; // Boutons pour la note

    @FXML
    private Button submitButton; // Bouton Envoyer

    private final AvisRepository avisRepository;

    public AvisController() {
        // Initialiser le repository avec la connexion à la base de données
        Connection conn = DataBaseConn.getInstance().getConnection();
        this.avisRepository = new AvisRepository(conn);
    }

    @FXML
    public void handleSubmit() {
        // Récupérer les données du formulaire
        String name = nameField.getText();
        String description = descriptionField.getText();
        int rating = getRatingFromStars();

        // Valider les données du formulaire
        if (name.isEmpty() || description.isEmpty() || rating == 0) {
            showAlert("Erreur", "Veuillez remplir tous les champs et sélectionner une note.");
            return;
        }

        // Créer un objet Avis avec les données du formulaire
        Avis avis = new Avis(0, name, description, rating);

        try {
            // Vérifier si un avis avec les mêmes données existe déjà
            if (avisRepository.exists(avis)) {
                showAlert("Erreur", "Un avis avec les mêmes coordonnées existe déjà.");
                return;
            }

            // Insérer l'avis dans la base de données
            boolean success = avisRepository.create(avis);

            // Afficher un message de succès ou d'erreur
            if (success) {
                showAlert("Succès", "Votre avis a été soumis avec succès.");
                redirectToListAvis(); // Rediriger vers l'interface ListAvis
            } else {
                showAlert("Erreur", "Une erreur s'est produite lors de la soumission de l'avis.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Une erreur de base de données s'est produite : " + e.getMessage());
        }
    }
    // Méthode pour obtenir la note à partir des boutons étoiles
    private int getRatingFromStars() {
        if (star1.isSelected()) return 1;
        if (star2.isSelected()) return 2;
        if (star3.isSelected()) return 3;
        if (star4.isSelected()) return 4;
        if (star5.isSelected()) return 5;
        return 0; // Aucune note sélectionnée
    }

    // Méthode pour afficher une alerte
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Méthode pour rediriger vers l'interface ListAvis
    private void redirectToListAvis() {
        try {
            // Charger l'interface ListAvis-view.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com.example.ewaste/views/ListAvis-view.fxml"));
            Parent root = loader.load();

            // Obtenir la scène actuelle
            Scene currentScene = submitButton.getScene();

            // Remplacer la scène actuelle par la nouvelle scène
            currentScene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger la page ListAvis.");
        }
    }
}