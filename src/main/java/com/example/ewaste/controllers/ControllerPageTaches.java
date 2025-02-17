package com.example.ewaste.controllers;

import com.example.ewaste.entities.Tache;
import com.example.ewaste.repository.TacheRepository;
import javafx.beans.property.SimpleStringProperty;
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

import javafx.scene.control.ComboBox;

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
    private TableColumn<Tache, String> colEmploye;

    @FXML
    private TableColumn<Tache, String> colMessage;

    @FXML
    private TableColumn<Tache, Float> colLatitude; // Changement ici pour latitude

    @FXML
    private TableColumn<Tache, Float> colLongitude; // Changement ici pour longitude

    @FXML
    private TableColumn<Tache, String> colEtat;

    @FXML
    private TextField fieldIdEmploye;

    @FXML
    private TextField fieldLatitude;

    @FXML
    private TextField fieldLongitude;

    @FXML
    private TextField fieldMessage;

    @FXML
    private TextField fieldEtat;

    @FXML
    private Button btnAjouter;

    @FXML
    private Button btnModifier;

    @FXML
    private ComboBox<String> comboEtat;

    @FXML
    private ComboBox<String> comboEmploye;

    private final TacheRepository serviceTache = new TacheRepository();

    private void loadTableData() throws SQLException {
        List<Tache> taches = serviceTache.afficher(1);
        ObservableList<Tache> data = FXCollections.observableArrayList(taches);
        tableTaches.setItems(data);
    }

    @FXML
    private void ouvrirPageAjout() {
        try {
            // Charger le fichier FXML de la page d'ajout
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ewaste/views/AjouterTache.fxml"));
            Parent root = loader.load();

            // Créer une nouvelle scène
            Stage stage = new Stage();
            stage.setTitle("Ajouter une tâche");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir la page d'ajout !");
        }
    }


    @FXML
    private void ajouterTache() {
        try {
            System.out.println("Début de la méthode ajouterTache");

            // Récupération des valeurs du formulaire
            //int idEmploye = Integer.parseInt(fieldIdEmploye.getText());
            String NomEmploye = comboEmploye.getValue() ;
            float latitude = Float.parseFloat(fieldLatitude.getText()); // Récupère la latitude
            float longitude = Float.parseFloat(fieldLongitude.getText()); // Récupère la longitude
            String message = fieldMessage.getText();
            String etat = comboEtat.getValue();
            System.out.println("ID: " + NomEmploye + " Latitude: " + latitude + " Longitude: " + longitude + " Message: " + message + " Etat: " + etat);

            // Vérifier que les champs ne sont pas vides
            if (latitude == 0 || longitude == 0 || message.isEmpty() || etat.isEmpty()) {
                showAlert("Erreur", "Tous les champs doivent être remplis !");
                return;
            }
            System.out.println("Champs valides, création de la tâche.");


            Integer idEmploye = serviceTache.getEmployeIdByName(NomEmploye);

            // Création d'un objet Tache
            Tache nouvelleTache = new Tache(1, idEmploye, latitude, longitude, message, etat); // Utilisation de latitude et longitude

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
            fieldLatitude.clear();
            fieldLongitude.clear();
            fieldEtat.clear();
            System.out.println("Champs réinitialisés.");

        } catch (NumberFormatException e) {
            showAlert("Erreur", "Les coordonnées doivent être des nombres valides !");
            System.out.println("Erreur NumberFormatException: " + e.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ajouter la tâche !");
            System.out.println("Erreur SQLException: " + e.getMessage());
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

            //int idEmploye = Integer.parseInt(fieldIdEmploye.getText());
            int employeID = serviceTache.getEmployeIdByName(comboEmploye.getValue());
            float latitude = Float.parseFloat(fieldLatitude.getText());
            float longitude = Float.parseFloat(fieldLongitude.getText()); // Récupère la longitude
            String message = fieldMessage.getText();
            String etat = comboEtat.getValue();

            if (latitude == 0 || longitude == 0 || message.isEmpty() || etat.isEmpty()) {
                showAlert("Erreur", "Tous les champs doivent être remplis !");
                return;
            }

            selectedTache.setId_employe(employeID);
            selectedTache.setLatitude(latitude); // Met à jour la latitude
            selectedTache.setLongitude(longitude); // Met à jour la longitude
            selectedTache.setMessage(message);
            selectedTache.setEtat(etat);

            serviceTache.modifier(selectedTache);  // Appel à la méthode de modification dans le service
            loadTableData();
            fieldIdEmploye.clear();
            fieldMessage.clear();
            fieldLatitude.clear();
            fieldLongitude.clear();
            fieldEtat.clear();
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Les coordonnées et l'ID Employé doivent être valides !");
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de modifier la tâche !");
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
        // Use a cell factory to fetch employee name by ID
        colEmploye.setCellValueFactory(param -> {
            String employeeName = null;
            try {
                employeeName = serviceTache.getEmployeNameById(param.getValue().getId_employe()); // Assuming your Tache class has an "id" property
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return new SimpleStringProperty(employeeName);
        });
        // Binding the employee name to the "colEmploye" column by calling the service metho

        colMessage.setCellValueFactory(new PropertyValueFactory<>("message"));
        colLatitude.setCellValueFactory(new PropertyValueFactory<>("latitude"));
        colLongitude.setCellValueFactory(new PropertyValueFactory<>("longitude"));
        colEtat.setCellValueFactory(new PropertyValueFactory<>("etat"));

        // Charger les données dans le tableau
        try {
            loadTableData();
            loadComboBoxData();
            loadComboEmploye();
        } catch (SQLException e) {
            e.printStackTrace(); // Gérer l'exception de manière plus propre si nécessaire
        }

        // Ajouter un écouteur pour détecter la sélection d'une ligne
        tableTaches.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                String employeNom = null;
                try {
                    employeNom = serviceTache.getEmployeNameById(newValue.getId_employe());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                comboEmploye.setValue(employeNom);

                fieldLatitude.setText(String.valueOf(newValue.getLatitude()));
                fieldLongitude.setText(String.valueOf(newValue.getLongitude()));
                fieldMessage.setText(newValue.getMessage());
                comboEtat.setValue(newValue.getEtat());
            }
        });
    }


    private void loadComboBoxData() {
        // Création de la liste des options statiques pour le ComboBox
        ObservableList<String> options = FXCollections.observableArrayList("En cours", "Accomplie");

        // Affectation de la liste d'options au ComboBox
        comboEtat.setItems(options);


    }


    private void loadComboEmploye() {
        try {
            // Récupération des employés depuis la table utilisateur
            List<String> employeNames = TacheRepository.getEmployeNames(); // Récupérer uniquement les noms

            comboEmploye.setItems(FXCollections.observableArrayList(employeNames));

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
