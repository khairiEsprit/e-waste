package com.example.ewaste.controllers;

import com.example.ewaste.entities.Tache;
import com.example.ewaste.repository.ServiceTache;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;


import javax.management.Descriptor;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class ControllerPageTaches implements Initializable {

    @FXML
    private TableView<Tache> tableTaches;

    @FXML
    private TableColumn<Tache, Integer> colId;

    @FXML
    private TableColumn<Tache, String> colMessage;

    @FXML
    private TableColumn<Tache, String> colAdresse;

    @FXML
    private TableColumn<Tache, String> colEtat;

    @FXML
    private TextField fieldIdEmploye;

    @FXML
    private TextField fieldAdresse;

    @FXML
    private TextField fieldMessage;

    @FXML
    private TextField fieldEtat;

    @FXML
    private Button btnAjouter;

    @FXML
    private Button btnModifier;

    private final ServiceTache serviceTache = new ServiceTache();



    private void loadTableData() throws SQLException {
        List<Tache> taches = serviceTache.afficher(1); // Remplace 1 par l'ID du centre voulu
        ObservableList<Tache> data = FXCollections.observableArrayList(taches);
        tableTaches.setItems(data);
    }

    @FXML
    private void ajouterTache() {
        try {
            System.out.println("Début de la méthode ajouterTache");

            // Récupération des valeurs du formulaire
            int idEmploye = Integer.parseInt(fieldIdEmploye.getText());
            String adressePoubelle = fieldAdresse.getText();
            String message = fieldMessage.getText();
            String etat = fieldEtat.getText().trim();
            System.out.println("ID: " + idEmploye + "Adresse" + adressePoubelle + "Message" + message + "Etat" + etat);
            // Vérifier que les champs ne sont pas vides
            if (adressePoubelle.isEmpty() || message.isEmpty() || etat.isEmpty()) {
                showAlert("Erreur", "Tous les champs doivent être remplis !");
                return;
            }
            System.out.println("Champs valides, création de la tâche.");

            // Création d'un objet Tache
            Tache nouvelleTache = new Tache(1, idEmploye, adressePoubelle, message, etat);

            System.out.println("Objet Tache créé : " + nouvelleTache);

            // Ajout dans la base de données
            serviceTache.ajouter(nouvelleTache);
            System.out.println("Tâche ajoutée à la base de données.");

            // Rafraîchir le tableau
            loadTableData();
            System.out.println("Tableau rafraîchi.");

            // Nettoyage des champs
            fieldIdEmploye.clear();
            fieldMessage.clear();
            fieldAdresse.clear();
            fieldEtat.clear();
            System.out.println("Champs réinitialisés.");

        } catch (NumberFormatException e) {
            showAlert("Erreur", "ID Employé doit être un nombre valide !");
            System.out.println("Erreur NumberFormatException: " + e.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ajouter la tâche !");
            System.out.println("Erreur SQLException: " + e.getMessage());
        }
    }

    @FXML
    private void supprimerTache() {
        // Récupérer la tâche sélectionnée dans le tableau
        Tache selectedTache = tableTaches.getSelectionModel().getSelectedItem();

        // Vérifier si une tâche est sélectionnée
        if (selectedTache == null) {
            showAlert("Erreur", "Veuillez sélectionner une tâche à supprimer.");
            return;
        }

        // Demander une confirmation à l'utilisateur
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmation");
        confirmationAlert.setHeaderText("Supprimer la tâche");
        confirmationAlert.setContentText("Êtes-vous sûr de vouloir supprimer cette tâche ?");
        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    // Supprimer la tâche de la base de données
                    serviceTache.supprimer(selectedTache.getId());

                    // Rafraîchir les données dans le tableau après suppression
                    loadTableData();
                    System.out.println("Tâche supprimée avec succès.");

                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert("Erreur", "Impossible de supprimer la tâche !");
                }
            }
        });
    }



    @FXML
    private void ouvrirPlannification() {
        // Récupérer la tâche sélectionnée
        Tache selectedTache = tableTaches.getSelectionModel().getSelectedItem();

        if (selectedTache == null) {
            showAlert("Erreur", "Veuillez sélectionner une tâche pour la planification.");
            return;
        }

        try {
            // Charger la page FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ewaste/views/PlannificationTache.fxml"));
            Parent root = loader.load();

            // Récupérer le contrôleur de la nouvelle fenêtre et passer l'ID de la tâche
            ControllerPlannificationTache controller = loader.getController();
            controller.initData(selectedTache.getId());

            // Afficher la nouvelle fenêtre
            Stage stage = new Stage();
            stage.setTitle("Planification de la tâche");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir la page de planification !");
        }
    }

    @FXML
    private void modifierTache() {
        try {
            Tache selectedTache = tableTaches.getSelectionModel().getSelectedItem();
            if (selectedTache == null) {
                showAlert("Erreur", "Veuillez sélectionner une tâche à modifier.");
                return;
            }

            int idEmploye = Integer.parseInt(fieldIdEmploye.getText());
            String adressePoubelle = fieldAdresse.getText();
            String message = fieldMessage.getText();
            String etat = fieldEtat.getText().trim();

            if (adressePoubelle.isEmpty() || message.isEmpty() || etat.isEmpty()) {
                showAlert("Erreur", "Tous les champs doivent être remplis !");
                return;
            }

            selectedTache.setId_employe(idEmploye);
            selectedTache.setAdresse_poubelle(adressePoubelle);
            selectedTache.setMessage(message);
            selectedTache.setEtat(etat);

            serviceTache.modifier(selectedTache);  // Appel à la méthode de modification dans le service
            loadTableData();
            fieldIdEmploye.clear();
            fieldMessage.clear();
            fieldAdresse.clear();
            fieldEtat.clear();
        } catch (NumberFormatException e) {
            showAlert("Erreur", "ID Employé doit être un nombre valide !");
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de modifier la tâche !");
        }
    }


    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Associer les colonnes aux attributs de l'objet Tache
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colMessage.setCellValueFactory(new PropertyValueFactory<>("message"));
        colAdresse.setCellValueFactory(new PropertyValueFactory<>("adresse_poubelle"));
        colEtat.setCellValueFactory(new PropertyValueFactory<>("etat"));

        // Charger les données dans le tableau
        try {
            loadTableData();
        } catch (SQLException e) {
            e.printStackTrace(); // Gérer l'exception de manière plus propre si nécessaire
        }

        // Ajouter un écouteur pour détecter la sélection d'une ligne
        tableTaches.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                // Remplir les champs avec les données de la tâche sélectionnée
                fieldIdEmploye.setText(String.valueOf(newValue.getId_employe()));
                fieldAdresse.setText(newValue.getAdresse_poubelle());
                fieldMessage.setText(newValue.getMessage());
                fieldEtat.setText(newValue.getEtat());
            }
        });
    }

}
