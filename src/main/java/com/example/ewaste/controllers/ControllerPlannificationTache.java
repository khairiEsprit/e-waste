package com.example.ewaste.controllers;

import com.example.ewaste.entities.PlanificationTache;
import com.example.ewaste.repository.ServicePlanificationTache;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;

public class ControllerPlannificationTache {

    @FXML
    private Button btnValider, btnModifier, btnSupprimer;

    @FXML
    private TextField fieldIdTache, fieldPriorite;

    @FXML
    private Label labelIdTache;

    @FXML
    private DatePicker datePicker;

    @FXML
    private ComboBox<String> comboPriorite;


    private int idTache;
    private final ServicePlanificationTache servicePlanification = new ServicePlanificationTache();

    public void initData(int idTache) {
        this.idTache = idTache;
        labelIdTache.setText("Tâche ID : " + idTache);
        fieldIdTache.setText(String.valueOf(idTache));

        try {
            PlanificationTache planification = servicePlanification.getByIdTache(idTache);
            if (planification != null) {
                fieldPriorite.setText(planification.getPriorite());
                if (planification.getDate_limite() != null) {
                    datePicker.setValue(planification.getDate_limite().toLocalDate());
                } else {
                    datePicker.setValue(null);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de récupérer la planification existante.");
        }
    }



    @FXML
    private void ajouterPlanification() {
        if (servicePlanificationExiste(idTache)) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une planification existe déjà pour cette tâche.");
            return;
        }

        try {
            String priorite = fieldPriorite.getText();
            LocalDate dateLimite = datePicker.getValue();
            if (priorite.isEmpty() || dateLimite == null) {
                showAlert(Alert.AlertType.WARNING, "Champs vides", "Veuillez remplir tous les champs.");
                return;
            }

            PlanificationTache planification = new PlanificationTache(0, idTache, priorite, java.sql.Date.valueOf(dateLimite));
            servicePlanification.ajouter(planification);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Planification ajoutée avec succès !");
            fermerFenetre();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ajouter la planification.");
        }
    }

    @FXML
    private void modifierPlanification() {
        try {
            PlanificationTache planificationExistante = servicePlanification.getByIdTache(idTache);
            if (planificationExistante == null) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Aucune planification existante pour cette tâche.");
                return;
            }

            String priorite = fieldPriorite.getText();
            LocalDate dateLimite = datePicker.getValue();
            if (priorite.isEmpty() || dateLimite == null) {
                showAlert(Alert.AlertType.WARNING, "Champs vides", "Veuillez remplir tous les champs.");
                return;
            }

            PlanificationTache planification = new PlanificationTache(
                    planificationExistante.getId(), // Utilisation de l'ID existant
                    idTache,
                    priorite,
                    java.sql.Date.valueOf(dateLimite)
            );

            servicePlanification.modifier(planification);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Planification modifiée avec succès !");
            fermerFenetre();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de modifier la planification.");
        }
    }



    @FXML
    private void supprimerPlanification() {
        try {
            PlanificationTache planification = servicePlanification.getByIdTache(idTache);
            if (planification == null) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Aucune planification existante pour cette tâche.");
                return;
            }

            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Confirmation");
            confirmation.setHeaderText("Supprimer la planification");
            confirmation.setContentText("Êtes-vous sûr de vouloir supprimer cette planification ?");

            Optional<ButtonType> result = confirmation.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                servicePlanification.supprimer(planification.getId()); // Utilisation de l'ID réel
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Planification supprimée avec succès !");
                fermerFenetre();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de supprimer la planification.");
        }
    }


    private boolean servicePlanificationExiste(int idTache) {
        try {
            return servicePlanification.afficher(0).stream().anyMatch(p -> p.getId_tache() == idTache);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void fermerFenetre() {
        Stage stage = (Stage) btnValider.getScene().getWindow();
        stage.close();
    }
}
