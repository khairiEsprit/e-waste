package com.example.ewaste.controllers;

import com.example.ewaste.entities.Tache;
import com.example.ewaste.repository.TacheRepository;
import com.example.ewaste.repository.EmailRepository;
import com.example.ewaste.repository.TemperatureRepository;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import netscape.javascript.JSObject;

import java.io.IOException;
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
    private TableColumn<Tache, Float> colLatitude;

    @FXML
    private TableColumn<Tache, Float> colLongitude;

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

    @FXML
    private WebView Map;

    @FXML
    private Label labelTemperature; // Label pour afficher la température

    private WebEngine webEngine;

    // Variables pour stocker les coordonnées de la carte
    private double selectedLatitude = 36.8065; // Valeur par défaut (Tunis)
    private double selectedLongitude = 10.1815; // Valeur par défaut (Tunis)

    private final TacheRepository serviceTache = new TacheRepository();
    private final TemperatureRepository serviceTemperature = new TemperatureRepository(); // Service pour la température

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialisation des colonnes du tableau
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colEmploye.setCellValueFactory(param -> {
            String employeeName = null;
            try {
                employeeName = serviceTache.getEmployeNameById(param.getValue().getId_employe());
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return new SimpleStringProperty(employeeName);
        });
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
            e.printStackTrace();
        }

        // Initialisation du WebView et chargement de la carte Leaflet
        if (Map != null) {
            webEngine = Map.getEngine();
            URL mapUrl = getClass().getResource("/com/example/ewaste/views/map.html");
            if (mapUrl != null) {
                webEngine.load(mapUrl.toExternalForm());

                // Attendre que la page soit chargée pour exécuter du JavaScript
                webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
                    if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                        // Liaison entre Java et JavaScript
                        JSObject window = (JSObject) webEngine.executeScript("window");
                        window.setMember("javaApp", this);

                        // Définir les coordonnées par défaut
                        updateMap(selectedLatitude, selectedLongitude); // Coordonnées par défaut (Tunis)
                        updateTemperature(selectedLatitude, selectedLongitude); // Mettre à jour la température
                    }
                });
            } else {
                System.out.println("Fichier map.html non trouvé !");
            }
        }

        // Écouteur pour la sélection d'une ligne dans le tableau
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

                // Mettre à jour la carte avec les coordonnées de la tâche sélectionnée
                updateMap(newValue.getLatitude(), newValue.getLongitude());
                updateTemperature(newValue.getLatitude(), newValue.getLongitude()); // Mettre à jour la température
            }
        });
    }

    // Méthode pour mettre à jour la carte avec de nouvelles coordonnées
    public void updateMap(double latitude, double longitude) {
        if (webEngine != null) {
            String script = String.format("updateLocation(%f, %f);", latitude, longitude);
            webEngine.executeScript(script);
        }
    }

    // Méthode pour mettre à jour la température
    public void updateTemperature(double latitude, double longitude) {
        String temperature = serviceTemperature.getTemperature(latitude, longitude);
        labelTemperature.setText(temperature);
    }

    // Méthode appelée depuis JavaScript pour récupérer les coordonnées
    public void setCoordinates(double lat, double lon) {
        this.selectedLatitude = lat;
        this.selectedLongitude = lon;
        fieldLatitude.setText(String.valueOf(lat));
        fieldLongitude.setText(String.valueOf(lon));
        updateTemperature(lat, lon);
    }

    @FXML
    private void ajouterTache() {
        try {
            System.out.println("Début de la méthode ajouterTache");

            String nomEmploye = comboEmploye.getValue();
            float latitude = Float.parseFloat(fieldLatitude.getText());
            float longitude = Float.parseFloat(fieldLongitude.getText());
            String message = fieldMessage.getText();
            String etat = comboEtat.getValue();

            if (latitude == 0 || longitude == 0 || message.isEmpty() || etat.isEmpty()) {
                showAlert("Erreur", "Tous les champs doivent être remplis !");
                return;
            }

            Integer idEmploye = serviceTache.getEmployeIdByName(nomEmploye);

            // Création d'un objet Tache
            Tache nouvelleTache = new Tache(1, idEmploye, latitude, longitude, message, etat);

            // Ajout dans la base de données
            serviceTache.ajouter(nouvelleTache);

            // Envoyer une notification par e-mail
            String recipientEmail = serviceTache.getEmployeEmailById(idEmploye); // Implémentez cette méthode dans TacheRepository
            String subject = "[Tâche Affectée] Nouvelle Tâche Assignée";
            String emailMessage = "Bonjour " + nomEmploye + ",\n\n" +
                    "Nous vous informons qu'une nouvelle tâche vous a été assignée dans le cadre de vos responsabilités :\n\n" +
                    "Description de la tâche : " + message + "\n\n" +
                    "Localisation :\n" +
                    "  - Latitude : " + latitude + "\n" +
                    "  - Longitude : " + longitude + "\n\n" +
                    "État initial : " + etat + "\n\n" +
                    "Nous vous prions de prendre connaissance de cette tâche et de la traiter dans les meilleurs délais.\n\n" +
                    "Pour plus de détails ou en cas de questions, n'hésitez pas à contacter le responsable de la planification.\n\n" +
                    "Nous vous remercions pour votre engagement et votre contribution à nos efforts.\n\n" +
                    "Cordialement,\n\n" +
                    "L'équipe E-WASTE";

            EmailRepository.sendEmail(recipientEmail, subject, emailMessage);

            // Rafraîchir le tableau
            loadTableData();

            fieldIdEmploye.clear();
            fieldMessage.clear();
            fieldLatitude.clear();
            fieldLongitude.clear();
            fieldEtat.clear();

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

            int employeID = serviceTache.getEmployeIdByName(comboEmploye.getValue());
            String message = fieldMessage.getText();
            String etat = comboEtat.getValue();

            // Utiliser les coordonnées de la carte (plutôt que celles du formulaire)
            float latitude = (float) selectedLatitude;
            float longitude = (float) selectedLongitude;

            if (message.isEmpty() || etat.isEmpty()) {
                showAlert("Erreur", "Tous les champs doivent être remplis !");
                return;
            }

            selectedTache.setId_employe(employeID);
            selectedTache.setLatitude(latitude);
            selectedTache.setLongitude(longitude);
            selectedTache.setMessage(message);
            selectedTache.setEtat(etat);

            serviceTache.modifier(selectedTache);
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

    private void loadTableData() throws SQLException {
        List<Tache> taches = serviceTache.afficher(1);
        ObservableList<Tache> data = FXCollections.observableArrayList(taches);
        tableTaches.setItems(data);
    }

    private void loadComboBoxData() {
        // Création de la liste des options statiques pour le ComboBox
        ObservableList<String> options = FXCollections.observableArrayList("En attente","En cours", "Accomplie","Annulé");

        // Affectation de la liste d'options au ComboBox
        comboEtat.setItems(options);
    }

    private void loadComboEmploye() {
        try {
            // Récupération des employés depuis la table utilisateur
            List<String> employeNames = TacheRepository.getEmployeNames();

            comboEmploye.setItems(FXCollections.observableArrayList(employeNames));

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}