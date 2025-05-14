package com.example.ewaste.Controllers;

import com.example.ewaste.Entities.capteur;
import com.example.ewaste.Entities.etat;
import com.example.ewaste.Entities.poubelle;
import com.example.ewaste.Repository.CapteurRepository;
import com.example.ewaste.Repository.PoubelleRepository;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Modifier_Poubelle {

    @FXML
    private TextField adresseField;

    @FXML
    private TextField hauteurTotaleField;

    @FXML
    private TextField distanceMesureeField;

    @FXML
    private TextField porteeMaximaleField;

    @FXML
    private TextField precisionCapteurField;

    @FXML
    private ComboBox<String> etatComboBox;

    @FXML
    private DatePicker dateInstallationPicker;

    @FXML
    private ComboBox<String> centreComboBox; // ComboBox pour les centres

    private poubelle selectedPoubelle;
    private capteur selectedCapteur;

    private PoubelleRepository poubelleRepo = new PoubelleRepository();
    private CapteurRepository capteurRepo = new CapteurRepository();
    private Map<String, Integer> centresMap = new HashMap<>(); // Pour stocker les noms et IDs des centres

    @FXML
    public void initialize() {
        // Initialiser les valeurs de la ComboBox pour l'état
        etatComboBox.getItems().setAll(
                Arrays.stream(etat.values()) // Convertir l'énumération en stream
                        .map(Enum::toString) // Convertir chaque valeur en String
                        .collect(Collectors.toList()));

        // Charger les centres depuis la base de données
        chargerCentres();
    }

    // Méthode pour charger les centres depuis la base de données
    private void chargerCentres() {
        try {
            // Récupérer les centres depuis la base de données
            Map<String, Integer> centres = poubelleRepo.recupererCentres(); // Cette méthode doit retourner une Map<nomCentre, idCentre>
            centresMap.clear();
            centresMap.putAll(centres);

            // Ajouter les noms des centres à la ComboBox
            centreComboBox.getItems().setAll(centresMap.keySet());
        } catch (SQLException e) {
            afficherAlerte("Erreur", "Impossible de charger les centres : " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void afficherAlerte(String titre, String message, Alert.AlertType type) {
        Alert alerte = new Alert(type);
        alerte.setTitle(titre);
        alerte.setHeaderText(null);
        alerte.setContentText(message);
        alerte.showAndWait();
    }

    public void setSelectedPoubelle(poubelle poubelle) {
        this.selectedPoubelle = poubelle;
        loadPoubelleData();
    }

    private void loadPoubelleData() {
        if (selectedPoubelle != null) {
            // Sélectionner le centre correspondant dans la ComboBox
            for (Map.Entry<String, Integer> entry : centresMap.entrySet()) {
                if (entry.getValue() == selectedPoubelle.getId_centre()) {
                    centreComboBox.setValue(entry.getKey());
                    break;
                }
            }

            adresseField.setText(selectedPoubelle.getAdresse());
            hauteurTotaleField.setText(String.valueOf(selectedPoubelle.getHauteurTotale()));
            etatComboBox.setValue(selectedPoubelle.getEtat().toString());

            // Charger les données du capteur associé à la poubelle
            try {                selectedCapteur = capteurRepo.recuperer().stream()
                        .filter(c -> c.getPoubelle_id() == selectedPoubelle.getId())
                        .findFirst()
                        .orElse(null);

                if (selectedCapteur != null) {
                    distanceMesureeField.setText(String.valueOf(selectedCapteur.getDistance_mesuree()));
                    porteeMaximaleField.setText(String.valueOf(selectedCapteur.getPortee_maximale()));
                    precisionCapteurField.setText(String.valueOf(selectedCapteur.getPrecision_capteur()));
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Erreur", "Erreur lors de la récupération des données du capteur : " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }    @FXML
    private void handleConfirm() {
        if (selectedPoubelle != null) {
            try {
                // Valider les entrées
                if (centreComboBox.getValue() == null || adresseField.getText().isEmpty() || 
                    hauteurTotaleField.getText().isEmpty() || etatComboBox.getValue() == null) {
                    showAlert("Erreur de validation", "Veuillez remplir tous les champs obligatoires.", Alert.AlertType.ERROR);
                    return;
                }

                // Mettre à jour la poubelle
                String nomCentre = centreComboBox.getValue();
                int idCentre = centresMap.get(nomCentre);
                selectedPoubelle.setId_centre(idCentre);
                selectedPoubelle.setAdresse(adresseField.getText());
                selectedPoubelle.setHauteurTotale(Integer.parseInt(hauteurTotaleField.getText()));
                selectedPoubelle.setEtat(etat.valueOf(etatComboBox.getValue()));
                poubelleRepo.modifier(selectedPoubelle);

                // Mettre à jour le capteur si présent
                if (selectedCapteur != null) {
                    try {
                        selectedCapteur.setDistance_mesuree(Double.parseDouble(distanceMesureeField.getText()));
                        selectedCapteur.setPortee_maximale(Double.parseDouble(porteeMaximaleField.getText()));
                        selectedCapteur.setPrecision_capteur(Double.parseDouble(precisionCapteurField.getText()));
                        selectedCapteur.setDate_mesure(Timestamp.valueOf(LocalDateTime.now()));
                        capteurRepo.modifier(selectedCapteur);
                    } catch (NumberFormatException e) {
                        showAlert("Erreur de validation", "Les valeurs du capteur doivent être des nombres valides.", Alert.AlertType.ERROR);
                        return;
                    }
                }

                // Afficher un message de confirmation
                showAlert("Succès", "La poubelle et le capteur ont été modifiés avec succès.", Alert.AlertType.INFORMATION);
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Erreur", "Une erreur s'est produite lors de la modification : " + e.getMessage(), Alert.AlertType.ERROR);
            }
        } else {
            showAlert("Erreur", "Aucune poubelle ou capteur sélectionné.", Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}