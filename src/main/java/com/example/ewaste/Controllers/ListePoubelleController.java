package com.example.ewaste.Controllers;

import com.example.ewaste.Entities.poubelle;
import com.example.ewaste.Repository.PoubelleRepository;
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
    private final PoubelleRepository pr = new PoubelleRepository();

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
            private final ProgressBar progressBar = new ProgressBar();
            private final Label lblEtat = new Label();
            private final Label lblDetails = new Label(); // Ajout pour la date et hauteur
            private final Button deleteButton = new Button("🗑");

            {
                grid.setHgap(10);
                grid.setVgap(5);
                grid.setPadding(new Insets(10));

                progressBar.setPrefWidth(200);

                grid.addColumn(0, lblId, lblAdresse);
                grid.add(progressBar, 1, 0, 1, 2);
                grid.addColumn(2, lblEtat, lblDetails);
                grid.add(deleteButton, 3, 0, 1, 2);

                deleteButton.setOnAction(event -> {
                    poubelle item = getItem();
                    if (item != null) {
                        try {
                            pr.supprimer(item.getId());
                            poubelleList.remove(item);
                        } catch (SQLException e) {
                            showAlert("Erreur SQL", e.getMessage(), Alert.AlertType.ERROR);
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
                   // lblId.setText("ID: " + item.getId());
                    lblAdresse.setText("Adresse: " + item.getAdresse());
                    lblEtat.setText("État: " + item.getEtat());
                    lblDetails.setText("Date: " + item.getDate_installation() + "\nHauteur: " + item.getHauteurTotale() + " cm"); // Ajouté
                    progressBar.setProgress(item.getNiveau() / 100.0);
                    updateProgressStyle(item.getNiveau());
                    setGraphic(grid);
                }
            }

            private void updateProgressStyle(int niveau) {
                String style;
                if (niveau >= 100) {
                    style = "-fx-accent: #F44336;"; // Rouge
                } else if (niveau >= 80) {
                    style = "-fx-accent: #FF9800;"; // Orange
                } else if (niveau >= 50) {
                    style = "-fx-accent: #4CAF50;"; // Vert
                } else {
                    style = "-fx-accent: #8BC34A;"; // Vert clair
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
                        poubelle.getEtat().toString().toLowerCase().contains(lowerCaseFilter) ||
                        String.valueOf(poubelle.getHauteurTotale()).contains(lowerCaseFilter); // Ajouté
            });
        });

        sortedData.comparatorProperty().bind(
                Bindings.createObjectBinding(() -> {
                    String selectedSort = sortComboBox.getValue();
                    if (selectedSort == null) return null;

                    switch (selectedSort) {
                        case "Tri par ID":
                            return Comparator.comparingInt(poubelle::getId);
                        case "Tri par Date":
                            return Comparator.comparing(poubelle::getDate_installation);
                        case "Tri par Niveau":
                            return Comparator.comparingInt(poubelle::getNiveau);
                        case "Tri par État":
                            return Comparator.comparing(p -> p.getEtat().toString());
                        case "Tri par Hauteur": // Ajouté
                            return Comparator.comparingInt(poubelle::getHauteurTotale);
                        default:
                            return null;
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
                "Tri par État",
                "Tri par Hauteur" // Ajouté
        );

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
                case "Tri par Hauteur": // Ajouté
                    comparator = Comparator.comparingInt(poubelle::getHauteurTotale);
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
            showAlert("Erreur de chargement", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleAdd(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ewaste/views/Ajouter_Poubelle.fxml"));
            Parent root = loader.load();
            Ajouter_poubelle_controller formController = loader.getController();
            formController.setMainController(this);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEdit(ActionEvent event) {
        poubelle selectedPoubelle = listPoubelles.getSelectionModel().getSelectedItem();

        if (selectedPoubelle == null) {
            showAlert("Aucune sélection", "Veuillez sélectionner une poubelle à modifier.", Alert.AlertType.WARNING);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ewaste/views/Modifier_Poubelle.fxml"));
            Parent root = loader.load();
            Modifier_Poubelle controller = loader.getController();
            controller.setSelectedPoubelle(selectedPoubelle);

            Stage stage = new Stage();
            stage.setOnHidden(e -> loadData()); // Rafraîchir après fermeture
            stage.setScene(new Scene(root));
            stage.setTitle("Modifier la Poubelle");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de l'ouverture du formulaire.", Alert.AlertType.ERROR);
        }
    }
    @FXML
    private void handleHistorique(ActionEvent event) {
        poubelle selectedPoubelle = listPoubelles.getSelectionModel().getSelectedItem();

        if (selectedPoubelle == null) {
            showAlert("Aucune sélection", "Veuillez sélectionner une poubelle pour afficher son historique.", Alert.AlertType.WARNING);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ewaste/views/Historique_Poubelle.fxml"));
            Parent root = loader.load();

            HistoriquePoubelleController historiqueController = loader.getController();
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
}