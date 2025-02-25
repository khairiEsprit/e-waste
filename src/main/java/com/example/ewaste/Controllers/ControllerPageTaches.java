package com.example.ewaste.Controllers;

import com.example.ewaste.Entities.Tache;
import com.example.ewaste.Repository.TacheRepository;
import com.example.ewaste.Repository.EmailRepository;
import com.example.ewaste.Repository.TemperatureRepository;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import netscape.javascript.JSObject;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class ControllerPageTaches implements Initializable {

    // Main page elements
    @FXML private TableView<Tache> tableTaches;
    @FXML private TableColumn<Tache, Integer> colId;
    @FXML private TableColumn<Tache, String> colEmploye;
    @FXML private TableColumn<Tache, String> colMessage;
    @FXML private TableColumn<Tache, Float> colLatitude;
    @FXML private TableColumn<Tache, Float> colLongitude;
    @FXML private TableColumn<Tache, String> colEtat;
    @FXML private Label labelTemperature;
    @FXML private Button btnAjouter;
    @FXML private Button btnModifier;
    @FXML private WebView mapMain;

    // Add dialog elements
    @FXML private ComboBox<String> comboEmployeAjout;
    @FXML private TextField fieldMessageAjout;
    @FXML private TextField fieldLatitudeAjout;
    @FXML private TextField fieldLongitudeAjout;
    @FXML private ComboBox<String> comboEtatAjout;
    @FXML private WebView mapAjout;
    @FXML private Button btnAjouterTache;
    @FXML private Button btnFermerAjout;

    // Modify dialog elements
    @FXML private ComboBox<String> comboEmployeModif;
    @FXML private TextField fieldMessageModif;
    @FXML private TextField fieldLatitudeModif;
    @FXML private TextField fieldLongitudeModif;
    @FXML private ComboBox<String> comboEtatModif;
    @FXML private WebView mapModif;
    @FXML private Button btnModifierTache;
    @FXML private Button btnFermerModif;

    private WebEngine webEngineMain;
    private WebEngine webEngineAjout;
    private WebEngine webEngineModif;
    private double selectedLatitudeMain = 36.8065;
    private double selectedLongitudeMain = 10.1815;
    private double selectedLatitudeAjout = 36.8065;
    private double selectedLongitudeAjout = 10.1815;
    private double selectedLatitudeModif;
    private double selectedLongitudeModif;
    private Tache selectedTache;

    private final TacheRepository serviceTache = new TacheRepository();
    private final TemperatureRepository serviceTemperature = new TemperatureRepository();
    private final EmailRepository emailService = new EmailRepository();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize main page
        if (tableTaches != null) {
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

            try {
                loadTableData();
                initMapMain(); // Initialize the map first
                updateTemperature(selectedLatitudeMain, selectedLongitudeMain); // Then update temperature
            } catch (SQLException e) {
                e.printStackTrace();
            }

            // Update map and temperature when a task is selected
            tableTaches.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
                if (newValue != null) {
                    selectedLatitudeMain = newValue.getLatitude();
                    selectedLongitudeMain = newValue.getLongitude();
                    updateMapMain(selectedLatitudeMain, selectedLongitudeMain);
                    updateTemperature(selectedLatitudeMain, selectedLongitudeMain);
                }
            });
        }

        // Initialize Add dialog
        if (comboEmployeAjout != null) {
            loadComboBoxData(comboEtatAjout);
            loadComboEmploye(comboEmployeAjout);
            initMapAjout();
        }

        // Initialize Modify dialog
        if (comboEmployeModif != null) {
            loadComboBoxData(comboEtatModif);
            loadComboEmploye(comboEmployeModif);
            initMapModif();
        }
    }

    @FXML
    private void ouvrirAjouterTache() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ewaste/views/views/AjouterTache.fxml"));
            Parent root = loader.load();
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle("Ajouter une tâche");
            dialog.setScene(new Scene(root));
            dialog.setOnCloseRequest(event -> refreshTable());
            dialog.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir le dialogue d'ajout ! Détails : " + e.getMessage());
        }
    }

    @FXML
    private void ouvrirModifierTache() {
        selectedTache = tableTaches.getSelectionModel().getSelectedItem();
        if (selectedTache == null) {
            showAlert("Erreur", "Veuillez sélectionner une tâche à modifier.");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ewaste/views/views/ModifierTache.fxml"));
            Parent root = loader.load();
            ControllerPageTaches controller = loader.getController();

            // Wait for WebView to load before initializing data
            WebEngine engine = controller.mapModif.getEngine();
            engine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
                if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                    controller.initModifData(selectedTache);
                    // Set the initial coordinates
                    controller.setCoordinates(selectedTache.getLatitude(), selectedTache.getLongitude());
                }
            });

            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle("Modifier une tâche");
            dialog.setScene(new Scene(root));
            dialog.setOnCloseRequest(event -> refreshTable());
            dialog.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir le dialogue de modification ! Détails : " + e.getMessage());
        }
    }

    @FXML
    private void ajouterTache() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, "Confirmer l'ajout de la tâche ?", ButtonType.YES, ButtonType.NO);
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                try {
                    String nomEmploye = comboEmployeAjout.getValue();
                    float latitude = Float.parseFloat(fieldLatitudeAjout.getText());
                    float longitude = Float.parseFloat(fieldLongitudeAjout.getText());
                    String message = fieldMessageAjout.getText();
                    String etat = comboEtatAjout.getValue();

                    if (message.isEmpty() || etat.isEmpty() || nomEmploye == null) {
                        showAlert("Erreur", "Tous les champs doivent être remplis !");
                        return;
                    }

                    Integer idEmploye = serviceTache.getEmployeIdByName(nomEmploye);
                    Tache nouvelleTache = new Tache(1, idEmploye, latitude, longitude, message, etat);
                    serviceTache.ajouter(nouvelleTache);

                    String recipientEmail = serviceTache.getEmployeEmailById(idEmploye);
                    String subject = "[Tâche Affectée] Nouvelle Tâche Assignée";
                    String googleMapsLink = "https://www.google.com/maps?q=" + latitude + "," + longitude;
                    String emailMessage = "Bonjour " + nomEmploye + ",\n\n" +
                            "Nous vous informons qu'une nouvelle tâche vous a été assignée dans le cadre de vos responsabilités :\n\n" +
                            "Description de la tâche : " + message + "\n\n" +
                            "Localisation :\n" +
                            //"  - Latitude : " + latitude + "\n" +
                            //"  - Longitude : " + longitude + "\n\n" +
                            "  - Adresse : " +googleMapsLink+ "\n\n" +
                            "État initial : " + etat + "\n\n" +
                            "Nous vous prions de prendre connaissance de cette tâche et de la traiter dans les meilleurs délais.\n\n" +
                            "Pour plus de détails ou en cas de questions, n'hésitez pas à contacter le responsable de la planification.\n\n" +
                            "Nous vous remercions pour votre engagement et votre contribution à nos efforts.\n\n" +
                            "Cordialement,\n\n" +
                            "L'équipe E-WASTE";
                    EmailRepository.sendEmail(recipientEmail, subject, emailMessage);

                    fermerAjouterDialog();
                    loadTableData();
                } catch (SQLException e) {
                    showAlert("Erreur", "Impossible d'ajouter la tâche !");
                } catch (NumberFormatException e) {
                    showAlert("Erreur", "Les coordonnées doivent être des nombres valides !");
                }
            }
        });
    }

    @FXML
    private void modifierTache() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, "Confirmer la modification de la tâche ?", ButtonType.YES, ButtonType.NO);
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                try {
                    int employeID = serviceTache.getEmployeIdByName(comboEmployeModif.getValue());
                    String message = fieldMessageModif.getText();
                    String etat = comboEtatModif.getValue();
                    float latitude = Float.parseFloat(fieldLatitudeModif.getText());
                    float longitude = Float.parseFloat(fieldLongitudeModif.getText());

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
                    fermerModifierDialog();
                    loadTableData();
                } catch (SQLException e) {
                    showAlert("Erreur", "Impossible de modifier la tâche !");
                } catch (NumberFormatException e) {
                    showAlert("Erreur", "Les coordonnées doivent être valides !");
                }
            }
        });
    }

    @FXML
    private void supprimerTache() {
        Tache selectedTache = tableTaches.getSelectionModel().getSelectedItem();
        if (selectedTache == null) {
            showAlert("Erreur", "Veuillez sélectionner une tâche à supprimer.");
            return;
        }
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, "Êtes-vous sûr de vouloir supprimer cette tâche ?", ButtonType.OK, ButtonType.CANCEL);
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    serviceTache.supprimer(selectedTache.getId());
                    loadTableData();
                } catch (SQLException e) {
                    showAlert("Erreur", "Impossible de supprimer la tâche !");
                }
            }
        });
    }

    @FXML
    private void ouvrirPlannification() {
        Tache selectedTache = tableTaches.getSelectionModel().getSelectedItem();
        if (selectedTache == null) {
            showAlert("Erreur", "Veuillez sélectionner une tâche pour la planification.");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ewaste/views/views/PlannificationTache.fxml"));
            Parent root = loader.load();
            ControllerPlannificationTache controller = loader.getController();
            controller.initData(selectedTache.getId());
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
    private void fermerAjouterDialog() {
        Stage stage = (Stage) btnAjouterTache.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void fermerModifierDialog() {
        Stage stage = (Stage) btnModifierTache.getScene().getWindow();
        stage.close();
    }

    private void initMapMain() {
        webEngineMain = mapMain.getEngine();
        URL mapUrl = getClass().getResource("/com/example/ewaste/views/map.html");
        if (mapUrl != null) {
            webEngineMain.load(mapUrl.toExternalForm());
            webEngineMain.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
                if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                    JSObject window = (JSObject) webEngineMain.executeScript("window");
                    window.setMember("javaApp", this);
                    updateMapMain(selectedLatitudeMain, selectedLongitudeMain);
                }
            });
        } else {
            System.out.println("map.html not found at /com/example/ewaste/views/map.html");
        }
    }

    private void initMapAjout() {
        webEngineAjout = mapAjout.getEngine();
        URL mapUrl = getClass().getResource("/com/example/ewaste/views/map.html");
        if (mapUrl != null) {
            webEngineAjout.load(mapUrl.toExternalForm());
            webEngineAjout.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
                if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                    JSObject window = (JSObject) webEngineAjout.executeScript("window");
                    window.setMember("javaApp", this);
                    updateMapAjout(selectedLatitudeAjout, selectedLongitudeAjout);
                }
            });
        }
    }

    private void initMapModif() {
        webEngineModif = mapModif.getEngine();
        URL mapUrl = getClass().getResource("/com/example/ewaste/views/map.html");
        if (mapUrl != null) {
            webEngineModif.load(mapUrl.toExternalForm());
        }
    }

    public void updateMapMain(double latitude, double longitude) {
        if (webEngineMain != null) {
            try {
                String script = String.format("updateLocation(%f, %f);", latitude, longitude);
                webEngineMain.executeScript(script);
            } catch (Exception e) {
                System.out.println("Error updating mapMain: " + e.getMessage());
            }
        }
    }

    public void updateMapAjout(double latitude, double longitude) {
        if (webEngineAjout != null) {
            String script = String.format("updateLocation(%f, %f);", latitude, longitude);
            webEngineAjout.executeScript(script);
        }
    }

    public void updateMapModif(double latitude, double longitude) {
        if (webEngineModif != null) {
            String script = String.format("updateLocation(%f, %f);", latitude, longitude);
            webEngineModif.executeScript(script);
        }
    }

    public void setCoordinates(double lat, double lon) {
        if (fieldLatitudeAjout != null && fieldLongitudeAjout != null) {
            this.selectedLatitudeAjout = lat;
            this.selectedLongitudeAjout = lon;
            fieldLatitudeAjout.setText(String.valueOf(lat));
            fieldLongitudeAjout.setText(String.valueOf(lon));
            updateMapAjout(lat, lon);
        } else if (fieldLatitudeModif != null && fieldLongitudeModif != null) {
            this.selectedLatitudeModif = lat;
            this.selectedLongitudeModif = lon;
            fieldLatitudeModif.setText(String.valueOf(lat));
            fieldLongitudeModif.setText(String.valueOf(lon));
            updateMapModif(lat, lon);
        } else if (mapMain != null) {
            this.selectedLatitudeMain = lat;
            this.selectedLongitudeMain = lon;
            updateMapMain(lat, lon);
            updateTemperature(lat, lon);
        }
    }

    private void loadTableData() throws SQLException {
        List<Tache> taches = serviceTache.afficher(1);
        ObservableList<Tache> data = FXCollections.observableArrayList(taches);
        tableTaches.setItems(data);
    }

    private void refreshTable() {
        try {
            loadTableData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateTemperature(double latitude, double longitude) {
        String temperature = serviceTemperature.getTemperature(latitude, longitude);
        labelTemperature.setText(temperature);
    }

    private void loadComboBoxData(ComboBox<String> comboBox) {
        comboBox.setItems(FXCollections.observableArrayList("En attente", "En cours", "Accomplie", "Annulé"));
    }

    private void loadComboEmploye(ComboBox<String> comboBox) {
        try {
            List<String> employeNames = TacheRepository.getEmployeNames();
            comboBox.setItems(FXCollections.observableArrayList(employeNames));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void initModifData(Tache tache) {
        try {
            String employeNom = serviceTache.getEmployeNameById(tache.getId_employe());
            comboEmployeModif.setValue(employeNom);
            fieldMessageModif.setText(tache.getMessage());
            fieldLatitudeModif.setText(String.valueOf(tache.getLatitude()));
            fieldLongitudeModif.setText(String.valueOf(tache.getLongitude()));
            comboEtatModif.setValue(tache.getEtat());
            selectedLatitudeModif = tache.getLatitude();
            selectedLongitudeModif = tache.getLongitude();
            updateMapModif(selectedLatitudeModif, selectedLongitudeModif);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}