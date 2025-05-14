package com.example.ewaste.Controllers;

import com.example.ewaste.Entities.ToastManager;
import com.example.ewaste.Entities.poubelle;
import com.example.ewaste.Repository.CapteurRepository;
import com.example.ewaste.Repository.PoubelleRepository;
import javafx.animation.AnimationTimer;
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
import java.util.Date;
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

    @FXML
    private Pagination pagination;
    @FXML
    private Label statusLabel;

    private ObservableList<poubelle> poubelleList = FXCollections.observableArrayList();
    private final PoubelleRepository pr = new PoubelleRepository();
    private final CapteurRepository cr = new CapteurRepository();

    private static final int ITEMS_PER_PAGE = 10;    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupList();
        setupSorting();
        setupSearch();
        setupThemeToggle();
        loadData();

        // Initialize status label with empty text
        if (statusLabel != null) {
            statusLabel.setText("");
        }

        AnimationTimer timer = new AnimationTimer() {
            private long lastUpdate = 0;            @Override
            public void handle(long now) {
                if (now - lastUpdate >= 5_000_000_000L) {
                    try {
                        cr.simulerMesureNiveauRemplissage();
                        Platform.runLater(() -> statusLabel.setText("Mesures mises à jour avec succès"));

                        for (poubelle p : listPoubelles.getItems()) {
                            if (p.getNiveau() >= 75) {
                                String message = String.format(
                                        "[ALERTE] Poubelle #%d - Niveau critique: %d%%\nAdresse: %s\nDate: %s",
                                        p.getId(),
                                        p.getNiveau(),
                                        p.getAdresse(),
                                        new Date()
                                );

                                // SMS functionality disabled for now
                                // String messageSid = TwilioSMSUtil.sendSMS("+21628346062", message);

                                System.out.println("Alerte pour la poubelle #" + p.getId() + ": " + message);
                            }
                        }

                        refreshList();
                    } catch (SQLException e) {
                        e.printStackTrace();
                        Platform.runLater(() ->
                                statusLabel.setText("Erreur lors de la mise à jour des niveaux"));
                    } catch (Exception e) {
                        Platform.runLater(() ->
                                statusLabel.setText("Échec d'envoi SMS: " + e.getMessage()));
                    }
                    lastUpdate = now;
                }
            }
        };
        timer.start();

        Platform.runLater(() -> {
            Stage stage = (Stage) listPoubelles.getScene().getWindow();
            stage.setMinWidth(1000);
            stage.setMinHeight(700);
            stage.setWidth(1200);
            stage.setHeight(800);
        });
    }

    private String getEtatDisplayName(com.example.ewaste.Entities.etat etat) {
        if (etat == null) return "Inconnu";
        switch (etat) {
            case FONCTIONNEL: return "Fonctionnel";
            case NON_FONCTIONNEL: return "Non Fonctionnel";
            case EN_REPARATION: return "En Réparation";
            case HORS_SERVICE: return "Hors Service";
            default: return "Inconnu";
        }
    }

    private void setupList() {
        listPoubelles.setCellFactory(param -> new ListCell<poubelle>() {
            private final GridPane grid = new GridPane();
            private final Label lblId = new Label();
            private final Label lblAdresse = new Label();
            private final Label lblCentre = new Label();
            private final ProgressBar progressBar = new ProgressBar();
            private final Label lblEtat = new Label();
            private final Label lblDetails = new Label();
            private final Label lblPosition = new Label();
            private final Label lblRevenu = new Label();
            private final Button deleteButton = new Button("🗑");

            {
                grid.setHgap(10);
                grid.setVgap(5);
                grid.setPadding(new Insets(10));
                progressBar.setPrefWidth(150);

                grid.addColumn(0, lblId, lblAdresse, lblCentre);
                grid.add(progressBar, 1, 0, 1, 2);
                grid.addColumn(2, lblEtat, lblDetails, lblPosition, lblRevenu);
                grid.add(deleteButton, 3, 0, 1, 4);

                deleteButton.setOnAction(event -> {
                    poubelle item = getItem();
                    if (item != null) {
                        Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
                        confirmationDialog.setTitle("Confirmer la suppression");
                        confirmationDialog.setHeaderText("Êtes-vous sûr de vouloir supprimer cette poubelle ?");
                        confirmationDialog.setContentText("Cette action supprimera également les capteurs associés.");

                        confirmationDialog.showAndWait().ifPresent(response -> {
                            if (response == ButtonType.OK) {
                                try {
                                    pr.supprimer(item.getId());
                                    poubelleList.remove(item);
                                    Stage ownerStage = (Stage) listPoubelles.getScene().getWindow();
                                    ToastManager.showToast(ownerStage, "Poubelle et capteur supprimés avec succès", "toast-success");
                                } catch (SQLException e) {
                                    showAlert("Erreur SQL", e.getMessage(), Alert.AlertType.ERROR);
                                }
                            }
                        });
                    }
                });
            }

            @Override
            protected void updateItem(poubelle item, boolean empty) {
                super.updateItem(item, empty);                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    lblId.setVisible(false); // Cache l'ID
                    lblAdresse.setText("Adresse: " + item.getAdresse());
                    lblCentre.setText("Centre: " + item.getNomCentre());
                    lblEtat.setText("État: " + getEtatDisplayName(item.getEtat()));
                    lblDetails.setText(String.format("Installation: %s\nHauteur: %d cm",
                            item.getDate_installation(),
                            item.getHauteurTotale()));  // Correction ici
                    lblPosition.setText(String.format("Position: %.4f, %.4f",
                            item.getLatitude(),
                            item.getLongitude()));
                    lblRevenu.setText(String.format("Revenu: %.2f €",
                            item.getRevenu_genere()));

                    progressBar.setProgress(item.getNiveau() / 100.0);
                    updateProgressStyle(item.getNiveau());
                    setGraphic(grid);
                }
            }

            private void updateProgressStyle(int niveau) {
                String style;
                if (niveau >= 100) {
                    style = "-fx-accent: #F44336;";
                } else if (niveau >= 80) {
                    style = "-fx-accent: #FF9800;";
                } else if (niveau >= 50) {
                    style = "-fx-accent: #4CAF50;";
                } else {
                    style = "-fx-accent: #8BC34A;";
                }
                progressBar.setStyle(style);
            }
        });
    }

    private void setupSearch() {
        FilteredList<poubelle> filteredData = new FilteredList<>(poubelleList, p -> true);
        SortedList<poubelle> sortedData = new SortedList<>(filteredData);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {            filteredData.setPredicate(poubelle -> {
                if (newValue == null || newValue.isEmpty()) return true;
                String lowerCaseFilter = newValue.toLowerCase();
                return poubelle.getAdresse().toLowerCase().contains(lowerCaseFilter) ||
                       poubelle.getNomCentre().toLowerCase().contains(lowerCaseFilter) ||
                       String.valueOf(poubelle.getNiveau()).contains(lowerCaseFilter) ||
                       getEtatDisplayName(poubelle.getEtat()).toLowerCase().contains(lowerCaseFilter);
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
                            return Comparator.comparing(p -> getEtatDisplayName(p.getEtat()));
                        case "Tri par Hauteur":
                            return Comparator.comparingInt(poubelle::getHauteurTotale);  // Correction ici
                        case "Tri par Revenu":
                            return Comparator.comparingDouble(poubelle::getRevenu_genere);
                        case "Tri par Centre":
                            return Comparator.comparingInt(poubelle::getId_centre);  // Correction ici
                        case "Tri par Position":
                            return Comparator.comparingDouble(poubelle::getLatitude)
                                              .thenComparingDouble(poubelle::getLongitude);
                        default:
                            return null;
                    }
                }, sortComboBox.valueProperty())
        );

        listPoubelles.setItems(sortedData);
    }

    private void setupSorting() {
        sortComboBox.getItems().setAll(
            "Tri par ID",
            "Tri par Date",
            "Tri par Niveau",
            "Tri par État",
            "Tri par Hauteur",
            "Tri par Revenu",
            "Tri par Centre",
            "Tri par Position"
        );

        sortComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                Comparator<poubelle> comparator = switch (newVal) {
                    case "Tri par ID" -> Comparator.comparingInt(poubelle::getId);
                    case "Tri par Date" -> Comparator.comparing(poubelle::getDate_installation);
                    case "Tri par Niveau" -> Comparator.comparingInt(poubelle::getNiveau);
                    case "Tri par État" -> Comparator.comparing(p -> getEtatDisplayName(p.getEtat()));
                    case "Tri par Hauteur" -> Comparator.comparingInt(poubelle::getHauteurTotale);  // Correction ici
                    case "Tri par Revenu" -> Comparator.comparingDouble(poubelle::getRevenu_genere);
                    case "Tri par Centre" -> Comparator.comparingInt(poubelle::getId_centre);  // Correction ici
                    case "Tri par Position" -> Comparator.comparingDouble(poubelle::getLatitude)
                                                      .thenComparingDouble(poubelle::getLongitude);
                    default -> null;
                };

                if (comparator != null) {
                    FXCollections.sort(poubelleList, comparator);
                }
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
            stage.showAndWait();

            refreshList();
            ToastManager.showToast((Stage) listPoubelles.getScene().getWindow(), "Poubelle ajoutée avec succès", "toast-success");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de l'ouverture du formulaire.", Alert.AlertType.ERROR);
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
            stage.setOnHidden(e -> {
                refreshList();
                ToastManager.showToast((Stage) listPoubelles.getScene().getWindow(), "Poubelle modifiée avec succès", "toast-success");
            });
            stage.setScene(new Scene(root));
            stage.setTitle("Modifier la Poubelle");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de l'ouverture du formulaire.", Alert.AlertType.ERROR);
        }
    }

    public void refreshList() {
        loadData();
    }

    @FXML
    private void handleHistorique(ActionEvent event) {
        poubelle selectedPoubelle = listPoubelles.getSelectionModel().getSelectedItem();

        if (selectedPoubelle == null) {
            showAlert("Aucune sélection", "Veuillez sélectionner une poubelle pour afficher son historique.", Alert.AlertType.WARNING);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ewaste/views/Historique_poubelle.fxml"));
            Parent root = loader.load();

            HistoriquePoubelleController historiqueController = loader.getController();
            historiqueController.setPoubelleId(selectedPoubelle.getId());
            historiqueController.setSelectedPoubelle(selectedPoubelle);

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
}