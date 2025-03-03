package com.example.ewaste.Controllers;

import com.example.ewaste.Entities.Tache;
import com.example.ewaste.Repository.TacheRepository;
import com.example.ewaste.Repository.EmailRepository;
import com.example.ewaste.Repository.TemperatureRepository;
import com.example.ewaste.Utils.GeminiApiTache;
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
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import netscape.javascript.JSObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import java.util.Locale;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

public class ControllerPageTaches implements Initializable {

    // Main page elements
    @FXML private TableView<Tache> tableTaches;
    @FXML private TableColumn<Tache, Integer> colId;
    @FXML private TableColumn<Tache, String> colEmploye;
    @FXML private TableColumn<Tache, String> colMessage;
    @FXML private TableColumn<Tache, String> colAdresse;
    @FXML private TableColumn<Tache, String> colEtat;
    @FXML private Label labelTemperature;
    @FXML private Button btnAjouter;
    @FXML private Button btnModifier;
    @FXML private WebView mapMain;
    @FXML
    private TextField searchField;


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
    private FilteredList<Tache> filteredData;
    private SortedList<Tache> sortedData;

    private final TacheRepository serviceTache = new TacheRepository();
    private final TemperatureRepository serviceTemperature = new TemperatureRepository();
    private final EmailRepository emailService = new EmailRepository();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
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
            colAdresse.setCellValueFactory(param -> {
                Tache tache = param.getValue();
                return new SimpleStringProperty(
                        getAddressFromCoordinates(tache.getLatitude(), tache.getLongitude())
                );
            });
            colEtat.setCellValueFactory(new PropertyValueFactory<>("etat"));

            try {
                // Charger les tâches une seule fois
                ObservableList<Tache> allTasks = FXCollections.observableArrayList(serviceTache.afficher(1));

                // Créer un FilteredList à partir des tâches
                filteredData = new FilteredList<>(allTasks, tache -> true); // Affiche tout par défaut

                // Lier le FilteredList à la TableView
                tableTaches.setItems(filteredData);

                // Initialiser la carte et la température
                initMapMain();
                updateTemperature(selectedLatitudeMain, selectedLongitudeMain);

                // Configurer le filtrage
                setupSearch();
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Erreur", "Erreur lors du chargement des tâches.");
            }

