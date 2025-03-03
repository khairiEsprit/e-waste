package com.example.ewaste.Controllers;

import com.example.ewaste.Entities.Event;
import com.example.ewaste.Repository.EventRepository;
import com.example.ewaste.Utils.QwenApiClientEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDate;
import java.util.Base64;


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
    private ImageView imageView;

    @FXML
    private Button ajouterButton;

    @FXML
    private Button modifierButton;

    @FXML
    private Button supprimerButton;

    @FXML
    private Button generateButton; // Bouton pour générer la description

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
    private VBox mainContainer;

    private String imageUrl; // L'image est maintenant stockée sous forme de String (URL ou chemin)
    private final EventRepository eventRepository = new EventRepository();
    private Event selectedEvent;

    @FXML
    public void initialize() {
        titreColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        lieuColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        placesColumn.setCellValueFactory(new PropertyValueFactory<>("remainingPlaces"));

        eventTable.getItems().addAll(eventRepository.getEvents());

        eventTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedEvent = newSelection;
                modifierButton.setDisable(false);
                supprimerButton.setDisable(false);
                populateForm(selectedEvent);
            } else {
                selectedEvent = null;
                modifierButton.setDisable(true);
                supprimerButton.setDisable(true);
                clearForm();
            }
        });

        if (mainContainer != null) {
            mainContainer.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
                if (!eventTable.getBoundsInParent().contains(event.getX(), event.getY())) {
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
        File selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile != null) {
            // Check if the file is a supported image type
            if (isImageFileValid(selectedFile)) {
                // Convert the image to base64
                String base64Image = encodeImageToBase64(selectedFile);
                if (base64Image != null) {
                    imageUrl = base64Image; // Use base64-encoded image
                    Image image = new Image(selectedFile.toURI().toString());
                    imageView.setImage(image);
                } else {
                    showAlert("Erreur", "Impossible de convertir l'image en base64.");
                }
            } else {
                showAlert("Erreur", "Le type d'image n'est pas supporté. Veuillez sélectionner une image au format JPEG ou PNG.");
            }
        }
    }
    private boolean isImageFileValid(File file) {
        // Check file extension
        String fileName = file.getName().toLowerCase();
        if (!fileName.endsWith(".jpg") && !fileName.endsWith(".jpeg") && !fileName.endsWith(".png")) {
            return false;
        }

        // Check file size (e.g., limit to 5MB)
        long fileSize = file.length();
        if (fileSize > 5 * 1024 * 1024) { // 5MB limit
            return false;
        }

        // You can add more checks here (e.g., image dimensions)

        return true;
    }
    private String encodeImageToBase64(File file) {
        try (FileInputStream imageInputStream = new FileInputStream(file)) {
            byte[] imageBytes = new byte[(int) file.length()];
            imageInputStream.read(imageBytes);
            return Base64.getEncoder().encodeToString(imageBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @FXML
    private void handleAjouterButtonAction() {
        if (ajouterButton.getText().equals("Ajouter")) {
            Event event = new Event(
                    0,
                    titreField.getText(),
                    descriptionField.getText(),
                    imageUrl, // Utiliser l'URL de l'image
                    Integer.parseInt(placesField.getText()),
                    lieuField.getText(),
                    dateField.getValue()
            );

            eventRepository.addEvent(event);
            eventTable.getItems().add(event);
        } else {
            selectedEvent.setTitle(titreField.getText());
            selectedEvent.setDescription(descriptionField.getText());
            selectedEvent.setDate(dateField.getValue());
            selectedEvent.setLocation(lieuField.getText());
            selectedEvent.setRemainingPlaces(Integer.parseInt(placesField.getText()));
            selectedEvent.setImageUrl(imageUrl); // Utiliser l'URL de l'image

            eventRepository.updateEvent(selectedEvent);
            eventTable.refresh();
        }

        clearForm();
        ajouterButton.setText("Ajouter");
    }

    @FXML
    private void handleModifierButtonAction() {
        if (selectedEvent != null) {
            showEditDialog(selectedEvent);
        } else {
            showAlert("Erreur", "Veuillez sélectionner un événement à modifier.");
        }
    }

    @FXML
    private void handleSupprimerButtonAction() {
        if (selectedEvent != null) {
            eventRepository.deleteEvent(selectedEvent.getId());
            eventTable.getItems().remove(selectedEvent);
            clearForm();
        }
    }

    @FXML
    private void generateDescription() {
        if (imageUrl == null) {
            showAlert("Erreur", "Veuillez sélectionner une image avant de générer la description.");
            return;
        }

        // Générer la description à partir de l'URL de l'image
        String generatedDescription = QwenApiClientEvent.generateTextFromImage("Générer une description en 2 lignes à partir de cette photo d'événement.", imageUrl);
        System.out.println(imageUrl);
        // Vérifier si la description contient une erreur
        if (generatedDescription.startsWith("API Error:") || generatedDescription.startsWith("Unexpected API response:")) {
            showAlert("Erreur API", generatedDescription);
        } else {
            // Mettre à jour le champ de description
            descriptionField.setText(generatedDescription);
        }
    }

    private void populateForm(Event event) {
        titreField.setText(event.getTitle());
        descriptionField.setText(event.getDescription());
        dateField.setValue(event.getDate());
        lieuField.setText(event.getLocation());
        placesField.setText(String.valueOf(event.getRemainingPlaces()));
        if (event.getImageUrl() != null) {
            imageUrl = event.getImageUrl();
            Image image = new Image(imageUrl);
            imageView.setImage(image);
        } else {
            imageView.setImage(null);
        }
    }

    private void clearForm() {
        titreField.clear();
        descriptionField.clear();
        dateField.setValue(null);
        lieuField.clear();
        placesField.clear();
        imageView.setImage(null);
        imageUrl = null;
    }

    private void showEditDialog(Event event) {
        // Créer une nouvelle boîte de dialogue
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Modifier Événement");
        dialog.setHeaderText("Modifier les détails de l'événement");

        // Appliquer le style CSS à la boîte de dialogue
        dialog.getDialogPane().getStylesheets().add(
                getClass().getResource("/com/example/ewaste/styles/AjouterEvenement.css").toExternalForm()
        );

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
        vbox.setSpacing(10); // Espacement entre les éléments
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

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}