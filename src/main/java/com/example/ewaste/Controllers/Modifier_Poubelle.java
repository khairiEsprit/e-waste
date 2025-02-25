package com.example.ewaste.Controllers;

import com.example.ewaste.Entities.etat;
import com.example.ewaste.Entities.poubelle;
import com.example.ewaste.Repository.PoubelleRepository;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDate;

public class Modifier_Poubelle {

    @FXML
    private TextField idCentreField;

    @FXML
    private TextField adresseField;

    @FXML
    private TextField hauteurTotaleField; // Ajouté

    @FXML
    private Slider niveauSlider;

    @FXML
    private ProgressBar niveauProgressBar;

    @FXML
    private ComboBox<etat> etatComboBox;

    @FXML
    private DatePicker dateInstallationPicker;

    private poubelle selectedPoubelle;
    private PoubelleRepository service = new PoubelleRepository();

    public void setSelectedPoubelle(poubelle poubelle) {
        this.selectedPoubelle = poubelle;
        idCentreField.setText(String.valueOf(poubelle.getId_centre()));
        adresseField.setText(poubelle.getAdresse());
        hauteurTotaleField.setText(String.valueOf(poubelle.getHauteurTotale())); // Initialiser hauteurTotale
        niveauSlider.setValue(poubelle.getNiveau());
        niveauProgressBar.setProgress(poubelle.getNiveau() / 100.0);
        etatComboBox.getItems().setAll(etat.values());
        etatComboBox.setValue(poubelle.getEtat());

        // Conversion de java.util.Date vers LocalDate
        java.util.Date utilDate = poubelle.getDate_installation();
        java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
        LocalDate localDate = sqlDate.toLocalDate();
        dateInstallationPicker.setValue(localDate);
        dateInstallationPicker.setDisable(true);
    }

    @FXML
    private void initialize() {
        niveauSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            niveauProgressBar.setProgress(newVal.doubleValue() / 100.0);
        });
    }

    @FXML
    private void handleSave() {
        try {
            // Validation des champs obligatoires
            if (idCentreField.getText().isEmpty() || adresseField.getText().isEmpty() || hauteurTotaleField.getText().isEmpty()) {
                showAlert("Champs manquants", "Veuillez remplir tous les champs obligatoires", Alert.AlertType.WARNING);
                return;
            }

            // Validation de la hauteur totale
            int hauteurTotale;
            try {
                hauteurTotale = Integer.parseInt(hauteurTotaleField.getText());
                if (hauteurTotale <= 0) {
                    showAlert("Erreur de validation", "La hauteur doit être supérieure à 0 cm", Alert.AlertType.ERROR);
                    return;
                }
            } catch (NumberFormatException e) {
                showAlert("Erreur de format", "La hauteur doit être un nombre valide", Alert.AlertType.ERROR);
                return;
            }

            // Vérification de la date
            LocalDate localDate = dateInstallationPicker.getValue();
            if (localDate == null) {
                showAlert("Erreur de date", "La date d'installation est obligatoire", Alert.AlertType.ERROR);
                return;
            }

            // Mise à jour de l'objet
            selectedPoubelle.setId_centre(Integer.parseInt(idCentreField.getText()));
            selectedPoubelle.setAdresse(adresseField.getText());
            selectedPoubelle.setNiveau((int) niveauSlider.getValue());
            selectedPoubelle.setEtat(etatComboBox.getValue());
            selectedPoubelle.setHauteurTotale(hauteurTotale); // Mise à jour hauteurTotale

            // Conversion de la date
            java.sql.Date sqlDate = java.sql.Date.valueOf(localDate);
            selectedPoubelle.setDate_installation(new java.util.Date(sqlDate.getTime()));

            // Appel au service
            service.modifier(selectedPoubelle);

            // Fermeture de la fenêtre
            Stage stage = (Stage) idCentreField.getScene().getWindow();
            stage.close();

            showAlert("Succès", "Modification effectuée avec succès", Alert.AlertType.INFORMATION);

        } catch (NumberFormatException e) {
            showAlert("Erreur de format", "L'ID du centre doit être un nombre valide", Alert.AlertType.ERROR);
        } catch (SQLException e) {
            showAlert("Erreur SQL", "Erreur lors de la mise à jour : " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleConfirm() {
        handleSave();
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) idCentreField.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}