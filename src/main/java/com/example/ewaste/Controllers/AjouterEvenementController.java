package com.example.ewaste.Controllers;

import com.example.ewaste.Entities.Event;
import com.example.ewaste.Repository.EventRepository;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.time.LocalDate;

public class AjouterEvenementController {

    @FXML
    private TextField titreField;

    @FXML
    private TextArea descriptionField;

    @FXML
    private DatePicker dateField;

    @FXML
    private TextField lieuField;

    @FXML
    private TextField placesField;

    @FXML
    private Button imageButton;

    @FXML
    private Button ajouterButton;

    @FXML
    private Button modifierButton;

    @FXML
    private Button supprimerButton;

    @FXML
    private TableView<Event> eventTable;

    @FXML
    private TableColumn<Event, String> titreColumn;

    @FXML
    private TableColumn<Event, String> descriptionColumn;

    @FXML
    private TableColumn<Event, LocalDate> dateColumn;

    @FXML
    private TableColumn<Event, String> lieuColumn;

    @FXML
    private TableColumn<Event, Integer> placesColumn;

    @FXML
    private VBox mainContainer; // Conteneur principal

    private File imageFile;
    private final EventRepository eventRepository = new EventRepository();
    private Event selectedEvent;

    @FXML
    public void initialize() {
        // Configurer les colonnes de la TableView
        titreColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        lieuColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        placesColumn.setCellValueFactory(new PropertyValueFactory<>("remainingPlaces"));

        // Charger les événements dans la TableView
        eventTable.getItems().addAll(eventRepository.getEvents());

        // Activer/désactiver les boutons en fonction de la sélection
        eventTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                // Un événement est sélectionné
                selectedEvent = newSelection;
                modifierButton.setDisable(false);
                supprimerButton.setDisable(false);
            } else {
                // Aucun événement n'est sélectionné
                selectedEvent = null;
                modifierButton.setDisable(true);
                supprimerButton.setDisable(true);
            }
        });

        // Ajouter un écouteur d'événements pour désélectionner la ligne lors d'un clic en dehors de la TableView
        if (mainContainer != null) {
            mainContainer.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
                // Vérifier si le clic est en dehors de la TableView
                if (!eventTable.getBoundsInParent().contains(event.getX(), event.getY())) {
                    // Désélectionner la ligne sélectionnée
                    eventTable.getSelectionModel().clearSelection();
                }
            });
        } else {
            System.err.println("Erreur : mainContainer est null. Vérifiez le fichier FXML.");
        }
    }

    @FXML
    private void handleImageButtonAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );
        imageFile = fileChooser.showOpenDialog(new Stage());
        if (imageFile != null) {
            imageButton.setText(imageFile.getName());
        }
    }

    @FXML
    private void handleAjouterButtonAction() {
        if (ajouterButton.getText().equals("Ajouter")) {
            // Ajouter un nouvel événement
            Event event = new Event(
                    0, // ID sera généré par la base de données
                    titreField.getText(),
                    descriptionField.getText(),
                    imageFile != null ? imageFile.toURI().toString() : null,
                    Integer.parseInt(placesField.getText()),
                    lieuField.getText(),
                    dateField.getValue()
            );

            eventRepository.addEvent(event);
            eventTable.getItems().add(event); // Ajouter l'événement à la TableView
        } else {
            // Modifier l'événement existant
            selectedEvent.setTitle(titreField.getText());
            selectedEvent.setDescription(descriptionField.getText());
            selectedEvent.setDate(dateField.getValue());
            selectedEvent.setLocation(lieuField.getText());
            selectedEvent.setRemainingPlaces(Integer.parseInt(placesField.getText()));
            selectedEvent.setImageUrl(imageFile != null ? imageFile.toURI().toString() : null);

            eventRepository.updateEvent(selectedEvent);
            eventTable.refresh(); // Rafraîchir la TableView
        }

        // Réinitialiser le formulaire
        clearForm();
        ajouterButton.setText("Ajouter"); // Remettre le texte du bouton à "Ajouter"
    }

    @FXML
    private void handleModifierButtonAction() {
        if (selectedEvent != null) {
            showEditDialog(selectedEvent); // Afficher la boîte de dialogue de modification
        } else {
            showAlert("Erreur", "Veuillez sélectionner un événement à modifier.");
        }
    }

    @FXML
    private void handleSupprimerButtonAction() {
        if (selectedEvent != null) {
            // Supprimer l'événement sélectionné de la base de données
            eventRepository.deleteEvent(selectedEvent.getId());

            // Mettre à jour la TableView
            eventTable.getItems().remove(selectedEvent);

            // Réinitialiser le formulaire
            clearForm();
        }
    }

    private void showEditDialog(Event event) {
        // Créer une nouvelle boîte de dialogue
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Modifier Événement");
        dialog.setHeaderText("Modifier les détails de l'événement");

        // Créer les champs de texte pour la modification
        TextField titreField = new TextField(event.getTitle());
        TextArea descriptionField = new TextArea(event.getDescription());
        DatePicker dateField = new DatePicker(event.getDate());
        TextField lieuField = new TextField(event.getLocation());
        TextField placesField = new TextField(String.valueOf(event.getRemainingPlaces()));

        // Ajouter les champs à la boîte de dialogue
        VBox vbox = new VBox(
                new Label("Titre:"), titreField,
                new Label("Description:"), descriptionField,
                new Label("Date:"), dateField,
                new Label("Lieu:"), lieuField,
                new Label("Places:"), placesField
        );
        dialog.getDialogPane().setContent(vbox);

        // Ajouter les boutons OK et Annuler
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Gérer le résultat de la boîte de dialogue
        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                event.setTitle(titreField.getText());
                event.setDescription(descriptionField.getText());
                event.setDate(dateField.getValue());
                event.setLocation(lieuField.getText());
                event.setRemainingPlaces(Integer.parseInt(placesField.getText()));
                return ButtonType.OK;
            }
            return null;
        });

        // Afficher la boîte de dialogue et attendre la réponse de l'utilisateur
        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                eventRepository.updateEvent(event);
                eventTable.refresh(); // Rafraîchir la TableView
                showAlert("Succès", "Événement modifié avec succès.");
            }
        });
    }

    private void clearForm() {
        titreField.clear();
        descriptionField.clear();
        dateField.setValue(null);
        lieuField.clear();
        placesField.clear();
        imageButton.setText("Télécharger une image");
        imageFile = null;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}