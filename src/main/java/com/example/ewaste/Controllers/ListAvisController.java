package com.example.ewaste.Controllers;

import com.example.ewaste.Entities.Avis;
import com.example.ewaste.Repository.AvisRepository;
import com.example.ewaste.Utils.DataBaseConn;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.sql.Connection;
import java.sql.SQLException;

public class ListAvisController {
    @FXML
    private TableView<Avis> avisTable;

    @FXML
    private TableColumn<Avis, String> nameColumn;
    @FXML
    private TableColumn<Avis, String> descriptionColumn;
    @FXML
    private TableColumn<Avis, Integer> ratingColumn;

    @FXML
    private Button editButton;
    @FXML
    private Button deleteButton;

    @FXML
    private TextField searchField; // Champ de recherche

    @FXML
    private VBox mainContainer; // Conteneur principal

    private final ObservableList<Avis> avisList = FXCollections.observableArrayList();
    private final AvisRepository avisRepository;

    public ListAvisController() {
        // Obtenir la connexion à la base de données
        Connection conn = DataBaseConn.getInstance().getConnection();
        // Initialiser AvisRepository avec la connexion
        this.avisRepository = new AvisRepository(conn);
    }

    @FXML
    public void initialize() {
        // Configuration des colonnes
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        ratingColumn.setCellValueFactory(new PropertyValueFactory<>("rating"));

        // Charger les données
        loadAvis();
        avisTable.setItems(avisList);

        // Désactiver les boutons par défaut
        editButton.setDisable(true);
        deleteButton.setDisable(true);

        // Ajouter un écouteur de sélection à la TableView
        avisTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                // Activer les boutons si une ligne est sélectionnée
                editButton.setDisable(false);
                deleteButton.setDisable(false);
            } else {
                // Désactiver les boutons si aucune ligne n'est sélectionnée
                editButton.setDisable(true);
                deleteButton.setDisable(true);
            }
        });

        // Ajouter un écouteur de texte pour la recherche
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterAvis(newValue); // Filtrer les avis en fonction du texte saisi
        });

        // Vérifier que mainContainer n'est pas null avant d'ajouter l'écouteur
        if (mainContainer != null) {
            mainContainer.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
                // Vérifier si le clic est en dehors de la TableView
                if (!avisTable.getBoundsInParent().contains(event.getX(), event.getY())) {
                    // Désélectionner la ligne sélectionnée
                    avisTable.getSelectionModel().clearSelection();
                }
            });
        } else {
            System.err.println("Erreur : mainContainer est null. Vérifiez le fichier FXML.");
        }
    }

    private void filterAvis(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            // Si le champ de recherche est vide, afficher tous les avis
            avisTable.setItems(avisList);
        } else {
            // Filtrer les avis en fonction du texte saisi
            ObservableList<Avis> filteredList = FXCollections.observableArrayList();
            for (Avis avis : avisList) {
                if (avis.getName().toLowerCase().contains(searchText.toLowerCase()) ||
                        avis.getDescription().toLowerCase().contains(searchText.toLowerCase())) {
                    filteredList.add(avis);
                }
            }
            avisTable.setItems(filteredList);
        }
    }

    private void loadAvis() {
        avisList.clear();
        avisList.addAll(avisRepository.readAll());
    }

    @FXML
    private void handleEditAction() {
        Avis selectedAvis = avisTable.getSelectionModel().getSelectedItem();
        if (selectedAvis != null) {
            showEditDialog(selectedAvis);
        } else {
            showAlert("Erreur", "Veuillez sélectionner un avis à modifier.");
        }
    }

    @FXML
    private void handleDeleteAction() {
        Avis selectedAvis = avisTable.getSelectionModel().getSelectedItem();
        if (selectedAvis != null) {
            try {
                boolean success = avisRepository.delete(selectedAvis.getId());
                if (success) {
                    avisList.remove(selectedAvis);
                    showAlert("Succès", "Avis supprimé avec succès.");
                } else {
                    showAlert("Erreur", "Échec de la suppression de l'avis.");
                }
            } catch (SQLException e) {
                showAlert("Erreur", "Une erreur s'est produite lors de la suppression de l'avis : " + e.getMessage());
            }
        } else {
            showAlert("Erreur", "Veuillez sélectionner un avis à supprimer.");
        }
    }

    private void showEditDialog(Avis avis) {
        // Créer une nouvelle boîte de dialogue
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Modifier Avis");
        dialog.setHeaderText("Modifier les détails de l'avis");

        // Créer les champs de texte pour la modification
        TextField nameField = new TextField(avis.getName());
        TextField descriptionField = new TextField(avis.getDescription());
        TextField ratingField = new TextField(String.valueOf(avis.getRating()));

        // Ajouter les champs à la boîte de dialogue
        VBox vbox = new VBox(
                new Label("Nom:"), nameField,
                new Label("Description:"), descriptionField,
                new Label("Note (1-5):"), ratingField
        );
        dialog.getDialogPane().setContent(vbox);

        // Ajouter les boutons OK et Annuler
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Gérer le résultat de la boîte de dialogue
        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                avis.setName(nameField.getText());
                avis.setDescription(descriptionField.getText());
                avis.setRating(Integer.parseInt(ratingField.getText()));
                return ButtonType.OK;
            }
            return null;
        });

        // Afficher la boîte de dialogue et attendre la réponse de l'utilisateur
        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    boolean success = avisRepository.update(avis);
                    if (success) {
                        avisTable.refresh();
                        showAlert("Succès", "Avis modifié avec succès.");
                    } else {
                        showAlert("Erreur", "Échec de la modification de l'avis.");
                    }
                } catch (SQLException e) {
                    showAlert("Erreur", "Une erreur s'est produite lors de la modification de l'avis : " + e.getMessage());
                }
            }
        });
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}