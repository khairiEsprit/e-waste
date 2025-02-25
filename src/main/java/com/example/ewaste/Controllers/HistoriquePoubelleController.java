package com.example.ewaste.Controllers;
import com.example.ewaste.Entities.Historique_Poubelle;
import com.example.ewaste.Entities.type;
import com.example.ewaste.Repository.HistoriquePoubelleRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;

public class HistoriquePoubelleController {

    @FXML
    private TextField poubelleIdField;

    @FXML
    private TextField quantiteDechetsField;

    @FXML
    private Label statusLabel;

    @FXML
    private TableView<Historique_Poubelle> historiqueTable;

    @FXML
    private TableColumn<Historique_Poubelle, Integer> idColumn;

    @FXML
    private TableColumn<Historique_Poubelle, Integer> idPoubelleColumn;

    @FXML
    private TableColumn<Historique_Poubelle, Date> dateEvenementColumn;

    @FXML
    private TableColumn<Historique_Poubelle, type> typeEvenementColumn;

    @FXML
    private TableColumn<Historique_Poubelle, String> descriptionColumn;

    @FXML
    private TableColumn<Historique_Poubelle, Float> quantiteDechetsColumn;

    @FXML
    private ComboBox<type> typeFilterComboBox;

    private HistoriquePoubelleRepository historiquePoubelleRepository = new HistoriquePoubelleRepository();
    private int poubelleId;

