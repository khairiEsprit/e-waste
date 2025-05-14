package com.example.ewaste.Controllers;

import com.example.ewaste.Entities.capteurp;
import com.example.ewaste.Entities.etat;
import com.example.ewaste.Entities.poubelle;
import com.example.ewaste.Repository.CapteurpRepository;
import com.example.ewaste.Repository.PoubelleRepository;
import com.example.ewaste.Repository.CapteurRepository;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class Ajouter_poubelle_controller {

    @FXML private ComboBox<String> centreComboBox;
    @FXML private TextField adresseField;
    @FXML private TextField hauteurTotaleField;
    @FXML private Label niveauRemplissageLabel;
    @FXML private DatePicker dateInstallationPicker;
    @FXML private ComboBox<etat> etatComboBox;
    @FXML private Button ajouterBtn;
    @FXML private StackPane confirmationPanel;
    @FXML private Label confirmationMessage;

    private ListePoubelleController mainController;
    private PoubelleRepository pr = new PoubelleRepository();
    private CapteurRepository cr = new CapteurRepository();
    private CapteurpRepository cp = new CapteurpRepository();
    private Map<String, Integer> centresMap = new HashMap<>();

    public void setMainController(ListePoubelleController listePoubelleController) {
        this.mainController = listePoubelleController;
    }

    @FXML
    public void initialize() {
        etatComboBox.getItems().setAll(etat.values());
        etatComboBox.setValue(etat.FONCTIONNEL);
        chargerCentres();
    }

    private void chargerCentres() {
        try {
            Map<String, Integer> centres = pr.recupererCentres();
            centresMap.clear();
            centresMap.putAll(centres);
            centreComboBox.getItems().setAll(centresMap.keySet());
        } catch (SQLException e) {
            afficherAlerte("Erreur", "Impossible de charger les centres : " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void ajouterPoubelle(ActionEvent event) {
        if (!champsValides()) {
            return;
        }

        try {
            String nomCentre = centreComboBox.getValue();
            int idCentre = centresMap.get(nomCentre);
            String adresse = adresseField.getText();
            int hauteurTotale = Integer.parseInt(hauteurTotaleField.getText());
            Date dateInstallation = Date.valueOf(dateInstallationPicker.getValue());
            etat etat = etatComboBox.getValue();
            double porteeMaximale = cr.getPorteeMaximale();
            if (hauteurTotale > porteeMaximale) {
                afficherAlerte("Erreur", "La hauteur totale ne doit pas dépasser la portée maximale du capteur (" + porteeMaximale + " cm).", Alert.AlertType.ERROR);
                return;
            }            poubelle p = new poubelle();
            p.setId_centre(idCentre);
            p.setAdresse(adresse);
            p.setNiveau(0);
            p.setEtat(etat);
            p.setDate_installation(dateInstallation);
            p.setHauteurTotale(hauteurTotale);
            p.setLatitude(0.0);
            p.setLongitude(0.0);
            p.setRevenu_genere(0.0);
            pr.ajouter(p);

            int idPoubelle = pr.recupererDernierIdPoubelle();
            capteurp cp = new capteurp(idPoubelle, 0.0, new Timestamp(System.currentTimeMillis()));

            // Utiliser le repository pour ajouter le capteur
            CapteurpRepository capteurpRepo = new CapteurpRepository();
            capteurpRepo.ajouter(cp);

            afficherConfirmation();
            reinitialiserFormulaire();
        } catch (Exception e) {
            e.printStackTrace();
            afficherAlerte("Erreur", "Erreur lors de l'ajout de la poubelle : " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void afficherConfirmation() {
        confirmationPanel.setVisible(true);
        String adresse = adresseField.getText();
        String message = String.format("Poubelle ajoutée avec succès !\nLa poubelle située à %s\n"
                + "a été enregistrée dans le centre correspondant.", adresse);
        confirmationMessage.setText(message);
    }

    @FXML
    private void fermerConfirmation(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    private boolean champsValides() {
        if (centreComboBox.getValue() == null) {
            afficherAlerte("Erreur", "Veuillez sélectionner un centre.", Alert.AlertType.ERROR);
            return false;
        }
        if (adresseField.getText().isEmpty()) {
            afficherAlerte("Erreur", "L'adresse est obligatoire.", Alert.AlertType.ERROR);
            return false;
        }
        if (hauteurTotaleField.getText().isEmpty()) {
            afficherAlerte("Erreur", "La hauteur totale est obligatoire.", Alert.AlertType.ERROR);
            return false;
        }
        if (dateInstallationPicker.getValue() == null) {
            afficherAlerte("Erreur", "La date d'installation est obligatoire.", Alert.AlertType.ERROR);
            return false;
        }

        try {
            int hauteurTotale = Integer.parseInt(hauteurTotaleField.getText());
            if (hauteurTotale <= 0) {
                afficherAlerte("Erreur", "La hauteur doit être supérieure à 0 cm.", Alert.AlertType.ERROR);
                return false;
            }
        } catch (NumberFormatException e) {
            afficherAlerte("Erreur", "La hauteur doit être un nombre valide.", Alert.AlertType.ERROR);
            return false;
        }

        return true;
    }

    @FXML
    private void reinitialiserFormulaire() {
        centreComboBox.getSelectionModel().clearSelection();
        adresseField.clear();
        hauteurTotaleField.clear();
        dateInstallationPicker.setValue(null);
        etatComboBox.setValue(etat.FONCTIONNEL);
    }

    private void afficherAlerte(String titre, String message, Alert.AlertType type) {
        Alert alerte = new Alert(type);
        alerte.setTitle(titre);
        alerte.setHeaderText(null);
        alerte.setContentText(message);
        alerte.showAndWait();
    }
}