package com.example.ewaste.controllers;
import com.example.ewaste.Models.poubelle;
import com.example.ewaste.repository.PoubelleRepository;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.ResourceBundle;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;


public class ListePoubelleController implements Initializable {
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
  private final  PoubelleRepository pr = new PoubelleRepository();
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

            // Instance du service pour appeler la méthode de suppression

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

                // Action pour le bouton "Supprimer"
                deleteButton.setOnAction(event -> {
                    // Récupérer l'élément actuel (poubelle) associé à la cellule
                    poubelle item = getItem();
                    if (item != null) {
                        try {
                            // Appel du service pour supprimer l'élément de la base de données
                            pr.supprimer(item.getId());

                            // Créer une nouvelle liste observable sans l'élément supprimé
                            ObservableList<poubelle> items = FXCollections.observableArrayList(listPoubelles.getItems());
                            items.remove(item);  // Supprime l'élément de la nouvelle liste

                            // Mettre à jour la ListView avec la nouvelle liste
                            listPoubelles.setItems(items);

                        } catch (SQLException e) {
                            e.printStackTrace();
                            // Gérer les erreurs de suppression
                        }
                    }
                });
            }

            @Override
            public void updateItem(poubelle item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    lblId.setText("ID: " + item.getId());
                    lblAdresse.setText("Adresse: " + item.getAdresse());
                    lblEtat.setText("État: " + item.getEtat());
                    lblDate.setText("Date: " + item.getDate_installation());
                    progressBar.setProgress(item.getNiveau() / 100.0);
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
        });// Mise à jour de la barre de progression (valeur entre 0 et 1)
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
                poubelleList.setAll(pr.recuperer());
            } catch (SQLException e) {
                showAlert("Erreur de chargement", e.getMessage());
            }
        }

    @FXML
    private void handleAdd(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Ajouter_Poubelle.fxml"));
            Parent root = loader.load();

            // N5thou el controller de formulaire
            Ajouter_poubelle_controller formController = loader.getController();
            formController.setMainController(this);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();}

    }
    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    public void refreshList() {
        loadData();
    }


    @FXML
    private void handleHistorique(ActionEvent event) {
        // Récupérer la poubelle sélectionnée
        poubelle selectedPoubelle = listPoubelles.getSelectionModel().getSelectedItem();

        // Vérifier si une poubelle est sélectionnée
        if (selectedPoubelle == null) {
            showAlert("Aucune sélection", "Veuillez sélectionner une poubelle pour afficher son historique.", Alert.AlertType.WARNING);
            return;
        }

        try {
            // Charger l'interface Historique_Poubelle.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Historique_Poubelle.fxml"));
            Parent root = loader.load();

            // n loadi le contrôleur de l'interface Historique_Poubelle
            HistoriquePoubelleController historiqueController = loader.getController();

            // Passer l'ID de la poubelle sélectionnée au contrôleur HistoriquePoubelleController
            historiqueController.setPoubelleId(selectedPoubelle.getId());

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Historique de la Poubelle " + selectedPoubelle.getId());
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Une erreur s'est produite lors de l'ouverture de l'historique.", Alert.AlertType.ERROR);
        }
    }
    @FXML
    private void handleEdit(ActionEvent event) {
        // Get the selected poubelle from the ListView
        poubelle selectedPoubelle = listPoubelles.getSelectionModel().getSelectedItem();

        // Check if a poubelle is selected
        if (selectedPoubelle == null) {
            showAlert("Aucune sélection", "Veuillez sélectionner une poubelle à modifier.", Alert.AlertType.WARNING);
            return;
        }

        try {
            // Load the Modifier_Poubelle.fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Modifier_Poubelle.fxml"));
            Parent root = loader.load();

            // Get the controller for the Modifier_Poubelle form
            Modifier_Poubelle controller = loader.getController();

            // Pass the selected poubelle to the Modifier_Poubelle controller
            controller.setSelectedPoubelle(selectedPoubelle);

            // Create a new stage for the Modifier_Poubelle form
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Modifier la Poubelle");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Une erreur s'est produite lors de l'ouverture du formulaire de modification.", Alert.AlertType.ERROR);
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