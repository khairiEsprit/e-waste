package com.example.ewaste.Controllers;

import com.example.ewaste.Entities.Participation;
import com.example.ewaste.Repository.ParticipationRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class ListParticipationController {
    @FXML
    private TableView<Participation> participationTable;

    @FXML
    private TableColumn<Participation, String> firstNameColumn;
    @FXML
    private TableColumn<Participation, String> lastNameColumn;
    @FXML
    private TableColumn<Participation, String> emailColumn;
    @FXML
    private TableColumn<Participation, String> phoneColumn;
    @FXML
    private TableColumn<Participation, String> cityColumn;
    @FXML
    private TableColumn<Participation, String> countryColumn;
    @FXML
    private TableColumn<Participation, String> zipCodeColumn;
    @FXML
    private TableColumn<Participation, Integer> pointsEarnedColumn;// Nouvelle colonne pour les points

    @FXML
    private Button editButton;
    @FXML
    private Button deleteButton;

    @FXML
    private TextField searchField; // Champ de recherche

    @FXML
    private VBox mainContainer; // Conteneur principal

    private final ObservableList<Participation> participationList = FXCollections.observableArrayList();
    private final ParticipationRepository participationRepository = new ParticipationRepository();

    @FXML
    public void initialize() {
        // Configuration des colonnes
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        cityColumn.setCellValueFactory(new PropertyValueFactory<>("city"));
        countryColumn.setCellValueFactory(new PropertyValueFactory<>("country"));
        zipCodeColumn.setCellValueFactory(new PropertyValueFactory<>("zipCode"));
        pointsEarnedColumn.setCellValueFactory(new PropertyValueFactory<>("pointsEarned")); // Ajout de la colonne des points

        // Charger les données
        loadParticipations();
        participationTable.setItems(participationList);

        // Désactiver les boutons par défaut
        editButton.setDisable(true);
        deleteButton.setDisable(true);

        // Ajouter un écouteur de sélection à la TableView
        participationTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
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
            filterParticipations(newValue); // Filtrer les participations en fonction du texte saisi
        });

        // Ajouter un écouteur d'événements pour désélectionner la ligne lors d'un clic en dehors de la TableView
        if (mainContainer != null) {
            mainContainer.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
                // Vérifier si le clic est en dehors de la TableView
                if (!participationTable.getBoundsInParent().contains(event.getX(), event.getY())) {
                    // Désélectionner la ligne sélectionnée
                    participationTable.getSelectionModel().clearSelection();
                }
            });
        } else {
            System.err.println("Erreur : mainContainer est null. Vérifiez le fichier FXML.");
        }
    }

    @FXML
    private void handleRetour() {
        try {
            // Charger le fichier FXML de l'interface ListEvenement
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ewaste/views/ListEvenement-view.fxml"));
            Parent root = loader.load();

            // Obtenir la scène actuelle à partir du bouton Retour
            Stage stage = (Stage) participationTable.getScene().getWindow();

            // Changer la scène pour afficher ListEvenement
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger l'interface ListEvenement.");
        }
    }

    private void filterParticipations(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            // Si le champ de recherche est vide, afficher toutes les participations
            participationTable.setItems(participationList);
        } else {
            // Filtrer les participations en fonction du texte saisi
            ObservableList<Participation> filteredList = FXCollections.observableArrayList();
            for (Participation participation : participationList) {
                if (participation.getFirstName().toLowerCase().contains(searchText.toLowerCase()) ||
                        participation.getLastName().toLowerCase().contains(searchText.toLowerCase())) {
                    filteredList.add(participation);
                }
            }
            participationTable.setItems(filteredList);
        }
    }

    private void loadParticipations() {
        participationList.clear();
        participationList.addAll(participationRepository.getAll());
    }

    @FXML
    private void handleEditAction() {
        Participation selectedParticipation = participationTable.getSelectionModel().getSelectedItem();
        if (selectedParticipation != null) {
            showEditDialog(selectedParticipation);
        } else {
            showAlert("Erreur", "Veuillez sélectionner une participation à modifier.");
        }
    }

    @FXML
    private void handleDeleteAction() {
        Participation selectedParticipation = participationTable.getSelectionModel().getSelectedItem();
        if (selectedParticipation != null) {
            boolean success = participationRepository.delete(selectedParticipation.getId());
            if (success) {
                participationList.remove(selectedParticipation);
                showAlert("Succès", "Participation supprimée avec succès.");
            } else {
                showAlert("Erreur", "Échec de la suppression de la participation.");
            }
        } else {
            showAlert("Erreur", "Veuillez sélectionner une participation à supprimer.");
        }
    }

    private void showEditDialog(Participation participation) {
        // Créer une nouvelle boîte de dialogue
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Modifier la participation");
        dialog.setHeaderText("Modifier les détails de la participation");

        // Appliquer les styles CSS à la boîte de dialogue
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/com/example/ewaste/styles/ListParticipation.css").toExternalForm());
        dialog.getDialogPane().getStyleClass().add("dialog-pane");

        // Créer les champs de texte pour la modification
        TextField firstNameField = new TextField(participation.getFirstName());
        TextField lastNameField = new TextField(participation.getLastName());
        TextField emailField = new TextField(participation.getEmail());
        TextField phoneField = new TextField(participation.getPhone());
        TextField cityField = new TextField(participation.getCity());
        TextField countryField = new TextField(participation.getCountry());
        TextField zipCodeField = new TextField(participation.getZipCode());
        TextField pointsEarnedField = new TextField(String.valueOf(participation.getPointsEarned()));

        // Désactiver le champ des points gagnés
        pointsEarnedField.setEditable(false);

        // Ajouter les champs à la boîte de dialogue
        VBox vbox = new VBox(
                new Label("Prénom:"), firstNameField,
                new Label("Nom:"), lastNameField,
                new Label("Email:"), emailField,
                new Label("Téléphone:"), phoneField,
                new Label("Ville:"), cityField,
                new Label("Pays:"), countryField,
                new Label("Code Postal:"), zipCodeField,
                new Label("Points gagnés:"), pointsEarnedField
        );
        dialog.getDialogPane().setContent(vbox);

        // Ajouter les boutons OK et Annuler
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Gérer le résultat de la boîte de dialogue
        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                participation.setFirstName(firstNameField.getText());
                participation.setLastName(lastNameField.getText());
                participation.setEmail(emailField.getText());
                participation.setPhone(phoneField.getText());
                participation.setCity(cityField.getText());
                participation.setCountry(countryField.getText());
                participation.setZipCode(zipCodeField.getText());
                // Ne pas mettre à jour les points gagnés
                // participation.setPointsEarned(Integer.parseInt(pointsEarnedField.getText()));
                return ButtonType.OK;
            }
            return null;
        });

        // Afficher la boîte de dialogue et attendre la réponse de l'utilisateur
        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                boolean success = participationRepository.updateParticipation(participation);
                if (success) {
                    participationTable.refresh();
                    showAlert("Succès", "Participation mise à jour avec succès.");
                } else {
                    showAlert("Erreur", "Échec de la mise à jour de la participation.");
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