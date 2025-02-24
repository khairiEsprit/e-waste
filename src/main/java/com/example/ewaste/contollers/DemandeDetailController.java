package com.example.ewaste.contollers;

import com.example.ewaste.entities.Demande;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class DemandeDetailController {
    @FXML
    private Label lblIdUtilisateur;
    @FXML
    private Label lblIdCentre;
    @FXML
    private Label lblAdresse;
    @FXML
    private Label lblEmailUtilisateur;
    @FXML
    private Label lblMessage;
    @FXML
    private Label lblType;

    // Method to set Demande details
    public void setDemandeDetails(Demande demande) {
        lblIdUtilisateur.setText("ID Utilisateur: " + demande.getIdUtilisateur());
        lblIdCentre.setText("ID Centre: " + demande.getIdCentre());
        lblAdresse.setText("Adresse: " + demande.getAdresse());
        lblEmailUtilisateur.setText("Email: " + demande.getEmailUtilisateur());
        lblMessage.setText("Message: " + demande.getMessage());
        lblType.setText("Type: " + demande.getType());
    }
}