    @FXML
    public void initialize() {
        // Vérification de toutes les colonnes
        if (historiqueTable == null || idColumn == null || idPoubelleColumn == null
                || dateEvenementColumn == null || typeEvenementColumn == null
                || descriptionColumn == null || quantiteDechetsColumn == null) {
            throw new IllegalStateException("Éléments FXML manquants ! Vérifiez les IDs dans le FXML");
        }

        // Configuration des colonnes
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idPoubelleColumn.setCellValueFactory(new PropertyValueFactory<>("id_poubelle"));
        dateEvenementColumn.setCellValueFactory(new PropertyValueFactory<>("date_evenement"));
        typeEvenementColumn.setCellValueFactory(new PropertyValueFactory<>("type_evenement"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        quantiteDechetsColumn.setCellValueFactory(new PropertyValueFactory<>("quantite_dechets"));

        // Configuration du ComboBox
        typeFilterComboBox.getItems().addAll(type.values());
        typeFilterComboBox.getItems().add(0, null);
        typeFilterComboBox.setButtonCell(new ListCell<type>() {
            @Override
            protected void updateItem(type item, boolean empty) {
                super.updateItem(item, empty);
                setText(item == null ? "Tous les types" : item.toString());
            }
        });

        // Charger l'historique initial (si nécessaire)
        loadHistorique();
    }

    public void refreshTable() {

    }
    private void configureColumn(TableColumn<?, ?> column, String property) {
        if (column != null) {
            column.setCellValueFactory(new PropertyValueFactory<>(property));
        } else {
            System.err.println("Colonne " + property + " non initialisée !");
        }
    }
    private void setupValidation() {
        // Validation numérique
        UnaryOperator<TextFormatter.Change> numberFilter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d*(\\.\\d*)?")) {
                return change;
            }
            return null;
        };

        poubelleIdField.setTextFormatter(new TextFormatter<>(numberFilter));
        quantiteDechetsField.setTextFormatter(new TextFormatter<>(numberFilter));

        // Style dynamique pour les erreurs
        poubelleIdField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.isEmpty()) {
                try {
                    boolean exists = historiquePoubelleRepository.existePoubelle(Integer.parseInt(newVal));
                    poubelleIdField.pseudoClassStateChanged(PseudoClass.getPseudoClass("error"), !exists);
                } catch (NumberFormatException e) {
                    poubelleIdField.pseudoClassStateChanged(PseudoClass.getPseudoClass("error"), true);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
    @FXML
    public void handleRemplissageEvent() {
        int idPoubelle = Integer.parseInt(poubelleIdField.getText());
        float quantiteDechets = Float.parseFloat(quantiteDechetsField.getText());

        Historique_Poubelle historique = new Historique_Poubelle(
                idPoubelle,
                new Date(),
                type.REMPLISSAGE,
                "Remplissage détecté",
                quantiteDechets
        );

        try {
            historiquePoubelleRepository.ajouter(historique);
            statusLabel.setText("Événement de remplissage enregistré avec succès.");
        } catch (SQLException e) {
            statusLabel.setText("Erreur lors de l'enregistrement de l'événement.");
            e.printStackTrace();
        }
    }
    @FXML
    public void handleVidageEvent() {
        int idPoubelle = Integer.parseInt(poubelleIdField.getText());
        float quantiteDechets = Float.parseFloat(quantiteDechetsField.getText());

        Historique_Poubelle historique = new Historique_Poubelle(
                idPoubelle,
                new Date(),
                type.VIDAGE,
                "Vidage détecté",
                quantiteDechets
        );

        try {
            historiquePoubelleRepository.ajouter(historique);
            statusLabel.setText("Événement de vidage enregistré avec succès.");
        } catch (SQLException e) {
            statusLabel.setText("Erreur lors de l'enregistrement de l'événement.");
            e.printStackTrace();
        }
    }

    @FXML
    public void handlePanneEvent() {
        int idPoubelle = Integer.parseInt(poubelleIdField.getText());

        Historique_Poubelle historique = new Historique_Poubelle(
                idPoubelle,
                new Date(),
                type.PANNE,
                "Panne détectée",
                0
        );

        try {
            historiquePoubelleRepository.ajouter(historique);
            statusLabel.setText("Événement de panne enregistré avec succès.");
        } catch (SQLException e) {
            statusLabel.setText("Erreur lors de l'enregistrement de l'événement.");
            e.printStackTrace();
        }
    }
    @FXML
    public void handleReparationEvent() {
        int idPoubelle = Integer.parseInt(poubelleIdField.getText());

        Historique_Poubelle historique = new Historique_Poubelle(
                idPoubelle,
                new Date(),
                type.REPARATION,
                "Réparation effectuée",
                0
        );

        try {
            historiquePoubelleRepository.ajouter(historique);
            statusLabel.setText("Événement de réparation enregistré avec succès.");
        } catch (SQLException e) {
            statusLabel.setText("Erreur lors de l'enregistrement de l'événement.");
            e.printStackTrace();
        }
    }
    @FXML
    public void handleAlerteRemplissageMax() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Alerte de Remplissage");
        alert.setHeaderText(null);
        alert.setContentText("La poubelle a atteint son seuil maximal de remplissage !");
        alert.showAndWait();
    }
    @FXML
    public void handleAfficherHistorique() {
        try {
            int idPoubelle = Integer.parseInt(poubelleIdField.getText());

            // Vérifier si la poubelle existe
            if (!historiquePoubelleRepository.existePoubelle(idPoubelle)) {
                statusLabel.setText("Erreur : La poubelle avec l'ID " + idPoubelle + " n'existe pas.");
                return;
            }

            // Récupérer et afficher l'historique
            List<Historique_Poubelle> historiqueList = historiquePoubelleRepository.recupererParPoubelle(idPoubelle);
            ObservableList<Historique_Poubelle> observableList = FXCollections.observableArrayList(historiqueList);
            historiqueTable.setItems(observableList);
            statusLabel.setText("Historique chargé avec succès.");
        } catch (NumberFormatException e) {
            statusLabel.setText("Erreur : L'ID de la poubelle doit être un nombre.");
        } catch (SQLException e) {
            statusLabel.setText("Erreur lors du chargement de l'historique.");
            e.printStackTrace();
        }
    }
    private void appliquerFiltre() {
        try {
            int idPoubelle = Integer.parseInt(poubelleIdField.getText());
            type typeFiltre = typeFilterComboBox.getValue();

            List<Historique_Poubelle> historiqueList;
            if (typeFiltre == null) {
                // Afficher tous les événements si aucun filtre n'est sélectionné
                historiqueList = historiquePoubelleRepository.recupererParPoubelle(idPoubelle);
            } else {
                // Filtrer par type d'événement
                historiqueList = historiquePoubelleRepository.recupererParPoubelleEtType(idPoubelle, typeFiltre);
            }

            ObservableList<Historique_Poubelle> observableList = FXCollections.observableArrayList(historiqueList);
            historiqueTable.setItems(observableList);
            statusLabel.setText("Filtre appliqué avec succès.");
        } catch (SQLException e) {
            statusLabel.setText("Erreur lors de l'application du filtre.");
            e.printStackTrace();
        }
    }


    @FXML
    public void handleExportCSV() {
        try {
            int idPoubelle = Integer.parseInt(poubelleIdField.getText());
            List<Historique_Poubelle> historiqueList = historiquePoubelleRepository.recupererParPoubelle(idPoubelle);

            // Créer un fichier CSV
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Exporter en CSV");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers CSV", "*.csv"));
            File file = fileChooser.showSaveDialog(null);

            if (file != null) {
                try (PrintWriter writer = new PrintWriter(file)) {
                    writer.println("ID" +
                            ",ID Poubelle,Date Événement,Type Événement,Description,Quantité Déchets (kg)");
                    for (Historique_Poubelle h : historiqueList) {
                        writer.println(h.getId() + "," + h.getId_poubelle() + "," + h.getDate_evenement() + "," +
                                h.getType_evenement() + "," + h.getDescription() + "," + h.getQuantite_dechets());
                    }
                    statusLabel.setText("Export CSV réussi : " + file.getAbsolutePath());
                }
            }
        } catch (Exception e) {
            statusLabel.setText("Erreur lors de l'export CSV.");
            e.printStackTrace();
        }
    }
    public void setPoubelleId(int poubelleId) {
        this.poubelleId = poubelleId;
        loadHistorique(); // Charger l'historique de la poubelle
    }

    private void loadHistorique() {
        try {
            List<Historique_Poubelle> historiqueList = historiquePoubelleRepository.recupererParPoubelle(poubelleId);
            ObservableList<Historique_Poubelle> observableList = FXCollections.observableArrayList(historiqueList);
            historiqueTable.setItems(observableList);
            statusLabel.setText("Historique chargé avec succès.");
        } catch (SQLException e) {
            statusLabel.setText("Erreur lors du chargement de l'historique.");
            e.printStackTrace();
        }
    }
    @FXML
    private void handleSupprimer(ActionEvent event) {
        // Récupérer l'élément sélectionné dans la liste
        Historique_Poubelle selectedItem = historiqueTable.getSelectionModel().getSelectedItem();

        if (selectedItem != null) {
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Confirmation de suppression");
            confirmation.setHeaderText("Voulez-vous vraiment supprimer cet historique ?");
            confirmation.setContentText("Cette action est irréversible.");

            Optional<ButtonType> result = confirmation.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    // Appel du service pour supprimer de la base de données
                    historiquePoubelleRepository.supprimer(selectedItem.getId());

                    // Mise à jour de la TableView
                    historiqueTable.getItems().remove(selectedItem);

                    // Affichage d'un message de succès
                    Alert success = new Alert(Alert.AlertType.INFORMATION);
                    success.setTitle("Suppression réussie");
                    success.setHeaderText(null);
                    success.setContentText("L'historique a été supprimé avec succès.");
                    success.show();

                } catch (SQLException e) {
                    e.printStackTrace();
                    // Affichage d'une erreur si la suppression échoue
                    Alert error = new Alert(Alert.AlertType.ERROR);
                    error.setTitle("Erreur");
                    error.setHeaderText("Échec de la suppression");
                    error.setContentText("Une erreur est survenue lors de la suppression.");
                    error.show();
                }
            }
        } else {
            // Si aucun élément n'est sélectionné
            Alert warning = new Alert(Alert.AlertType.WARNING);
            warning.setTitle("Aucune sélection");
            warning.setHeaderText(null);
            warning.setContentText("Veuillez sélectionner un élément à supprimer.");
            warning.show();
        }
    }

}