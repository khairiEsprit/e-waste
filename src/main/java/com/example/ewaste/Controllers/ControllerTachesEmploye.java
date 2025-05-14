package com.example.ewaste.Controllers;

import com.example.ewaste.Entities.Tache;
import com.example.ewaste.Main;
import com.example.ewaste.Repository.TacheRepository;
import com.example.ewaste.Repository.TemperatureRepository;
import com.example.ewaste.Utils.GeminiApiTache;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.geometry.Insets;

import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import javafx.scene.Parent;
import javafx.scene.Scene;
import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.concurrent.Task;

public class ControllerTachesEmploye implements Initializable {

    @FXML
    private TilePane tilePaneTaches;
    @FXML
    private TextField Recherche;

    private final TacheRepository tacheService = new TacheRepository();
    private final TemperatureRepository tempService = new TemperatureRepository();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            // Récupérer toutes les tâches une seule fois
            List<Tache> taches = tacheService.getTachesByEmploye(1);

            // Créer toutes les cartes et les associer à leur tâche
            for (Tache tache : taches) {
                VBox taskCard = createTaskCard(tache);
                taskCard.setUserData(tache); // Associer la tâche à la carte pour le filtrage
                tilePaneTaches.getChildren().add(taskCard);
            }

            // Configurer le filtrage
            setupSearch();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de la récupération des tâches.");
        }
    }
    private VBox createTaskCard(Tache tache) {
        WebView map = new WebView();
        map.setPrefSize(300, 200);
        WebEngine engine = map.getEngine();
        engine.load(Main.class.getResource("views/map.html").toExternalForm());
        engine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                String script = String.format(
                        "updateLocation(%f, %f);",
                        tache.getAltitude(),
                        tache.getLongitude()
                );
                engine.executeScript(script);
            }
        });

        Label lblMessage = new Label("Tâche : " + tache.getMessage());
        lblMessage.getStyleClass().add("tache-label");

        Label lblTemperature = new Label("Température : " + tempService.getTemperature(
                tache.getAltitude(),
                tache.getLongitude()
        ));
        lblTemperature.getStyleClass().add("tache-label");

        // Ajout de l'adresse
        Label lblAdresse = new Label("Adresse : " + getAddressFromCoordinates(
                tache.getAltitude(),
                tache.getLongitude()));
        lblAdresse.getStyleClass().add("tache-label");
        lblAdresse.setWrapText(true);

        // Ajout du bouton d'analyse
        Button btnAnalyse = new Button("Analyse Météo");
        btnAnalyse.getStyleClass().add("analyse-button");
        btnAnalyse.setOnAction(event -> ouvrirAnalyseMeteo(tache));

        VBox card = new VBox(10);
        card.getStyleClass().add("task-card");
        card.setPadding(new Insets(10));
        card.getChildren().addAll(map, lblMessage, lblAdresse, lblTemperature, btnAnalyse);

        return card;
    }

    private String getAddressFromCoordinates(double altitude, double longitude) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            String url = String.format(
                    "https://nominatim.openstreetmap.org/reverse?format=json&lat=%.6f&lon=%.6f",
                    altitude,
                    longitude
            );
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", "E-waste/1.0")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            JsonObject jsonObject = JsonParser.parseString(response.body()).getAsJsonObject();
            JsonObject address = jsonObject.getAsJsonObject("address");

            String road = address.has("road") ? address.get("road").getAsString() : "";
            String city = address.has("city") ? address.get("city").getAsString() :
                    (address.has("town") ? address.get("town").getAsString() :
                            (address.has("village") ? address.get("village").getAsString() : ""));

            return road + (road.isEmpty() || city.isEmpty() ? "" : ", ") + city;

        } catch (Exception e) {
            return "Adresse non disponible";
        }
    }

    private void ouvrirAnalyseMeteo(Tache tache) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/AnalyseMeteoDialog.fxml"));
            Parent root = loader.load();
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle("Analyse Météorologique");

            // Références aux éléments UI
            Scene scene = new Scene(root);
            Label altitudeLabel = (Label) scene.lookup("#labelAltitude");
            Label longitudeLabel = (Label) scene.lookup("#labelLongitude");
            Label temperatureLabel = (Label) scene.lookup("#labelTemperature");
            TextArea geminiResponse = (TextArea) scene.lookup("#geminiResponse");
            ProgressIndicator progress = (ProgressIndicator) scene.lookup("#progressIndicator");
            WebView weatherMap = (WebView) scene.lookup("#weatherMap");

            // Mise à jour des coordonnées
            double lat = tache.getAltitude();
            double lon = tache.getLongitude();
            altitudeLabel.setText(String.format("%.4f", lat));
            longitudeLabel.setText(String.format("%.4f", lon));

            // Récupération température
            String tempStr = tempService.getTemperature(lat, lon);
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
                            "- Altitude: %.4f\n- Longitude: %.4f\n- Température: %.1f°C\n" +
                            "Donne une analyse détaillée avec des recommandations pour les travailleurs.",
                    lat, lon, temperature
            );

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

            WebEngine webEngine = weatherMap.getEngine();
            webEngine.load("https://www.openstreetmap.org/?mlat=" + lat + "&mlon=" + lon + "#map=14/" + lat + "/" + lon);

            dialog.setScene(scene);
            dialog.show();

        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir l'analyse météo");
        }
    }

    @FXML
    private void ouvrirCalendrier() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/CalendrierTaches.fxml"));
            Parent root = loader.load();

            Scene scene = tilePaneTaches.getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    private void setupSearch() {
        Recherche.textProperty().addListener((observable, oldValue, newValue) -> {
            String filter = (newValue == null ? "" : newValue.toLowerCase());

            // Parcourir les cartes existantes dans tilePaneTaches
            for (Node node : tilePaneTaches.getChildren()) {
                if (node instanceof VBox) {
                    VBox card = (VBox) node;
                    Tache tache = (Tache) card.getUserData(); // Récupérer la tâche associée

                    if (tache != null) {
                        // Vérifier si la tâche correspond au filtre
                        boolean matches = filter.isEmpty() ||
                                String.valueOf(tache.getId_employe()).contains(filter) ||
                                tache.getMessage().toLowerCase().contains(filter) ||
                                tache.getEtat().toLowerCase().contains(filter);

                        // Afficher ou masquer la carte
                        card.setVisible(matches);
                        card.setManaged(matches); // Retirer la carte du layout si masquée
                    }
                }
            }
        });
    }


    @FXML
    private void telechargerListeTachesCSV() {
        try {
            // Fetch tasks for the employee (assuming ID 1 for now)
            List<Tache> taches = tacheService.getTachesByEmploye(1);

            if (taches.isEmpty()) {
                showAlert("Information", "Aucune tâche à exporter.");
                return;
            }

            // Open a file chooser dialog to let the user select where to save the CSV
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Enregistrer la liste des tâches en CSV");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers CSV", "*.csv"));
            fileChooser.setInitialFileName("taches_employe.csv");

            Stage stage = (Stage) tilePaneTaches.getScene().getWindow();
            File file = fileChooser.showSaveDialog(stage);

            if (file == null) {
                return; // User canceled the dialog
            }

            // Write tasks to CSV
            try (java.io.PrintWriter writer = new java.io.PrintWriter(file, "UTF-8")) {
                // CSV Header
                writer.println("ID Employé,Message,État,Altitude,Longitude,Adresse,Température");

                // CSV Rows
                for (Tache tache : taches) {
                    String adresse = getAddressFromCoordinates(tache.getAltitude(), tache.getLongitude());
                    String temperature = tempService.getTemperature(tache.getAltitude(), tache.getLongitude());
                    String line = String.format(
                            "%d,\"%s\",\"%s\",%.6f,%.6f,\"%s\",\"%s\"",
                            tache.getId_employe(),
                            tache.getMessage().replace("\"", "\"\""), // Escape quotes in message
                            tache.getEtat(),
                            tache.getAltitude(),
                            tache.getLongitude(),
                            adresse.replace("\"", "\"\""), // Escape quotes in address
                            temperature
                    );
                    writer.println(line);
                }

                showAlert("Succès", "Liste des tâches exportée avec succès vers " + file.getAbsolutePath());
            } catch (java.io.IOException e) {
                showAlert("Erreur", "Erreur lors de l'exportation du fichier CSV : " + e.getMessage());
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de la récupération des tâches : " + e.getMessage());
        }
    }

}


