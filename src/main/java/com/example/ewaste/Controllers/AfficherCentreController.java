package com.example.ewaste.Controllers;

import com.example.ewaste.Utils.DataBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.fxml.FXMLLoader;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import com.example.ewaste.Entities.Centre;
import com.example.ewaste.Repository.CentreRepository;
import netscape.javascript.JSObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

import org.json.JSONObject;

public class AfficherCentreController {

    @FXML
    private ListView<Centre> affichage;

    @FXML
    private TextField TelephoneCentre;

    @FXML
    private TextField LongitudeCentre;

    @FXML
    private TextField AltitudeCentre;

    @FXML
    private TextField NomCentre;

    @FXML
    private TextField EmailCentre;

    @FXML
    private Text CountryFlag;

    @FXML
    private TextField Recherche;

    @FXML
    private WebView Map; // Note: Unused; mapView is used instead

    private CentreRepository centreRepository = new CentreRepository();

    @FXML
    private WebView mapView;
    private WebEngine webEngine;

    private Centre centreToModify; // Field to store the centre being modified



    @FXML
    void GoToAjout(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ewaste/views/AjouterCentre.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Ajouter un Centre");
            dialogStage.setScene(scene);
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(((Node) event.getSource()).getScene().getWindow());
            dialogStage.showAndWait();
            // Refresh ListView and update map after modal closes, similar to ModifierCentre
            loadData();
            Centre selected = affichage.getSelectionModel().getSelectedItem();
            if (selected != null && mapView != null) {
                setLocation(selected.getLatitude(), selected.getLongitude());
            } else if (mapView != null) {
                webEngine.executeScript("if (marker) { map.removeLayer(marker); }");
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger la boîte de dialogue.");
        }
    }

