package com.example.ewaste.Controllers;

import com.example.ewaste.Entities.Participation;
import com.example.ewaste.Repository.ParticipationRepository;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class ParticipationController {

    // Champs du formulaire
    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField phoneField;
    @FXML
    private TextField cityField;
    @FXML
    private TextField countryField;
    @FXML
    private TextField zipCodeField;

    // Boutons
    @FXML
    private Button submitButton;
    @FXML
    private Button shareFacebookButton;
    @FXML
    private Button shareInstagramButton;

    // Référence au repository
    private final ParticipationRepository participationRepository = new ParticipationRepository();

    // Gestion de la soumission du formulaire
    @FXML
    public void handleSubmit() {
        // Récupérer les données du formulaire
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String email = emailField.getText();
        String phone = phoneField.getText();
        String city = cityField.getText();
        String country = countryField.getText();
        String zipCode = zipCodeField.getText();

        // Validation des champs obligatoires
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()) {
            showAlert("Erreur", "Veuillez remplir tous les champs obligatoires.");
            return;
        }

        // Vérifier si une participation avec le même e-mail existe déjà
        if (participationRepository.isParticipationExists(email)) {
            showAlert("Erreur", "Une participation avec cet e-mail existe déjà.");
            return;
        }

        // Créer un objet Participation avec 10 points gagnés par défaut
        Participation participation = new Participation(
                0,  // ID généré automatiquement par la base de données
                firstName,
                lastName,
                email,
                phone,
                city,
                country,
                zipCode,
                10  // Points gagnés par défaut
        );

        // Enregistrer la participation dans la base de données
        boolean success = participationRepository.create(participation);

        // Afficher un message de succès ou d'erreur
        if (success) {
            showAlert("Succès", "Votre participation a été enregistrée avec succès. Vous avez gagné 10 points !");

            // Vérifier si le citoyen a atteint 100 points
            int totalPoints = participationRepository.getTotalPointsByEmail(email);
            if (totalPoints >= 100) {
                showAlert("Félicitations !", "Vous avez atteint 100 points et gagné une remise de cadeau !");
                participationRepository.resetPoints(email); // Réinitialiser les points après la remise
            }

            clearForm();  // Vider le formulaire après soumission
            redirectToListParticipation();  // Rediriger vers la liste des participations
        }
    }

    // Partager sur Facebook
    @FXML
    private void shareOnFacebook() {
        // Récupérer les données du formulaire
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String email = emailField.getText();
        String city = cityField.getText();
        String country = countryField.getText();

        // Créer un message de partage
        String message = "Je viens de participer à un événement !\n\n" +
                "Nom : " + firstName + " " + lastName + "\n" +
                "E-mail : " + email + "\n" +
                "Ville : " + city + "\n" +
                "Pays : " + country + "\n\n" +
                "Rejoignez-moi !";

        // Encoder le message pour l'URL
        String encodedMessage;
        try {
            encodedMessage = URLEncoder.encode(message, StandardCharsets.UTF_8.toString());
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'encoder le message pour le partage.");
            return;
        }

        // Créer l'URL de partage Facebook
        String eventUrl = "https://www.votresite.com/evenement"; // Remplacez par l'URL de votre événement
        String facebookUrl = "https://www.facebook.com/sharer/sharer.php?u=" + URLEncoder.encode(eventUrl, StandardCharsets.UTF_8);

        // Ouvrir l'URL dans le navigateur
        openWebPage(facebookUrl);

        // Afficher le message dans une boîte de dialogue (optionnel)
        showAlert("Partager sur Facebook", "Copiez ce message et partagez-le sur Facebook :\n\n" + message);
    }

    // Partager sur Instagram
    @FXML
    private void shareOnInstagram() {
        // Rediriger vers Instagram (pas de partage direct possible)
        String instagramUrl = "https://www.instagram.com/";
        openWebPage(instagramUrl);
    }

    // Rediriger vers la liste des participations
    private void redirectToListParticipation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com.example.ewaste/views/ListParticipation-view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) submitButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger la liste des participations.");
        }
    }

    // Gérer le bouton Retour
    @FXML
    private void handleRetour() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com.example.ewaste/views/ListEvenement-view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) submitButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger la liste des événements.");
        }
    }

    // Ouvrir une page web dans le navigateur
    private void openWebPage(String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir la page web.");
        }
    }

    // Afficher une alerte
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Vider le formulaire
    private void clearForm() {
        firstNameField.clear();
        lastNameField.clear();
        emailField.clear();
        phoneField.clear();
        cityField.clear();
        countryField.clear();
        zipCodeField.clear();
    }
}