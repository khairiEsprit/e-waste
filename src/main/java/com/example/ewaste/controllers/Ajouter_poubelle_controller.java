package com.example.ewaste.controllers;

import com.example.ewaste.Models.etat;
import com.example.ewaste.Models.poubelle;
import com.example.ewaste.repository.service_poubelle;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import java.sql.SQLException;
import java.time.ZoneId;
import java.util.Date;

public class Ajouter_poubelle_controller {

    // Injection des champs FXML
    @FXML private TextField idCentreField;
    @FXML private TextField adresseField;
    @FXML private TextField niveauField;
    @FXML private DatePicker dateInstallationPicker;
    @FXML private ComboBox<etat> etatComboBox;
    @FXML private Button ajouterBtn;

    // Instance du service
    private  service_poubelle poubelleService = new service_poubelle();

    @FXML
    public void initialize() {
        etatComboBox.getItems().setAll(etat.values());
    }

    @FXML
    private void ajouterPoubelle() {
        try {
            System.out.println("Bouton cliqué !"); // Debug

            if (champsValides()) {
                System.out.println("Validation OK"); // Debug

                poubelle nouvellePoubelle = creerPoubelle();
                poubelleService.ajouter(nouvellePoubelle);

                System.out.println("Insertion effectuée"); // Debug
                reinitialiserFormulaire();
                afficherAlerte("Succès", "Ajout réussi !", Alert.AlertType.INFORMATION);
            }
        } catch (Exception e) {
            System.err.println("Erreur : " + e.getMessage()); // Debug
            afficherAlerte("Erreur", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private boolean champsValides() {
        // Vérifier les champs vides
        if (idCentreField.getText().isEmpty()) {
            afficherAlerte("Erreur", "L'ID du centre est obligatoire", Alert.AlertType.ERROR);
            return false;
        }
        if (adresseField.getText().isEmpty()) {
            afficherAlerte("Erreur", "L'adresse est obligatoire", Alert.AlertType.ERROR);
            return false;
        }
        if (niveauField.getText().isEmpty()) {
            afficherAlerte("Erreur", "Le niveau est obligatoire", Alert.AlertType.ERROR);
            return false;
        }
        if (dateInstallationPicker.getValue() == null) {
            afficherAlerte("Erreur", "La date d'installation est obligatoire", Alert.AlertType.ERROR);
            return false;
        }
        if (etatComboBox.getValue() == null) {
            afficherAlerte("Erreur", "L'état est obligatoire", Alert.AlertType.ERROR);
            return false;
        }

        // Vérifier le format numérique
        try {
            Integer.parseInt(idCentreField.getText());
        } catch (NumberFormatException e) {
            afficherAlerte("Erreur", "L'ID du centre doit être un nombre valide", Alert.AlertType.ERROR);
            return false;
        }

        try {
            int niveau = Integer.parseInt(niveauField.getText());
            if (niveau < 0 || niveau > 100) {
                afficherAlerte("Erreur", "Le niveau doit être entre 0 et 100%", Alert.AlertType.ERROR);
                return false;
            }
        } catch (NumberFormatException e) {
            afficherAlerte("Erreur", "Le niveau doit être un nombre valide", Alert.AlertType.ERROR);
            return false;
        }

        return true;
    }

    private poubelle creerPoubelle() {
        return new poubelle(
                Integer.parseInt(idCentreField.getText()),
                adresseField.getText(),
                Integer.parseInt(niveauField.getText()),
                etatComboBox.getValue(),
                Date.from(dateInstallationPicker.getValue()
                                .atStartOfDay(ZoneId.systemDefault())
                                .toInstant()
                )
        );
    }

    private void reinitialiserFormulaire() {
        // Réinitialiser tous les champs
        idCentreField.clear();
        adresseField.clear();
        niveauField.clear();
        dateInstallationPicker.setValue(null);
        etatComboBox.getSelectionModel().clearSelection();

        // Remettre le focus sur le premier champ
        idCentreField.requestFocus();
    }

    private void afficherAlerte(String titre, String message, Alert.AlertType type) {
        Alert alerte = new Alert(type);
        alerte.setTitle(titre);
        alerte.setHeaderText(null);
        alerte.setContentText(message);
        alerte.showAndWait();
    }
}