    @FXML
    public void initialize() {
        loadData();
        affichage.setCellFactory(param -> new CentreListCellController());
        LongitudeCentre.setEditable(false);
        AltitudeCentre.setEditable(false);
        detectUserLocation();
        setupSearch();

        if (mapView != null) {
            webEngine = mapView.getEngine();
            URL mapUrl = getClass().getResource("/com/example/ewaste/views/map.html");
            if (mapUrl != null) {
                webEngine.load(mapUrl.toExternalForm());
                webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
                    if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                        JSObject window = (JSObject) webEngine.executeScript("window");
                        window.setMember("javaApp", this);
                        Centre selected = affichage.getSelectionModel().getSelectedItem();
                        if (selected != null) {
                            setLocation(selected.getLatitude(), selected.getLongitude());
                        }
                    } else if (newState == javafx.concurrent.Worker.State.FAILED) {
                        System.out.println("Erreur : Échec du chargement de map.html");
                    }
                });
            } else {
                System.out.println("Erreur : Fichier map.html introuvable !");
            }
        }

        affichage.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                NomCentre.setText(newSelection.getNom());
                LongitudeCentre.setText(String.valueOf(newSelection.getLongitude()));
                AltitudeCentre.setText(String.valueOf(newSelection.getLatitude()));
                TelephoneCentre.setText(String.valueOf(newSelection.getTelephone()));
                EmailCentre.setText(newSelection.getEmail());
                if (mapView != null) {
                    setLocation(newSelection.getLatitude(), newSelection.getLongitude());
                    setCoordinates(newSelection.getLatitude(), newSelection.getLongitude());
                }
                openModifierCentreWindow(newSelection);
            }
        });
    }

    public void setCentreData(Centre centre) {
        centreToModify = centre;
        NomCentre.setText(centre.getNom());
        LongitudeCentre.setText(String.valueOf(centre.getLongitude()));
        AltitudeCentre.setText(String.valueOf(centre.getLatitude()));
        TelephoneCentre.setText(String.valueOf(centre.getTelephone()));
        EmailCentre.setText(centre.getEmail());
    }

    private void openModifierCentreWindow(Centre selectedCentre) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ewaste/views/ModifierCentre.fxml"));
            Parent root = loader.load();
            AfficherCentreController controller = loader.getController();
            controller.setCentreData(selectedCentre);
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Modifier Centre");
            dialogStage.setScene(new Scene(root));
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(affichage.getScene().getWindow());
            dialogStage.showAndWait();
            loadData();
            Centre selected = affichage.getSelectionModel().getSelectedItem();
            if (selected != null && mapView != null) {
                setLocation(selected.getLatitude(), selected.getLongitude());
            } else if (mapView != null) {
                webEngine.executeScript("if (marker) { map.removeLayer(marker); }");
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger la fenêtre de modification.");
        }
    }

    public void setCoordinates(double lat, double lon) {
        LongitudeCentre.setText(String.valueOf(lon));
        AltitudeCentre.setText(String.valueOf(lat));
        setLocation(lat, lon);
    }

    public void setLocation(double lat, double lon) {
        if (webEngine != null && webEngine.getLoadWorker().getState() == javafx.concurrent.Worker.State.SUCCEEDED) {
            try {
                String script = "updateLocation(" + lat + ", " + lon + ")";
                webEngine.executeScript(script);
            } catch (netscape.javascript.JSException e) {
                System.out.println("Erreur JavaScript : " + e.getMessage());
            }
        } else {
            System.out.println("WebEngine non prêt ou page non chargée. Latitude: " + lat + ", Longitude: " + lon);
        }
    }

    @FXML
    private void afficherCentre(Centre centre) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ewaste/views/Afficher_Centre.fxml"));
        try {
            Parent root = loader.load();
            AfficherCentreController controller = loader.getController();
            if (centre != null) {
                controller.setLocation(centre.getLatitude(), centre.getLongitude());
            } else {
                System.out.println("Erreur : Centre non trouvé !");
            }
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadData() {
        try {
            List<Centre> centreList = centreRepository.afficher();
            ObservableList<Centre> observableCentres = FXCollections.observableArrayList(centreList);
            affichage.setItems(observableCentres);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors du chargement des données.");
        }
    }

    @FXML
    public void Supprimer(ActionEvent actionEvent) {
        Centre centreSelectionne = affichage.getSelectionModel().getSelectedItem();
        if (centreSelectionne != null) {
            try {
                centreRepository.supprimer(centreSelectionne.getId());
                affichage.getItems().remove(centreSelectionne);
                System.out.println("Centre supprimé : " + centreSelectionne);
                if (mapView != null) {
                    webEngine.executeScript("if (marker) { map.removeLayer(marker); }");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Erreur", "Erreur lors de la suppression du centre.");
            }
        } else {
            showAlert("Erreur", "Aucun centre sélectionné pour la suppression.");
        }
    }

    @FXML
    public void SupprimerFromModifier(ActionEvent event) {
        if (centreToModify != null) {
            try {
                centreRepository.supprimer(centreToModify.getId());
                affichage.getItems().remove(centreToModify);
                showAlert("Succès", "Le centre a été supprimé avec succès.");
                if (mapView != null) {
                    webEngine.executeScript("if (marker) { map.removeLayer(marker); }");
                }
                centreToModify = null;
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.close();
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Erreur", "Erreur lors de la suppression du centre.");
            }
        } else {
            showAlert("Erreur", "Aucun centre n'est disponible pour suppression.");
        }
    }

    @FXML
    void ModifierCentre(ActionEvent event) {
        if (centreToModify != null) {
            if (validateInputs()) {
                centreToModify.setNom(NomCentre.getText());
                centreToModify.setLongitude(Float.parseFloat(LongitudeCentre.getText()));
                centreToModify.setLatitude(Float.parseFloat(AltitudeCentre.getText()));
                centreToModify.setTelephone(Integer.parseInt(TelephoneCentre.getText()));
                centreToModify.setEmail(EmailCentre.getText());

                String sql = "UPDATE centre SET nom=?, longitude=?, altitude=?, telephone=?, email=? WHERE id=?";
                try (Connection conn = DataBase.getInstance().getConnection();
                     PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, centreToModify.getNom());
                    stmt.setFloat(2, centreToModify.getLongitude());
                    stmt.setFloat(3, centreToModify.getLatitude());
                    stmt.setInt(4, centreToModify.getTelephone());
                    stmt.setString(5, centreToModify.getEmail());
                    stmt.setInt(6, centreToModify.getId());

                    stmt.executeUpdate();
                    showAlert("Succès", "Le centre a été modifié avec succès.");
                    if (mapView != null) {
                        setLocation(centreToModify.getLatitude(), centreToModify.getLongitude());
                    }
                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    stage.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert("Erreur", "Erreur lors de la modification du centre.");
                }
            } else {
                System.out.println("Les données sont invalides.");
            }
        } else {
            showAlert("Erreur", "Aucun centre n'est disponible pour modification.");
        }
    }

    @FXML
    void AjouterCentre(ActionEvent event) {
        if (validateInputs()) {
            String nom = NomCentre.getText();
            float longitude = Float.parseFloat(LongitudeCentre.getText());
            float altitude = Float.parseFloat(AltitudeCentre.getText());
            String email = EmailCentre.getText();
            String phoneText = TelephoneCentre.getText();

            try {
                int telephone = Integer.parseInt(phoneText);
                if (centreRepository.existeCentre(altitude, longitude)) {
                    showAlert("Erreur", "Un centre avec ces coordonnées existe déjà !");
                    return;
                }
                Centre centre = new Centre(nom, longitude, altitude, telephone, email);
                centreRepository.ajouter(centre);
                NomCentre.clear();
                LongitudeCentre.clear();
                AltitudeCentre.clear();
                EmailCentre.clear();
                TelephoneCentre.clear();
                loadData(); // Refresh the ListView
                if (mapView != null) {
                    setLocation(altitude, longitude); // Update map with new centre
                }
                System.out.println("Centre ajouté : " + centre);
                // Close the Ajouter Centre window
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.close();
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Erreur", "Erreur lors de l'ajout du centre.");
            } catch (NumberFormatException e) {
                e.printStackTrace();
                showAlert("Erreur", "Veuillez entrer un numéro de téléphone et des coordonnées valides.");
            }
        }
    }

    private boolean validateInputs() {
        String nom = NomCentre.getText();
        String longitudeText = LongitudeCentre.getText();
        String altitudeText = AltitudeCentre.getText();
        String email = EmailCentre.getText();
        String phoneText = TelephoneCentre.getText();

        if (nom.isEmpty() || longitudeText.isEmpty() || altitudeText.isEmpty() || email.isEmpty() || phoneText.isEmpty()) {
            showAlert("Erreur", "Tous les champs doivent être remplis !");
            return false;
        }

        try {
            if (!nom.matches("[A-Za-z\\s]+")) {
                showAlert("Erreur", "Le nom ne doit contenir que des lettres et des espaces.");
                return false;
            }
            Float.parseFloat(longitudeText);
            Float.parseFloat(altitudeText);
            if (phoneText.length() != 8) {
                showAlert("Erreur", "Le numéro de téléphone doit contenir 8 chiffres.");
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Veuillez entrer un numéro de téléphone et des coordonnées valides.");
            return false;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            showAlert("Erreur", "Veuillez entrer un email valide.");
            return false;
        }
        return true;
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    void GoToContrat(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ewaste/views/Afficher_Contrat.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void detectUserLocation() {
        String apiUrl = "http://ip-api.com/json/";
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(apiUrl).openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            connection.connect();
            Scanner scanner = new Scanner(connection.getInputStream());
            String response = scanner.useDelimiter("\\A").next();
            scanner.close();
            JSONObject json = new JSONObject(response);
            String countryCode = json.getString("countryCode");
            String countryName = json.getString("country");
            updatePhoneField(countryCode, countryName);
        } catch (IOException e) {
            System.out.println("Erreur lors de la récupération de la localisation.");
            e.printStackTrace();
        }
    }

    private void updatePhoneField(String countryCode, String countryName) {
        String phoneCode = "";
        String flagEmoji = "";
        switch (countryCode) {
            case "TN":
                phoneCode = "+216";
                flagEmoji = "🇹🇳";
                break;
            case "FR":
                phoneCode = "+33";
                flagEmoji = "🇫🇷";
                break;
            default:
                phoneCode = "";
                flagEmoji = "🌍";
                break;
        }
        TelephoneCentre.setPromptText(phoneCode);
        CountryFlag.setText(flagEmoji + " " + countryName);
    }

    private void setupSearch() {
        Recherche.textProperty().addListener((observable, oldValue, newValue) -> {
            List<Centre> filteredCentres;
            try {
                filteredCentres = centreRepository.afficher().stream()
                        .filter(centre -> newValue == null || newValue.isEmpty() ||
                                centre.getNom().toLowerCase().contains(newValue.toLowerCase()))
                        .toList();
                affichage.setItems(FXCollections.observableArrayList(filteredCentres));
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Erreur", "Erreur lors du filtrage des centres.");
            }
        });
    }
}