package com.example.ewaste.Controllers;

import com.example.ewaste.Main;
import com.example.ewaste.Utils.DataBase;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.fxml.FXMLLoader;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.scene.layout.VBox;
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
    private WebView mapView;

    @FXML
    private WebView mapViewAjouter;

    @FXML
    private WebView mapViewModifier;

    @FXML
    private VBox mainContent;

    @FXML
    private VBox ajouterForm;

    @FXML
    private VBox modifierForm;

    @FXML
    private TextField NomCentreMod;

    @FXML
    private TextField LongitudeCentreMod;

    @FXML
    private TextField AltitudeCentreMod;

    @FXML
    private TextField TelephoneCentreMod;

    @FXML
    private TextField EmailCentreMod;

    @FXML
    private Text CountryFlagMod;

    @FXML
    private Button btnAjouter;

    @FXML
    private Button btnModifier;

    @FXML
    private Button btnSupprimer;

    private WebEngine webEngineMain;
    private WebEngine webEngineAjouter;
    private WebEngine webEngineModifier;

    private CentreRepository centreRepository = new CentreRepository();

    private Centre centreToModify;

    private String activeForm = "main";

    @FXML
    public void initialize() {
        loadData();
        affichage.setCellFactory(param -> new CentreListCellController());
        LongitudeCentre.setEditable(true);
        AltitudeCentre.setEditable(true);
        LongitudeCentreMod.setEditable(true);
        AltitudeCentreMod.setEditable(true);
        detectUserLocation();
        setupSearch();

        initializeWebView(mapView, "main", engine -> webEngineMain = engine);
        initializeWebView(mapViewAjouter, "ajouter", engine -> webEngineAjouter = engine);
        initializeWebView(mapViewModifier, "modifier", engine -> webEngineModifier = engine);

        affichage.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                centreToModify = newSelection;
                showModifierForm(null);
            }
        });
    }

    private void initializeWebView(WebView webView, String type, java.util.function.Consumer<WebEngine> engineSetter) {
        if (webView != null) {
            WebEngine engine = webView.getEngine();
            URL mapUrl = Main.class.getResource("views/map.html");
            if (mapUrl != null) {
                System.out.println("Chargement de map.html pour " + type + " : " + mapUrl.toExternalForm());
                engine.load(mapUrl.toExternalForm());
                engine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
                    System.out.println("État du WebEngine pour " + type + " : " + newState);
                    if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                        JSObject window = (JSObject) engine.executeScript("window");
                        window.setMember("javaApp", this);
                        System.out.println("Carte chargée avec succès pour " + type);
                        if (type.equals("main") && affichage.getSelectionModel().getSelectedItem() != null) {
                            Centre selected = affichage.getSelectionModel().getSelectedItem();
                            setLocation(selected.getLatitude(), selected.getLongitude(), engine);
                        }
                    } else if (newState == javafx.concurrent.Worker.State.FAILED) {
                        System.out.println("Erreur : Échec du chargement de map.html pour " + type);
                    }
                });
            } else {
                System.out.println("Erreur : Fichier map.html introuvable pour " + type + " !");
            }
            engineSetter.accept(engine);
        } else {
            System.out.println("WebView null pour " + type);
        }
    }

    public void setCoordinates(double lat, double lon) {
        System.out.println("Coordonnées reçues : lat=" + lat + ", lon=" + lon + ", formulaire actif=" + activeForm);
        if ("ajouter".equals(activeForm)) {
            AltitudeCentre.setText(String.valueOf(lat));
            LongitudeCentre.setText(String.valueOf(lon));
        } else if ("modifier".equals(activeForm)) {
            AltitudeCentreMod.setText(String.valueOf(lat));
            LongitudeCentreMod.setText(String.valueOf(lon));
        }
    }

    @FXML
    void showAjouterForm(ActionEvent event) {
        mainContent.setEffect(new GaussianBlur(5));
        ajouterForm.setVisible(true);
        ajouterForm.setManaged(true);
        clearAjouterForm();
        activeForm = "ajouter";
        if (webEngineAjouter != null) {
            setLocation(36.8065, 10.1815, webEngineAjouter);
        }
    }

    @FXML
    void hideAjouterForm(ActionEvent event) {
        mainContent.setEffect(null);
        ajouterForm.setVisible(false);
        ajouterForm.setManaged(false);
        activeForm = "main";
    }

    @FXML
    void showModifierForm(ActionEvent event) {
        if (centreToModify != null) {
            mainContent.setEffect(new GaussianBlur(5));
            modifierForm.setVisible(true);
            modifierForm.setManaged(true);
            setCentreData(centreToModify);
            activeForm = "modifier";
            // Forcer le rendu et la mise à jour après que le formulaire soit visible
            Platform.runLater(() -> {
                if (webEngineModifier != null) {
                    if (webEngineModifier.getLoadWorker().getState() != javafx.concurrent.Worker.State.SUCCEEDED) {
                        System.out.println("Rechargement de map.html pour modifier...");
                        URL mapUrl = getClass().getResource("/com.example.ewaste/views/map.html");
                        if (mapUrl != null) {
                            webEngineModifier.load(mapUrl.toExternalForm());
                        }
                    }
                    // Attendre que le chargement soit terminé avant de mettre à jour
                    webEngineModifier.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
                        if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                            setLocation(centreToModify.getLatitude(), centreToModify.getLongitude(), webEngineModifier);
                        }
                    });
                    // Forcer une mise à jour si déjà chargé
                    if (webEngineModifier.getLoadWorker().getState() == javafx.concurrent.Worker.State.SUCCEEDED) {
                        setLocation(centreToModify.getLatitude(), centreToModify.getLongitude(), webEngineModifier);
                    }
                }
            });
        } else {
            showAlert("Erreur", "Veuillez sélectionner un centre à modifier.");
        }
    }

    @FXML
    void hideModifierForm(ActionEvent event) {
        mainContent.setEffect(null);
        modifierForm.setVisible(false);
        modifierForm.setManaged(false);
        activeForm = "main";
    }

    public void setCentreData(Centre centre) {
        centreToModify = centre;
        NomCentreMod.setText(centre.getNom());
        LongitudeCentreMod.setText(String.valueOf(centre.getLongitude()));
        AltitudeCentreMod.setText(String.valueOf(centre.getLatitude()));
        TelephoneCentreMod.setText(String.valueOf(centre.getTelephone()));
        EmailCentreMod.setText(centre.getEmail());
        CountryFlagMod.setText(CountryFlag.getText());
    }

    @FXML
    void AjouterCentre(ActionEvent event) {
        if (validateInputs(NomCentre, LongitudeCentre, AltitudeCentre, TelephoneCentre, EmailCentre)) {
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
                loadData();
                if (webEngineMain != null) {
                    setLocation(altitude, longitude, webEngineMain);
                }
                hideAjouterForm(event);
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Erreur", "Erreur lors de l'ajout du centre.");
            } catch (NumberFormatException e) {
                e.printStackTrace();
                showAlert("Erreur", "Veuillez entrer un numéro de téléphone et des coordonnées valides.");
            }
        }
    }

    @FXML
    void ModifierCentre(ActionEvent event) {
        if (centreToModify != null && validateInputs(NomCentreMod, LongitudeCentreMod, AltitudeCentreMod, TelephoneCentreMod, EmailCentreMod)) {
            centreToModify.setNom(NomCentreMod.getText());
            centreToModify.setLongitude(Float.parseFloat(LongitudeCentreMod.getText()));
            centreToModify.setLatitude(Float.parseFloat(AltitudeCentreMod.getText()));
            centreToModify.setTelephone(Integer.parseInt(TelephoneCentreMod.getText()));
            centreToModify.setEmail(EmailCentreMod.getText());

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
                if (webEngineMain != null) {
                    setLocation(centreToModify.getLatitude(), centreToModify.getLongitude(), webEngineMain);
                }
                hideModifierForm(event);
                loadData();
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Erreur", "Erreur lors de la modification du centre.");
            }
        } else {
            showAlert("Erreur", "Aucun centre n'est disponible pour modification ou les données sont invalides.");
        }
    }

    @FXML
    public void SupprimerFromModifier(ActionEvent event) {
        if (centreToModify != null) {
            try {
                centreRepository.supprimer(centreToModify.getId());
                affichage.getItems().remove(centreToModify);
                showAlert("Succès", "Le centre a été supprimé avec succès.");
                if (webEngineMain != null) {
                    webEngineMain.executeScript("if (marker) { map.removeLayer(marker); }");
                }
                centreToModify = null;
                hideModifierForm(event);
                loadData();
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Erreur", "Erreur lors de la suppression du centre.");
            }
        } else {
            showAlert("Erreur", "Aucun centre n'est disponible pour suppression.");
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

    public static void setLocation(double lat, double lon, WebEngine engine) {
        if (engine != null && engine.getLoadWorker().getState() == javafx.concurrent.Worker.State.SUCCEEDED) {
            try {
                String script = "updateLocation(" + lat + ", " + lon + ")";
                engine.executeScript(script);
                System.out.println("Mise à jour de la carte pour latitude: " + lat + ", longitude: " + lon);
            } catch (netscape.javascript.JSException e) {
                System.out.println("Erreur JavaScript : " + e.getMessage());
            }
        } else {
            System.out.println("WebEngine non prêt ou non chargé pour cette mise à jour.");
        }
    }

    public void setLocation(double lat, double lon) {
        setLocation(lat, lon, webEngineMain);
    }

    private boolean validateInputs(TextField nomField, TextField longitudeField, TextField altitudeField, TextField phoneField, TextField emailField) {
        String nom = nomField.getText();
        String longitudeText = longitudeField.getText();
        String altitudeText = altitudeField.getText();
        String email = emailField.getText();
        String phoneText = phoneField.getText();

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

    private void clearAjouterForm() {
        NomCentre.clear();
        LongitudeCentre.clear();
        AltitudeCentre.clear();
        TelephoneCentre.clear();
        EmailCentre.clear();
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
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/Afficher_Contrat.fxml"));
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
            if (json.has("lat") && json.has("lon") && webEngineMain != null) {
                double lat = json.getDouble("lat");
                double lon = json.getDouble("lon");
                setLocation(lat, lon, webEngineMain);
            }
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
        TelephoneCentreMod.setPromptText(phoneCode);
        CountryFlagMod.setText(flagEmoji + " " + countryName);
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