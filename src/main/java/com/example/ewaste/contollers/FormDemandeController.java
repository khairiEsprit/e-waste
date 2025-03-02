package com.example.ewaste.contollers;


import com.example.ewaste.entities.Demande;
import com.example.ewaste.repository.DemandeRepository;
import com.example.ewaste.utils.AlertUtil;
import com.example.ewaste.utils.BadWordFilter;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;


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
            String filteredMessage= BadWordFilter.filterBadWords(message);
            if (demandeToEdit == null) {

                Demande newDemande = new Demande(adresse, email, filteredMessage, type);
                demandeRepository.ajouter(newDemande);
                showCustomAlert("Succès", "Demande Ajoutée avec succès !");
            } else {
                demandeToEdit.setAdresse(adresse);
                demandeToEdit.setEmailUtilisateur(email);
                demandeToEdit.setMessage(filteredMessage);
                demandeToEdit.setType(type);
                demandeRepository.modifier(demandeToEdit);
                showCustomAlert("Succès", "Demande modifiée avec succès !");
            }

            ((Stage) saveButton.getScene().getWindow()).close();
            openDemandeView();

        } catch (SQLException e) {
            e.printStackTrace();
            AlertUtil.showAlert("Erreur", "Une erreur s'est produite lors de l'enregistrement.", Alert.AlertType.ERROR);
        }
    }

    private void showCustomAlert(String title, String message) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ewaste/views/CustomAlert.fxml"));
            Parent root = loader.load();

            CustomAlertController controller = loader.getController();
            controller.setMessage(title);
            controller.setDetails(message);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ewaste/views/ListDemandesAdmin.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Liste des Demandes");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtil.showAlert("Erreur", "Impossible d'ouvrir la liste des demandes.", Alert.AlertType.ERROR);
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
