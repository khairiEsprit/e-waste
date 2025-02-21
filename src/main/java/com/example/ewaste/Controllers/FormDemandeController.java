package com.example.ewaste.Controllers;

import com.example.ewaste.Entities.Demande;
import com.example.ewaste.Utils.Modals;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import com.example.ewaste.Repository.DemandeRepository;

import java.io.IOException;
import java.sql.SQLException;

public class FormDemandeController {
    public TextField adresseField;
    public TextField emailField;
    public TextField messageField;
    public ComboBox typeField;
    public Button saveButton;
    public Label adresseError;
    public Label emailError;
    public Label typeError;
    public Label messageError;
    private Demande demandeToEdit = null;
    private final DemandeRepository demandeRepository = new DemandeRepository();


public void handleSubmit(ActionEvent actionEvent) {
    String adresse = adresseField.getText().trim();
    String email = emailField.getText().trim();
    String message = messageField.getText().trim();
    String type = (String) typeField.getValue();

    // Hide error messages
    resetErrorMessages();

    boolean isValid = true;

    if (adresse.isEmpty()) {
        adresseError.setText("Veuillez saisir une adresse.");
        adresseError.setVisible(true);
        isValid = false;
    }

    if (email.isEmpty() || !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
        emailError.setText("Veuillez saisir un email valide.");
        emailError.setVisible(true);
        isValid = false;
    }

    if (message.isEmpty()) {
        messageError.setText("Le message ne peut pas être vide.");
        messageError.setVisible(true);
        isValid = false;
    }

    if (type == null) {
        typeError.setText("Veuillez sélectionner un type.");
        typeError.setVisible(true);
        isValid = false;
    }

    if (!isValid) return;

    try {
        if (demandeToEdit == null) {
            Demande newDemande = new Demande(adresse, email, message, type);
            demandeRepository.ajouter(newDemande);
            Modals.displaySuccess("Succès", "Demande ajoutée avec succès.");
        } else {
            demandeToEdit.setAdresse(adresse);
            demandeToEdit.setEmailUtilisateur(email);
            demandeToEdit.setMessage(message);
            demandeToEdit.setType(type);
            demandeRepository.modifier(demandeToEdit);
            Modals.displaySuccess("Succès", "Demande modifiée avec succès.");
        }

        ((Stage) saveButton.getScene().getWindow()).close();
        openDemandeView();

    } catch (SQLException e) {
        e.printStackTrace();
        Modals.displayError("Erreur", "Une erreur s'est produite lors de l'enregistrement.");
    }
}

    private void resetErrorMessages() {
        adresseError.setVisible(false);
        emailError.setVisible(false);
        messageError.setVisible(false);
        typeError.setVisible(false);
    }


    private void openDemandeView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ewaste/views/DemandeView.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Liste des Demandes");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            Modals.displayError("Erreur", "Impossible d'ouvrir la liste des demandes.");
        }
    }

    private void clearForm() {
        adresseField.clear();
        emailField.clear();
        messageField.clear();
        typeField.getSelectionModel().clearSelection();
    }

    public void setDemandeToEdit(Demande demande) {
        if (demande != null) {
            this.demandeToEdit = demande;
            adresseField.setText(demande.getAdresse());
            emailField.setText(demande.getEmailUtilisateur());
            messageField.setText(demande.getMessage());
            typeField.setValue(demande.getType());
            saveButton.setText("Modifier");
        }
    }
}
