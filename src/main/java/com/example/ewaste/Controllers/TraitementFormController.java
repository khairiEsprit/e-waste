package com.example.ewaste.Controllers;

import com.example.ewaste.Entities.Demande;
import com.example.ewaste.Entities.Traitement;
import com.example.ewaste.Repository.DemandeRepository;
import com.example.ewaste.Repository.TraitementRepository;
import com.example.ewaste.Utils.AlertUtil;
import com.example.ewaste.Utils.SendMail;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

public class TraitementFormController implements Initializable {
    @FXML
    private ComboBox<String> statusField;

    @FXML
    private WebView editorWebView;

    @FXML
    private Button saveButton;



    @FXML
    private Button cancelButton;

    @FXML
    private TextField idDemandeField;

    private final TraitementRepository traitementRepository = new TraitementRepository();
    private final DemandeRepository demandeRepository = new DemandeRepository();
    private int idDemande;
    private Traitement traitementToEdit = null;
    private WebEngine webEngine;

    public void setDemandeId(int idDemande) {
        this.idDemande = idDemande;
        if (idDemandeField != null) {
            idDemandeField.setEditable(false); // Ensure it's non-editable
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        webEngine = editorWebView.getEngine();
        URL editorUrl = getClass().getResource("/ckeditor/ckeditor.html");
        if (editorUrl != null) {
            webEngine.load(editorUrl.toExternalForm());
            webEngine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue == Worker.State.SUCCEEDED) {
                    System.out.println("CKEditor loaded successfully.");
                } else if (newValue == Worker.State.FAILED) {
                    System.err.println("Failed to load CKEditor.");
                }
            });
        } else {
            AlertUtil.showAlert("Error", "CKEditor could not be loaded.", Alert.AlertType.ERROR);
        }
    }
//Gestion du bouton "Enregistrer"
    public void handleSubmit(ActionEvent actionEvent) {
        try {
            String status = statusField.getValue();
            String commentaire = getEditorContent();

            if (status == null || status.isEmpty() || commentaire.isEmpty()) {
                AlertUtil.showAlert("Error", "Please fill all fields.", Alert.AlertType.ERROR);
                return;
            }

            if (traitementToEdit == null) {
                // Add new treatment
                Traitement newTraitement = new Traitement(idDemande, status, LocalDateTime.now(), commentaire);
                Demande demandeFromTraitement = demandeRepository.getDemandeById(idDemande);
                String emailContent = buildEmailContent(demandeFromTraitement, status, commentaire);
                SendMail.send(demandeFromTraitement.getEmailUtilisateur(), "Treatment Resolution", emailContent);
                traitementRepository.ajouter(newTraitement);
                AlertUtil.showAlert("Success", "Treatment added successfully.", Alert.AlertType.INFORMATION);
            } else {
                // Modify existing treatment
                traitementToEdit.setStatus(status);
                traitementToEdit.setCommentaire(commentaire);
                traitementToEdit.setDateTraitement(LocalDateTime.now());
                traitementRepository.modifier(traitementToEdit);
                AlertUtil.showAlert("Success", "Treatment modified successfully.", Alert.AlertType.INFORMATION);
            }

            closeForm();
            refreshTraitementTable();

        } catch (SQLException e) {
            AlertUtil.showAlert("Error", "An error occurred while saving.", Alert.AlertType.ERROR);
        }
    }
//Cette méthode génère le contenu de l'email à envoyer à l'utilisateur.
    private String buildEmailContent(Demande demande, String status, String commentaire) {
        StringBuilder emailContent = new StringBuilder();

        // Ajouter les informations de la demande
        emailContent.append("Détails de la demande :\n");
        emailContent.append("- ID de la demande : ").append(demande.getId()).append("\n");
        emailContent.append("- Nom de l'utilisateur : ").append(demande.getutilisateur_id()).append("\n");
        emailContent.append("- Email de l'utilisateur : ").append(demande.getEmailUtilisateur()).append("\n");
        emailContent.append("- Adresse : ").append(demande.getAdresse()).append("\n");
        emailContent.append("- Type : ").append(demande.getType()).append("\n");
        emailContent.append("- Adresse : ").append(demande.getAdresse()).append("\n");


        // Ajouter les informations du traitement
        emailContent.append("\nDétails du traitement :\n");
        emailContent.append("- Statut : ").append(status).append("\n");
        emailContent.append("- Commentaire : ").append(commentaire).append("\n");
        emailContent.append("- Date du traitement : ").append(LocalDateTime.now()).append("\n");

        return emailContent.toString();
    }


    private String getEditorContent() {
        try {
            return (String) webEngine.executeScript("CKEDITOR.instances.editor.getData();");
        } catch (Exception e) {
            System.err.println("Error retrieving CKEditor content: " + e.getMessage());
            return "";
        }
    }

    private void setEditorContent(String content) {
        webEngine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Worker.State.SUCCEEDED) {
                webEngine.executeScript("CKEDITOR.instances.editor.setData('" + content + "');");
            }
        });
    }

    public void handleCancel(ActionEvent actionEvent) {
        closeForm();
    }

    private void closeForm() {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }

    private void refreshTraitementTable() {
        TaitementByDemandeAdminController traitementController = TaitementByDemandeAdminController.getInstance();
        if (traitementController != null) {
            System.out.println("Rafraîchissement de la table des traitements pour la demande : " + idDemande);
            traitementController.loadTraitementByDemande(idDemande);
        } else {
            System.err.println("Le contrôleur TaitementByDemandeAdminController est null.");
        }
    }

    public void setTraitementToEdit(Traitement traitement) {
        if (traitement == null) {
            AlertUtil.showAlert("Error", "No treatment selected for modification.", Alert.AlertType.ERROR);
            return;
        }

        this.traitementToEdit = traitement;
        this.idDemande = traitement.getIdDemande();

        statusField.setValue(traitement.getStatus());
        setEditorContent(traitement.getCommentaire());
        saveButton.setText("Modify");
    }
}