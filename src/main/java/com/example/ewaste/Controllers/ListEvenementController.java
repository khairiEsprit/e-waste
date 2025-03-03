package com.example.ewaste.Controllers;

import com.example.ewaste.Entities.CitizenPoints;
import com.example.ewaste.Entities.Event;
import com.example.ewaste.Repository.CitizenPointsRepository;
import com.example.ewaste.Repository.EventRepository;
import com.example.ewaste.Utils.DataBase;
import com.example.ewaste.Utils.Navigate;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class ListEvenementController {

    @FXML
    private VBox leftEventContainer;

    @FXML
    private VBox rightEventContainer;

    @FXML
    private TextField searchField;

    @FXML
    private VBox middleEventContainer;

    @FXML
    private ComboBox<String> statusFilter;

    @FXML
    private Label totalEventsLabel;

    private final EventRepository eventRepository = new EventRepository();
    private final CitizenPointsRepository citizenPointsRepository = new CitizenPointsRepository(DataBase.getInstance().getConnection());
    private List<Event> events;

    public void initialize() {
        // Récupérer la liste des événements depuis la base de données
        events = eventRepository.getEvents();
        displayEvents(events);

        // Mettre à jour le nombre total d'événements
        updateTotalEventsLabel();

        // Ajouter un écouteur pour la barre de recherche
        searchField.textProperty().addListener((observable, oldValue, newValue) -> filterEvents(newValue));

        // Ajouter un écouteur pour le ComboBox de filtrage par statut
        statusFilter.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            filterEventsByStatus(newValue);
        });
    }

    // Méthode pour naviguer vers l'interface Avis
    @FXML
    private void goToAvis() {
        Stage stage = (Stage) leftEventContainer.getScene().getWindow();
        Navigate.navigate(new Button(), "/com/example/ewaste/views/Avis.fxml", stage); // Utilisez un Button si nécessaire
    }

    // Méthode pour mettre à jour le nombre total d'événements
    private void updateTotalEventsLabel() {
        int totalEvents = events.size();
        totalEventsLabel.setText("Nombre total d'événements : " + totalEvents);
    }

    @FXML
    private void sortByDate() {
        List<Event> sortedEvents = events.stream()
                .sorted((e1, e2) -> e1.getDate().compareTo(e2.getDate()))
                .collect(Collectors.toList());
        displayEvents(sortedEvents);
    }

    private void filterEvents(String searchText) {
        List<Event> filteredEvents = events.stream()
                .filter(event -> event.getTitle().toLowerCase().contains(searchText.toLowerCase()))
                .collect(Collectors.toList());
        displayEvents(filteredEvents);
    }

    private void filterEventsByStatus(String status) {
        LocalDate today = LocalDate.now();
        List<Event> filteredEvents = events.stream()
                .filter(event -> {
                    LocalDate eventDate = event.getDate();
                    if (eventDate == null) {
                        return false; // Ignorer les événements sans date valide
                    }

                    switch (status) {
                        case "Événements terminés":
                            return eventDate.isBefore(today);
                        case "Événements en cours":
                            return eventDate.isEqual(today);
                        case "Événements à venir":
                            return eventDate.isAfter(today);
                        default:
                            return true; // Tous les événements
                    }
                })
                .collect(Collectors.toList());
        displayEvents(filteredEvents);
    }

    private void displayEvents(List<Event> events) {
        // Clear all columns
        leftEventContainer.getChildren().clear();
        middleEventContainer.getChildren().clear();
        rightEventContainer.getChildren().clear();

        // Distribute events across 3 columns
        for (int i = 0; i < events.size(); i++) {
            VBox targetContainer;
            if (i % 3 == 0) {
                targetContainer = leftEventContainer; // First column
            } else if (i % 3 == 1) {
                targetContainer = middleEventContainer; // Second column
            } else {
                targetContainer = rightEventContainer; // Third column
            }
            targetContainer.getChildren().add(createEventCard(events.get(i)));
        }

        // Ajouter le bouton Avis à la colonne de gauche
        Button avisButton = new Button("Avis");
        avisButton.getStyleClass().add("button");
        avisButton.setOnAction(event -> goToAvis());
        leftEventContainer.getChildren().add(avisButton);

        // Mettre à jour le nombre total d'événements
        updateTotalEventsLabel();
    }

    private VBox createEventCard(Event event) {
        VBox card = new VBox();
        card.getStyleClass().add("event-card");
        card.setSpacing(10);

        // Image de l'événement
        ImageView imageView = new ImageView();
        try {
            Image image = new Image(event.getImageUrl());
            imageView.setImage(image);
        } catch (Exception e) {
            System.err.println("Erreur de chargement de l'image: " + e.getMessage());
        }
        imageView.setFitWidth(300);
        imageView.setFitHeight(150);
        imageView.setPreserveRatio(true);

        // Informations de l'événement
        Label title = new Label(event.getTitle());
        title.getStyleClass().add("event-title");

        Label description = new Label(event.getDescription());
        description.getStyleClass().add("event-description");

        Label date = new Label("Date: " + event.getDate());
        date.getStyleClass().add("event-info");

        Label location = new Label("Lieu: " + event.getLocation());
        location.getStyleClass().add("event-info");

        Label places = new Label("Places disponibles: " + event.getRemainingPlaces());
        places.getStyleClass().add("event-info");

        // Bouton d'action
        Button actionButton = new Button(event.isAvailable() ? "Participer" : "Complet");
        actionButton.getStyleClass().add("button");
        actionButton.setDisable(!event.isAvailable());
        actionButton.setOnAction(evt -> {
            Stage stage = (Stage) actionButton.getScene().getWindow();
            Navigate.navigate(actionButton, "/com/example/ewaste/views/participation-form.fxml", stage);

            // Ajouter 10 points au citoyen
            String email = "citoyen@example.com"; // Remplacez par l'e-mail du citoyen actuel
            citizenPointsRepository.addPoints(email, 10);

            // Vérifier si le citoyen a atteint 100 points
            CitizenPoints citizenPoints = citizenPointsRepository.getCitizenPoints(email);
            if (citizenPoints != null && citizenPoints.getTotalPoints() >= 100) {
                showAlert("Félicitations !", "Vous avez atteint 100 points et gagné une remise de cadeau !");
                citizenPointsRepository.addPoints(email, -100); // Réinitialiser les points après la remise
            }
        });

        // Bouton Détails
        Button detailsButton = new Button("Détails");
        detailsButton.getStyleClass().add("button");
        detailsButton.setOnAction(evt -> showEventDetails(event));

        // Ajouter les éléments à la carte de l'événement
        card.getChildren().addAll(imageView, title, description, date, location, places, actionButton, detailsButton);

        return card;
    }

    private void showEventDetails(Event event) {
        // Créer une boîte de dialogue pour afficher les détails de l'événement
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Détails de l'événement");
        dialog.setHeaderText(event.getTitle());

        // Appliquer les styles CSS à la boîte de dialogue
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/com/example/ewaste/styles/ListEvenement.css").toExternalForm());
        dialog.getDialogPane().getStyleClass().add("dialog-pane");

        // Créer les champs pour afficher les détails
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        grid.add(new Label("Description:"), 0, 0);
        TextArea descriptionArea = new TextArea(event.getDescription());
        descriptionArea.setEditable(false);
        grid.add(descriptionArea, 1, 0);

        grid.add(new Label("Date:"), 0, 1);
        grid.add(new Label(event.getDate().toString()), 1, 1);

        grid.add(new Label("Lieu:"), 0, 2);
        grid.add(new Label(event.getLocation()), 1, 2);

        grid.add(new Label("Places disponibles:"), 0, 3);
        grid.add(new Label(String.valueOf(event.getRemainingPlaces())), 1, 3);

        // Ajouter les nouveaux champs
        grid.add(new Label("Début de l'événement:"), 0, 4);
        grid.add(new Label("10h"), 1, 4); // Remplacez "10h" par la valeur réelle de l'événement

        grid.add(new Label("Fin de l'événement:"), 0, 5);
        grid.add(new Label("14h30"), 1, 5); // Remplacez "14h30" par la valeur réelle de l'événement

        grid.add(new Label("Points gagnés:"), 0, 6);
        grid.add(new Label("10 points"), 1, 6); // Remplacez "10 points" par la valeur réelle de l'événement

        dialog.getDialogPane().setContent(grid);

        // Ajouter un bouton Fermer
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        // Afficher la boîte de dialogue
        dialog.showAndWait();
    }

    // Méthode pour afficher une alerte
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}