package com.example.ewaste.controllers;

import com.example.ewaste.Models.etat;
import com.example.ewaste.Models.poubelle;
import com.example.ewaste.repository.PoubelleRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;

import java.net.URL;
import java.sql.SQLException;
import java.util.Date;
import java.util.ResourceBundle;

public class liste_poubelle implements Initializable {

    @FXML
    private TableView<poubelle> tablePoubelles;

    @FXML
    private TableColumn<poubelle, Integer> colId;

    @FXML
    private TableColumn<poubelle, String> colAdresse;

    @FXML
    private TableColumn<poubelle, Integer> colNiveau;

    @FXML
    private TableColumn<poubelle, etat> colEtat;

    @FXML
    private TableColumn<poubelle, Date> colDate;

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

    @FXML
    private AnchorPane rootPane; // Ensure this is the root node of your FXML

    private ObservableList<poubelle> poubelleList = FXCollections.observableArrayList();
    private FilteredList<poubelle> filteredData;
    private SortedList<poubelle> sortedData;
    private PoubelleRepository service = new PoubelleRepository();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTable();
        setupSearch();
        setupSorting();
        setupThemeToggle();
        loadData();
    }

    private void setupTable() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colAdresse.setCellValueFactory(new PropertyValueFactory<>("adresse"));
        colNiveau.setCellValueFactory(new PropertyValueFactory<>("niveau"));
        colEtat.setCellValueFactory(new PropertyValueFactory<>("etat"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date_installation"));

        // Formatage des cellules
        colNiveau.setCellFactory(column -> new TableCell<poubelle, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item + "%");
            }
        });

        colEtat.setCellFactory(column -> new TableCell<poubelle, etat>() {
            @Override
            protected void updateItem(etat item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText("");
                    setStyle("");
                } else {
                    setText(item.getLabel());
                    setStyle("-fx-text-fill: " + (item == etat.FONCTIONNEL ? "green" : "red"));
                }
            }
        });

        TableColumn<poubelle, Void> colAction = new TableColumn<>("Action");
        colAction.setCellFactory(createActionCellFactory());
        tablePoubelles.getColumns().add(colAction);
    }

    private Callback<TableColumn<poubelle, Void>, TableCell<poubelle, Void>> createActionCellFactory() {
        return param -> new TableCell<poubelle, Void>() {
            private final Button deleteButton = new Button("🗑");
            {
                deleteButton.setOnAction(event -> {
                    poubelle p = getTableView().getItems().get(getIndex());
                    try {
                        service.supprimer(p.getId());
                        poubelleList.remove(p); // Remove the item from the observable list
                    } catch (SQLException e) {
                        showAlert("Erreur de suppression", e.getMessage());
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                }
            }
        };
    }

    private void setupSearch() {
        filteredData = new FilteredList<>(poubelleList, p -> true);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(poubelle -> {
                if (newValue == null || newValue.isEmpty()) return true;

                String lowerCaseFilter = newValue.toLowerCase();
                return poubelle.getAdresse().toLowerCase().contains(lowerCaseFilter) ||
                        String.valueOf(poubelle.getId()).contains(lowerCaseFilter) ||
                        poubelle.getEtat().toString().toLowerCase().contains(lowerCaseFilter);
            });
        });

        sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tablePoubelles.comparatorProperty());
        tablePoubelles.setItems(sortedData);
    }

    private void setupSorting() {
        sortComboBox.getItems().addAll(
                "Tri par ID",
                "Tri par Date",
                "Tri par Niveau",
                "Tri par État"
        );

        sortComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            switch (newVal) {
                case "Tri par ID":
                    tablePoubelles.getSortOrder().setAll(colId);
                    break;
                case "Tri par Date":
                    tablePoubelles.getSortOrder().setAll(colDate);
                    break;
                case "Tri par Niveau":
                    tablePoubelles.getSortOrder().setAll(colNiveau);
                    break;
                case "Tri par État":
                    tablePoubelles.getSortOrder().setAll(colEtat);
                    break;
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
        rootPane.setStyle("-fx-base: #2b2b2b;"); // Apply to the root node
    }

    private void setLightTheme() {
        rootPane.setStyle("-fx-base: #f4f4f4;"); // Apply to the root node
    }

    private void loadData() {
        try {
            poubelleList.setAll(service.recuperer());
        } catch (SQLException e) {
            showAlert("Erreur de chargement", e.getMessage());
        }
    }

    @FXML
    private void handleAdd() {
        // Refresh the data and reapply search, sorting, and theme toggle
        loadData();
        setupSearch();
        setupSorting();
        setupThemeToggle();
    }

    @FXML
    private void handleEdit() {
        // Refresh the data and reapply search, sorting, and theme toggle
        loadData();
        setupSearch();
        setupSorting();
        setupThemeToggle();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}