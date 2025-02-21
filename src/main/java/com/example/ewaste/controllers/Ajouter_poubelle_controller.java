package com.example.ewaste.Controllers;
import com.example.ewaste.Entities.etat;
import com.example.ewaste.Entities.poubelle;
import com.example.ewaste.Repository.service_poubelle;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

import javafx.stage.Stage;

import java.net.URL;
import java.sql.SQLException;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.Date;
import java.util.ResourceBundle;

public class Ajouter_poubelle_controller implements Initializable {
    @FXML
    private ListView<poubelle> listPoubelles;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> sortComboBox;

    @FXML
    private ToggleButton themeToggle;

    @FXML
    private Button btnAjouter;

    @FXML
    private Button btnModifier;
    private ObservableList<poubelle> poubelleList = FXCollections.observableArrayList();
    private service_poubelle service = new service_poubelle();
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupList();
        setupSorting();
        setupSearch();
        setupThemeToggle();
        loadData();
        Platform.runLater(() -> {
            Stage stage = (Stage) listPoubelles.getScene().getWindow();
            stage.setMinWidth(1000);
            stage.setMinHeight(700);
            stage.setWidth(1200);
            stage.setHeight(800);
        });
    }
    private void setupList() {
        listPoubelles.setCellFactory(param -> new ListCell<poubelle>() {
            private final GridPane grid = new GridPane();
            private final Label lblId = new Label();
            private final Label lblAdresse = new Label();
            private final ProgressBar progressBar = new ProgressBar(); // Ajout de la barre de progression
            private final Label lblEtat = new Label();
            private final Label lblDate = new Label();
            private final Button deleteButton = new Button("🗑");

            {
                grid.setHgap(10);
                grid.setVgap(5);
                grid.setPadding(new Insets(10));

                // Configuration de la barre de progression
                progressBar.setPrefWidth(200);
                progressBar.setStyle("-fx-accent: #4CAF50;"); // Couleur de base

                grid.addColumn(0, lblId, lblAdresse);
                grid.add(progressBar, 1, 0, 1, 2); // Positionnement de la barre
                grid.addColumn(2, lblEtat, lblDate);
                grid.add(deleteButton, 3, 0, 1, 2);
            }

            @Override
            protected void updateItem(poubelle item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                         lblId.setText("ID:            " + item.getId());
                    lblAdresse.setText("Adresse:       " + item.getAdresse());
                       lblEtat.setText("État:          " + item.getEtat().getLabel());
                      lblEtat.setStyle("-fx-text-fill: " + (item.getEtat() == etat.FONCTIONNEL ? "green" : "red"));
                       lblDate.setText("Installation:  " + item.getDate_installation());
                    // Configuration de la barre de progression
                    progressBar.setProgress(item.getNiveau() / 100.0);
                    updateProgressStyle(item.getNiveau()); // Méthode de style dynamique
                    setGraphic(grid);
                }
            }
            private void updateProgressStyle(int niveau) {
                String style;
                if (niveau >= 80) {
                    style = "-fx-accent: #4CAF50;"; // Vert
                } else if (niveau >= 50) {
                    style = "-fx-accent: #00693e;"; // Orange
                } else {
                    style = "-fx-accent: #F44336;"; // Rouge
                }
                progressBar.setStyle(style);
            }
        });
    }
    private void setupSearch() {
        FilteredList<poubelle> filteredData = new FilteredList<>(poubelleList, p -> true);
        SortedList<poubelle> sortedData = new SortedList<>(filteredData);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(poubelle -> {
                if (newValue == null || newValue.isEmpty()) return true;
                String lowerCaseFilter = newValue.toLowerCase();
                return poubelle.getAdresse().toLowerCase().contains(lowerCaseFilter) ||
                        String.valueOf(poubelle.getId()).contains(lowerCaseFilter) ||
                        poubelle.getEtat().toString().toLowerCase().contains(lowerCaseFilter);
            });
        });

        // Modifier le binding avec vérification de null
        sortedData.comparatorProperty().bind(
                Bindings.createObjectBinding(() -> {
                    String selectedSort = sortComboBox.getValue();
                    if (selectedSort == null) return null;

                    switch(selectedSort) {
                        case "Tri par ID": return Comparator.comparingInt(poubelle::getId);
                        case "Tri par Date": return Comparator.comparing(poubelle::getDate_installation);
                        case "Tri par Niveau": return Comparator.comparingInt(poubelle::getNiveau);
                        case "Tri par État": return Comparator.comparing(p -> p.getEtat().toString());
                        default: return null;
                    }
                }, sortComboBox.valueProperty())
        );

        listPoubelles.setItems(sortedData);
    }
    private void setupSorting() {
        sortComboBox.getItems().addAll(
                "Tri par ID",
                "Tri par Date",
                "Tri par Niveau",
                "Tri par État"
        );

        // Définir une valeur par défaut
        sortComboBox.setValue("Tri par ID");


        sortComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            Comparator<poubelle> comparator;
            switch (newVal) {
                case "Tri par ID":
                    comparator = Comparator.comparingInt(poubelle::getId);
                    break;
                case "Tri par Date":
                    comparator = Comparator.comparing(poubelle::getDate_installation);
                    break;
                case "Tri par Niveau":
                    comparator = Comparator.comparingInt(poubelle::getNiveau);
                    break;
                case "Tri par État":
                    comparator = Comparator.comparing(p -> p.getEtat().toString());
                    break;
                default:
                    comparator = null;
            }

            if (comparator != null) {
                FXCollections.sort(poubelleList, comparator);
            }
        });
    }
    private void setupThemeToggle() {
        themeToggle.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                setDarkTheme();
            } else {
                setLightTheme();
            }
        });
    }
    private void setDarkTheme() {
        listPoubelles.getScene().getRoot().setStyle("-fx-base: #2b2b2b;");
    }

    private void setLightTheme() {
        listPoubelles.getScene().getRoot().setStyle("-fx-base: #f4f4f4;");
    }

    private void loadData() {
        try {
            poubelleList.setAll(service.recuperer());
        } catch (SQLException e) {
            showAlert("Erreur de chargement", e.getMessage());
        }
    }

    @FXML
    private void handleAdd(ActionEvent event) {

    }
    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    // Méthode pour rafraîchir la liste après ajout
    public void refreshList() {
        loadData();
    }

    @FXML
    private void handleEdit() {
        // Ouvrir la fenêtre de modification
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    //Interface ajouter Poubelle:
    // Injection des champs FXML
    @FXML private TextField idCentreField;
    @FXML private TextField adresseField;
    @FXML private TextField niveauField;
    @FXML private DatePicker dateInstallationPicker;
    @FXML private ComboBox<etat> etatComboBox;
    @FXML private Button ajouterBtn;
    @FXML private StackPane confirmationPanel;
    @FXML private Label confirmationMessage;



    // Instance du service
    private  service_poubelle poubelleService = new service_poubelle();
    private Ajouter_poubelle_controller parentController;

    public void setParentController(Ajouter_poubelle_controller parentController) {
        this.parentController = parentController;
    }
    @FXML
    public void initialize() {
        etatComboBox.getItems().setAll(etat.values());
    }
    @FXML
    private void ajouterPoubelle(ActionEvent event) {
        try {
            System.out.println("Bouton cliqué !"); // Debug

            if (champsValides()) {
                System.out.println("Validation OK"); // Debug

                poubelle nouvellePoubelle = creerPoubelle();
                poubelleService.ajouter(nouvellePoubelle);

                System.out.println("Insertion effectuée"); // Debug
                reinitialiserFormulaire();
                afficherConfirmation();
            }
        } catch (Exception e) {
            System.err.println("Erreur : " + e.getMessage()); // Debug
            afficherAlerte("Erreur", e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    private void afficherConfirmation() {
        confirmationPanel.setVisible(true);
        String adresse = adresseField.getText();
        String message = String.format("Poubelle ajoutée avec succès !\nLa poubelle située à %s\n"
                + "a été enregistrée dans le centre correspondant.", adresse);
        confirmationMessage.setText(message);
    }

    @FXML
    private void fermerConfirmation(ActionEvent event) { // Doit être javafx.event.ActionEvent
        confirmationPanel.setVisible(false);
    }

    private boolean champsValides() {
        // Vérifier les champs vides
        if (idCentreField.getText().isEmpty()) {
            afficherAlerte("Erreur", "L'ID du centre est obligatoire", Alert.AlertType.ERROR);
            return false;
        }
        if (adresseField.getText().isEmpty()) {
            afficherAlerte("Erreur", "L'adresse est obligatoire", Alert.AlertType.ERROR);
            return false;
        }
        if (niveauField.getText().isEmpty()) {
            afficherAlerte("Erreur", "Le niveau est obligatoire", Alert.AlertType.ERROR);
            return false;
        }
        if (dateInstallationPicker.getValue() == null) {
            afficherAlerte("Erreur", "La date d'installation est obligatoire", Alert.AlertType.ERROR);
            return false;
        }
        if (etatComboBox.getValue() == null) {
            afficherAlerte("Erreur", "L'état est obligatoire", Alert.AlertType.ERROR);
            return false;
        }

        // Vérifier le format numérique
        try {
            Integer.parseInt(idCentreField.getText());
        } catch (NumberFormatException e) {
            afficherAlerte("Erreur", "L'ID du centre doit être un nombre valide", Alert.AlertType.ERROR);
            return false;
        }

        try {
            int niveau = Integer.parseInt(niveauField.getText());
            if (niveau < 0 || niveau > 100) {
                afficherAlerte("Erreur", "Le niveau doit être entre 0 et 100%", Alert.AlertType.ERROR);
                return false;
            }
        } catch (NumberFormatException e) {
            afficherAlerte("Erreur", "Le niveau doit être un nombre valide", Alert.AlertType.ERROR);
            return false;
        }

        return true;
    }

    private poubelle creerPoubelle() {
        return new poubelle(
                Integer.parseInt(idCentreField.getText()),
                adresseField.getText(),
                Integer.parseInt(niveauField.getText()),
                etatComboBox.getValue(),
                Date.from(dateInstallationPicker.getValue()
                                .atStartOfDay(ZoneId.systemDefault())
                                .toInstant()
                )
        );
    }

    private void reinitialiserFormulaire() {
        // Réinitialiser tous les champs
        idCentreField.clear();
        adresseField.clear();
        niveauField.clear();
        dateInstallationPicker.setValue(null);
        etatComboBox.getSelectionModel().clearSelection();

        // Remettre le focus sur le premier champ
        idCentreField.requestFocus();
    }

    private void afficherAlerte(String titre, String message, Alert.AlertType type) {
        Alert alerte = new Alert(type);
        alerte.setTitle(titre);
        alerte.setHeaderText(null);
        alerte.setContentText(message);
        alerte.showAndWait();
    }
}
