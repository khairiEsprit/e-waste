package com.example.ewaste.Controllers;
import com.example.ewaste.Entities.capteur;
import com.example.ewaste.Entities.etat;
import com.example.ewaste.Entities.poubelle;
import com.example.ewaste.Repository.PoubelleRepository;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.util.Date;
public class Ajouter_poubelle_controller  {

    //Interface ajouter Poubelle:
    // Injection des champs FXML
    @FXML private TextField idCentreField;
    @FXML private TextField adresseField;
    @FXML private TextField hauteurTotaleField;
    @FXML private Label niveauRemplissageLabel;
    @FXML private DatePicker dateInstallationPicker;
    @FXML private ComboBox<etat> etatComboBox;
    @FXML private Button ajouterBtn;
    @FXML private StackPane confirmationPanel;
    @FXML private Label confirmationMessage;


    private ListePoubelleController mainController;

    public void setMainController(ListePoubelleController listePoubelleController) {
        this.mainController = listePoubelleController;
    }
    // Instance du service
    private PoubelleRepository pr = new PoubelleRepository();
    private Ajouter_poubelle_controller parentController;

    public void setParentController(Ajouter_poubelle_controller parentController) {
        this.parentController = parentController;
    }
    @FXML
    public void initialize() {
        etatComboBox.getItems().setAll(etat.values());
    }


    private float dernierNiveau = 0; // Stocker le dernier niveau de remplissage

    @FXML
    private void ajouterPoubelle(ActionEvent event) {
        try {
            if (champsValides()) {
                poubelle nouvellePoubelle = creerPoubelle();
                pr.ajouter(nouvellePoubelle);

                // Initialiser le capteur avec une précision de 1 cm et une portée maximale de 150 cm
                capteur capteur = new capteur(0, 0, 0.0f, new Timestamp(System.currentTimeMillis()), 150.0f, 1.0f);
                // Simuler la mesure de distance avec variation progressive
                float distanceMesuree = capteur.simulerDistanceMesuree(nouvellePoubelle.getHauteurTotale(), dernierNiveau);

                // Mettre à jour le dernier niveau de remplissage
                dernierNiveau = distanceMesuree;

                // Mettre à jour le niveau de remplissage de la poubelle
                nouvellePoubelle.mettreAJourNiveauRemplissage(distanceMesuree);

                // Afficher le niveau de remplissage
                niveauRemplissageLabel.setText(nouvellePoubelle.getNiveau() + "%");

                // Vérifier le seuil critique
                nouvellePoubelle.verifierSeuilCritique();

                reinitialiserFormulaire();
                afficherConfirmation();
            }
        } catch (Exception e) {
            afficherAlerte("Erreur", e.getMessage(), Alert.AlertType.ERROR);
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
    private void fermerConfirmation(ActionEvent event) { // Doit être javafx.event.ActionEvent
        confirmationPanel.setVisible(false);
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
        if (hauteurTotaleField.getText().isEmpty()) {
            afficherAlerte("Erreur", "La hauteur totale est obligatoire", Alert.AlertType.ERROR); // Modifié
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
            int hauteurTotale = Integer.parseInt(hauteurTotaleField.getText());
            if (hauteurTotale <= 0) { // Modification ici
                afficherAlerte("Erreur", "La hauteur doit être supérieure à 0 cm", Alert.AlertType.ERROR); // Modifié
                return false;
            }
        } catch (NumberFormatException e) {
            afficherAlerte("Erreur", "La hauteur doit être un nombre valide", Alert.AlertType.ERROR); // Modifié
            return false;
        }

        return true;
    }

    private poubelle creerPoubelle() {
        return new poubelle(
                Integer.parseInt(idCentreField.getText()),   // id_centre
                adresseField.getText(),                      // adresse
                0,                                           // niveau (initialize to 0%)
                etatComboBox.getValue(),                     // etat
                Date.from(dateInstallationPicker.getValue()  // date_installation
                        .atStartOfDay(ZoneId.systemDefault())
                        .toInstant()),
                Integer.parseInt(hauteurTotaleField.getText()) // hauteurTotale
        );
    }

    private void reinitialiserFormulaire() {
        // Réinitialiser tous les champs
        idCentreField.clear();
        adresseField.clear();
        hauteurTotaleField.clear();
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
