package com.example.ewaste.Controllers;

import com.example.ewaste.Entities.Event;
import com.example.ewaste.Repository.EventRepository;
import com.example.ewaste.Utils.Navigate;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;
import java.util.stream.Collectors;

public class ListEvenementController {

    @FXML
    private VBox leftEventContainer;

    @FXML
    private VBox rightEventContainer;

    @FXML
    private TextField searchField;

    private final EventRepository eventRepository = new EventRepository();
    private List<Event> events;

    public void initialize() {
        // Récupérer la liste des événements depuis la base de données
        events = eventRepository.getEvents();
        displayEvents(events);

        // Ajouter un écouteur pour la barre de recherche
        searchField.textProperty().addListener((observable, oldValue, newValue) -> filterEvents(newValue));
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

    private void displayEvents(List<Event> events) {
        leftEventContainer.getChildren().clear();
        rightEventContainer.getChildren().clear();

        for (int i = 0; i < events.size(); i++) {
            VBox targetContainer = (i % 2 == 0) ? leftEventContainer : rightEventContainer;
            targetContainer.getChildren().add(createEventCard(events.get(i)));
        }
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
            Navigate.navigate(actionButton, "/views/event/participation-form.fxml", stage);
        });

        // Ajouter les éléments à la carte de l'événement
        card.getChildren().addAll(imageView, title, description, date, location, places, actionButton);

        return card;
    }
}
