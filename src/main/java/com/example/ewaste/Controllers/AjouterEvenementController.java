package com.example.ewaste.Controllers;

import com.example.ewaste.Entities.Event;
import com.example.ewaste.Repository.EventRepository;
import com.example.ewaste.Utils.ImageUtils;
import com.example.ewaste.Utils.QwenApiClientEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.ComboBox;
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
import java.time.LocalDateTime;
import java.time.LocalTime;
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
    private TableColumn<Event, LocalDateTime> dateColumn;

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
                try {
                    // Get the file extension
                    String fileName = selectedFile.getName();
                    String fileExtension = ImageUtils.getFileExtension(fileName);

                    // Load the image for display
                    Image image = new Image(selectedFile.toURI().toString());
                    imageView.setImage(image);

                    // Save the image to a file and store the path
                    try (FileInputStream imageInputStream = new FileInputStream(selectedFile)) {
                        byte[] imageBytes = new byte[(int) selectedFile.length()];
                        imageInputStream.read(imageBytes);
                        String base64Image = Base64.getEncoder().encodeToString(imageBytes);

                        // Check if the base64 string is too large for the database
                        if (ImageUtils.isBase64TooLarge(base64Image, 1000)) {
                            // Save the image to a file and store the path instead
                            String imagePath = ImageUtils.saveBase64Image(base64Image, fileExtension);
                            if (imagePath != null) {
                                imageUrl = imagePath;
                                System.out.println("Image saved to: " + imagePath);
                            } else {
                                showAlert("Erreur", "Impossible de sauvegarder l'image.");
                            }
                        } else {
                            // The base64 string is small enough to store directly
                            imageUrl = base64Image;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert("Erreur", "Erreur lors du traitement de l'image: " + e.getMessage());
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


    @FXML
    private void handleAjouterButtonAction() {
        if (ajouterButton.getText().equals("Ajouter")) {
            // Convert LocalDate to LocalDateTime (set time to midnight)
            LocalDateTime dateTime = null;
            if (dateField.getValue() != null) {
                dateTime = LocalDateTime.of(dateField.getValue(), LocalTime.MIDNIGHT);
            }

            Event event = new Event(
                    0,
                    titreField.getText(),
                    descriptionField.getText(),
                    imageUrl, // Utiliser l'URL de l'image
                    Integer.parseInt(placesField.getText()),
                    lieuField.getText(),
                    dateTime
            );

            // Set default participation mode
            event.setParticipationMode("on-site");

            eventRepository.addEvent(event);
            eventTable.getItems().add(event);
        } else {
            selectedEvent.setTitle(titreField.getText());
            selectedEvent.setDescription(descriptionField.getText());

            // Convert LocalDate to LocalDateTime
            if (dateField.getValue() != null) {
                LocalDateTime dateTime = LocalDateTime.of(dateField.getValue(), LocalTime.MIDNIGHT);
                selectedEvent.setDate(dateTime);
            }

            selectedEvent.setLocation(lieuField.getText());
            selectedEvent.setRemainingPlaces(Integer.parseInt(placesField.getText()));
            selectedEvent.setImageName(imageUrl); // Use setImageName instead of setImageUrl

            // Ensure participation mode is set
            if (selectedEvent.getParticipationMode() == null) {
                selectedEvent.setParticipationMode("on-site");
            }

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

        String imageData = imageUrl;

        // If the imageUrl is a file path, convert it to base64 for the API
        if (imageUrl.startsWith("images/") || imageUrl.contains("/")) {
            try {
                // Load the image from the file and convert to base64
                imageData = ImageUtils.loadImageAsBase64(imageUrl);
                if (imageData == null) {
                    showAlert("Erreur", "Impossible de charger l'image pour la génération de description.");
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Erreur", "Erreur lors du chargement de l'image: " + e.getMessage());
                return;
            }
        }

        // Générer la description à partir de l'URL de l'image
<<<<<<< Updated upstream
        String generatedDescription = QwenApiClientEvent.generateTextFromImage("Générer une description en 2 lignes à partir de cette photo d'événement.", imageUrl);
        System.out.println(imageUrl);
=======
        String generatedDescription = QwenApiClientEvent.generateTextFromImage(
            "Générer une description en 10 mots à partir de cette photo d'événement.",
            imageData
        );

>>>>>>> Stashed changes
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

        // Convert LocalDateTime to LocalDate for DatePicker
        if (event.getDate() != null) {
            dateField.setValue(event.getDate().toLocalDate());
        } else {
            dateField.setValue(null);
        }

        lieuField.setText(event.getLocation());
        placesField.setText(String.valueOf(event.getRemainingPlaces()));

        // Handle image - try both getImageUrl and getImageName for compatibility
        String imageSrc = event.getImageName() != null ? event.getImageName() : event.getImageUrl();
        if (imageSrc != null) {
            imageUrl = imageSrc;
            try {
                // Check if the image source is a file path
                if (imageSrc.startsWith("images/") || imageSrc.contains("/")) {
                    // It's a file path, load the file
                    try {
                        File imageFile = new File(imageSrc);
                        if (imageFile.exists()) {
                            Image image = new Image(imageFile.toURI().toString());
                            imageView.setImage(image);
                        } else {
                            System.err.println("Image file not found: " + imageSrc);
                            imageView.setImage(null);
                        }
                    } catch (Exception e) {
                        System.err.println("Error loading image file: " + e.getMessage());
                        imageView.setImage(null);
                    }
                } else {
                    // It's likely a base64 string, try to load it directly
                    try {
                        Image image = new Image(imageSrc);
                        imageView.setImage(image);
                    } catch (Exception e) {
                        System.err.println("Error loading image from string: " + e.getMessage());
                        imageView.setImage(null);
                    }
                }
            } catch (Exception e) {
                System.err.println("Error loading image: " + e.getMessage());
                imageView.setImage(null);
            }
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
                getClass().getResource("/com.example.ewaste/styles/AjouterEvenement.css").toExternalForm()
        );

        // Créer les champs de texte pour la modification
        TextField titreField = new TextField(event.getTitle());
        TextArea descriptionField = new TextArea(event.getDescription());

        // Create DatePicker with LocalDate (converted from LocalDateTime)
        DatePicker dateField = new DatePicker();
        if (event.getDate() != null) {
            dateField.setValue(event.getDate().toLocalDate());
        }

        TextField lieuField = new TextField(event.getLocation());
        TextField placesField = new TextField(String.valueOf(event.getRemainingPlaces()));

        // Add participation mode ComboBox
        ComboBox<String> participationModeComboBox = new ComboBox<>();
        participationModeComboBox.getItems().addAll("on-site", "online", "hybrid");
        participationModeComboBox.setValue(event.getParticipationMode() != null ?
                                          event.getParticipationMode() : "on-site");

        // Add Google Meet link field (only visible for online/hybrid modes)
        TextField googleMeetLinkField = new TextField(event.getGoogleMeetLink());
        googleMeetLinkField.setPromptText("Google Meet Link (for online/hybrid events)");
        googleMeetLinkField.setVisible("online".equals(event.getParticipationMode()) ||
                                      "hybrid".equals(event.getParticipationMode()));

        // Make Google Meet link field visible/invisible based on participation mode
        participationModeComboBox.setOnAction(e -> {
            String mode = participationModeComboBox.getValue();
            googleMeetLinkField.setVisible("online".equals(mode) || "hybrid".equals(mode));
        });

        // Ajouter les champs à la boîte de dialogue
        VBox vbox = new VBox(
                new Label("Titre:"), titreField,
                new Label("Description:"), descriptionField,
                new Label("Date:"), dateField,
                new Label("Lieu:"), lieuField,
                new Label("Places:"), placesField,
                new Label("Mode de participation:"), participationModeComboBox,
                new Label("Lien Google Meet (optionnel):"), googleMeetLinkField
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

                // Convert LocalDate to LocalDateTime
                if (dateField.getValue() != null) {
                    LocalDateTime dateTime = LocalDateTime.of(dateField.getValue(), LocalTime.MIDNIGHT);
                    event.setDate(dateTime);
                }

                event.setLocation(lieuField.getText());
                event.setRemainingPlaces(Integer.parseInt(placesField.getText()));
                event.setParticipationMode(participationModeComboBox.getValue());
                event.setGoogleMeetLink(googleMeetLinkField.getText());
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