            // Listener pour la sélection
            tableTaches.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
                if (newValue != null) {
                    selectedTache = newValue;
                    selectedLatitudeMain = newValue.getLatitude();
                    selectedLongitudeMain = newValue.getLongitude();
                    updateMapMain(selectedLatitudeMain, selectedLongitudeMain);
                    updateTemperature(selectedLatitudeMain, selectedLongitudeMain);
                } else {
                    selectedTache = null;
                }
            });
        }

        // Initialiser les dialogues Ajouter et Modifier si nécessaire
        if (comboEmployeAjout != null) {
            loadComboBoxData(comboEtatAjout);
            loadComboEmploye(comboEmployeAjout);
            initMapAjout();
        }
        if (comboEmployeModif != null) {
            loadComboBoxData(comboEtatModif);
            loadComboEmploye(comboEmployeModif);
            initMapModif();
        }
    }

    @FXML
    private void ouvrirAjouterTache() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ewaste/views/AjouterTache.fxml"));
            Parent root = loader.load();
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle("Ajouter une tâche");
            dialog.setScene(new Scene(root));
            dialog.setOnHidden(event -> refreshTable());
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ewaste/views/ModifierTache.fxml"));
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
            dialog.setOnHidden(event -> refreshTable());
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
                            "Localisation :\n" +googleMapsLink+ "\n\n" +
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ewaste/views/PlannificationTache.fxml"));
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
        ObservableList<Tache> allTasks = FXCollections.observableArrayList(serviceTache.afficher(1));
        filteredData = new FilteredList<>(allTasks, tache -> true);
        tableTaches.setItems(filteredData);
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
            this.selectedTache = tache;
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

    // Ajoutez cette méthode dans votre ControllerPageTaches
    @FXML
    private void ouvrirAnalyseMeteo() {
        Tache selectedTache = tableTaches.getSelectionModel().getSelectedItem();
        if (selectedTache == null) {
            showAlert("Erreur", "Veuillez sélectionner une tâche pour l'analyse météo.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ewaste/views/AnalyseMeteoDialog.fxml"));
            Parent root = loader.load();
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle("Analyse Météorologique");

            // Références aux éléments UI
            Scene scene = new Scene(root);
            Label latitudeLabel = (Label) scene.lookup("#labelLatitude");
            Label longitudeLabel = (Label) scene.lookup("#labelLongitude");
            Label temperatureLabel = (Label) scene.lookup("#labelTemperature");
            TextArea geminiResponse = (TextArea) scene.lookup("#geminiResponse");
            ProgressIndicator progress = (ProgressIndicator) scene.lookup("#progressIndicator");
            WebView weatherMap = (WebView) scene.lookup("#weatherMap");

            // Mise à jour des coordonnées
            double lat = selectedTache.getLatitude();
            double lon = selectedTache.getLongitude();
            latitudeLabel.setText(String.format("%.4f", lat));
            longitudeLabel.setText(String.format("%.4f", lon));

            // Récupération température
            // Récupération température
            String tempStr = serviceTemperature.getTemperature(lat, lon);
            temperatureLabel.setText(tempStr);

            double temperature;
            try {
                String normalizedTemp = tempStr.replace(",", ".");
                String cleanedTemp = normalizedTemp.replaceAll("[^0-9.]", "");
                temperature = Double.parseDouble(cleanedTemp);
            } catch (NumberFormatException e) {
                showAlert("Erreur", "Données de température invalides : " + tempStr);
                return;
            }

            String prompt = String.format(
                    "Analyse météo pour la tâche :\n" +
                            "- Latitude: %.4f\n- Longitude: %.4f\n- Température: %.1f°C\n" +
                            "Donne une analyse détaillée avec des recommandations pour les travailleurs.",
                    lat, lon, temperature
            );

            // Configuration de l'appel API asynchrone
            progress.setVisible(true);
            GeminiApiTache geminiApi = new GeminiApiTache();

            Task<String> geminiTask = new Task<>() {
                @Override
                protected String call() throws Exception {
                    return geminiApi.genererAnalyse(prompt);
                }
            };

            geminiTask.setOnSucceeded(e -> {
                geminiResponse.setText(geminiTask.getValue());
                progress.setVisible(false);
            });

            geminiTask.setOnFailed(e -> {
                progress.setVisible(false);
                showAlert("Erreur", "Échec de l'analyse : " + geminiTask.getException().getMessage());
            });

            new Thread(geminiTask).start();

            // Carte OpenStreetMap
            WebEngine webEngine = weatherMap.getEngine();
            webEngine.load("https://www.openstreetmap.org/?mlat=" + lat + "&mlon=" + lon + "#map=14/" + lat + "/" + lon);

            dialog.setScene(scene);
            dialog.show();

        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir l'analyse météo");
        }
    }

    private String getAddressFromCoordinates(double latitude, double longitude) {
        try {
            // Vérification des coordonnées
            if (latitude < -90 || latitude > 90 || longitude < -180 || longitude > 180) {
                return "Coordonnées invalides";
            }

            HttpClient client = HttpClient.newHttpClient();
            String url = String.format(
                    Locale.US,
                    "https://nominatim.openstreetmap.org/reverse?format=json&lat=%.6f&lon=%.6f",
                    latitude,
                    longitude
            );
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", "E-waste/1.0")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Vérifiez le code de statut HTTP
            if (response.statusCode() != 200) {
                return "Erreur serveur : " + response.statusCode();
            }

            // Analysez la réponse JSON
            JsonObject jsonObject = JsonParser.parseString(response.body()).getAsJsonObject();
            JsonObject address = jsonObject.getAsJsonObject("address");

            // Vérifiez si "address" existe
            if (address == null) {
                return "Aucune adresse trouvée dans la réponse";
            }

            // Récupérez les champs avec des valeurs par défaut si absents
            String road = address.has("road") ? address.get("road").getAsString() : "Route inconnue";
            String city = address.has("city") ? address.get("city").getAsString() :
                    (address.has("town") ? address.get("town").getAsString() :
                            (address.has("village") ? address.get("village").getAsString() : "Ville inconnue"));

            return road + ", " + city;

        } catch (Exception e) {
            e.printStackTrace();
            return "Erreur : " + e.getMessage();
        }
    }

    private void setupSearch() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(tache -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true; // Affiche toutes les tâches si le champ est vide
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return String.valueOf(tache.getId_employe()).contains(lowerCaseFilter) ||
                        tache.getMessage().toLowerCase().contains(lowerCaseFilter) ||
                        tache.getEtat().toLowerCase().contains(lowerCaseFilter);
            });
        });
    }

    @FXML
    private void telechargerCSV() {
        try {
            // Ouvrir un sélecteur de fichier pour choisir où sauvegarder le CSV
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Sauvegarder la liste des tâches en CSV");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers CSV", "*.csv"));
            fileChooser.setInitialFileName("liste_taches.csv");
            Stage stage = (Stage) tableTaches.getScene().getWindow();
            File file = fileChooser.showSaveDialog(stage);

            if (file == null) {
                return; // L'utilisateur a annulé
            }

            // Récupérer les données de la table
            ObservableList<Tache> taches = tableTaches.getItems();
            if (taches.isEmpty()) {
                showAlert("Information", "Aucune tâche à exporter.");
                return;
            }

            // Écrire dans le fichier CSV
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
                // Écrire l'en-tête UTF-8 BOM pour Excel (optionnel, pour compatibilité)
                writer.write("\uFEFF");

                // Écrire les en-têtes des colonnes
                String header = "ID,Employé,Message,Adresse,État,Latitude,Longitude\n";
                writer.write(header);

                // Parcourir chaque tâche et écrire les données
                for (Tache tache : taches) {
                    String employeNom = serviceTache.getEmployeNameById(tache.getId_employe());
                    String adresse = getAddressFromCoordinates(tache.getLatitude(), tache.getLongitude());

                    // Échapper les virgules et guillemets dans les champs
                    String message = "\"" + tache.getMessage().replace("\"", "\"\"") + "\"";
                    String etat = "\"" + tache.getEtat().replace("\"", "\"\"") + "\"";
                    String employe = "\"" + employeNom.replace("\"", "\"\"") + "\"";
                    String adresseEscaped = "\"" + adresse.replace("\"", "\"\"") + "\"";

                    // Construire la ligne CSV
                    String line = String.format("%d,%s,%s,%s,%s,%.6f,%.6f%n",
                            tache.getId(),
                            employe,
                            message,
                            adresseEscaped,
                            etat,
                            tache.getLatitude(),
                            tache.getLongitude());
                    writer.write(line);
                }

                showAlert("Succès", "La liste des tâches a été exportée avec succès en CSV !");
            } catch (IOException e) {
                showAlert("Erreur", "Erreur lors de l'écriture du fichier CSV : " + e.getMessage());
            } catch (SQLException e) {
                showAlert("Erreur", "Erreur lors de la récupération des données : " + e.getMessage());
            }

        } catch (Exception e) {
            showAlert("Erreur", "Une erreur inattendue est survenue : " + e.getMessage());
        }
    }

}