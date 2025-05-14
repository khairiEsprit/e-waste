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
            System.out.println("Centres récupérés: " + centres);

            if (centres.isEmpty()) {
                // Si aucun centre n'est trouvé, afficher un message d'erreur
                afficherAlerte("Attention", "Aucun centre n'a été trouvé dans la base de données. Veuillez d'abord créer un centre.", Alert.AlertType.WARNING);
            }

            centresMap.clear();
            centresMap.putAll(centres);
            centreComboBox.getItems().setAll(centresMap.keySet());

            // Sélectionner automatiquement le premier centre s'il y en a un
            if (!centresMap.isEmpty()) {
                centreComboBox.getSelectionModel().selectFirst();
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
            System.out.println("Centre sélectionné: " + nomCentre);

            Integer idCentre = 0; // Valeur par défaut si aucun centre n'est sélectionné

            // Si un centre est sélectionné, récupérer son ID
            if (nomCentre != null && !nomCentre.isEmpty()) {
                idCentre = centresMap.get(nomCentre);
                System.out.println("ID du centre: " + idCentre);

                if (idCentre == null) {
                    // Si le centre sélectionné n'a pas d'ID valide, afficher un avertissement mais continuer
                    afficherAlerte("Information", "Centre invalide. Un centre par défaut sera créé et utilisé automatiquement.", Alert.AlertType.INFORMATION);
                    idCentre = 0;
                }
            } else if (centresMap.isEmpty()) {
                // Si aucun centre n'existe, afficher un avertissement mais continuer
                afficherAlerte("Information", "Aucun centre n'existe. Un centre par défaut sera créé et utilisé automatiquement.", Alert.AlertType.INFORMATION);
            }

            String adresse = adresseField.getText();
            int hauteurTotale = Integer.parseInt(hauteurTotaleField.getText());
            Date dateInstallation = Date.valueOf(dateInstallationPicker.getValue());
            etat etat = etatComboBox.getValue();
            double porteeMaximale = cr.getPorteeMaximale();
            System.out.println("Portée maximale du capteur: " + porteeMaximale);

            if (hauteurTotale > porteeMaximale) {
                afficherAlerte("Erreur", "La hauteur totale ne doit pas dépasser la portée maximale du capteur (" + porteeMaximale + " cm).", Alert.AlertType.ERROR);
                return;
            }

            poubelle p = new poubelle();
            p.setId_centre(idCentre);
            p.setAdresse(adresse);
            p.setNiveau(0);
            p.setEtat(etat);
            p.setDate_installation(dateInstallation);
            p.setHauteurTotale(hauteurTotale);
            p.setLatitude(0.0);
            p.setLongitude(0.0);
            p.setRevenu_genere(0.0);

            System.out.println("Tentative d'ajout de la poubelle: " + p);
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
        // Utiliser une alerte standard au lieu du panel de confirmation
        String adresse = adresseField.getText();
        String message = String.format("Poubelle ajoutée avec succès !\nLa poubelle située à %s\n"
                + "a été enregistrée dans le centre correspondant.", adresse);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText("Poubelle ajoutée");
        alert.setContentText(message);
        alert.showAndWait();

        // Fermer la fenêtre après confirmation
        Node source = ajouterBtn;
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void fermerConfirmation(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    private boolean champsValides() {
        // Le centre est maintenant optionnel, donc on ne vérifie plus s'il est sélectionné

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