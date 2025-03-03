package com.example.ewaste.Controllers;

import com.example.ewaste.Entities.Avis;
import com.example.ewaste.Repository.AvisRepository;
import com.example.ewaste.Utils.DataBase;
import com.example.ewaste.Utils.TranslationService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

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
    private Button returnButton;

    @FXML
    private TextField searchField;

    @FXML
    private VBox mainContainer;

    private final ObservableList<Avis> avisList = FXCollections.observableArrayList();
    private final AvisRepository avisRepository;
    private final TranslationService translationService = new TranslationService();
    private final Map<String, String> translationCache = new HashMap<>(); // Cache pour stocker les traductions

    public ListAvisController() {
        Connection conn = DataBase.getInstance().getConnection();
        this.avisRepository = new AvisRepository(conn);
    }

    @FXML
    public void initialize() {
        // Configuration des colonnes existantes
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        ratingColumn.setCellValueFactory(new PropertyValueFactory<>("rating"));

        // Configurer la CellFactory pour afficher les étoiles
        ratingColumn.setCellFactory(column -> new TableCell<Avis, Integer>() {
            @Override
            protected void updateItem(Integer rating, boolean empty) {
                super.updateItem(rating, empty);

                if (empty || rating == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    HBox starContainer = new HBox();
                    starContainer.setSpacing(2);

                    for (int i = 1; i <= 5; i++) {
                        Label star = new Label("★");
                        if (i <= rating) {
                            star.setStyle("-fx-text-fill: gold;");
                        } else {
                            star.setStyle("-fx-text-fill: gray;");
                        }
                        starContainer.getChildren().add(star);
                    }

                    setGraphic(starContainer);
                }
            }
        });

        // Ajouter une colonne pour le bouton "Traduire"
        TableColumn<Avis, Void> translateColumn = new TableColumn<>("Traduire");
        translateColumn.setCellFactory(param -> new TableCell<>() {
            private final Button translateButton = new Button("Traduire");

            {
                translateButton.setOnAction(event -> {
                    Avis avis = getTableView().getItems().get(getIndex());
                    handleTranslateAction(avis, translateButton); // Appeler la méthode de traduction
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(translateButton);
                }
            }
        });

        // Ajouter la colonne de traduction à la TableView
        avisTable.getColumns().add(translateColumn);

        // Charger les données
        loadAvis();
        avisTable.setItems(avisList);

        // Désactiver les boutons par défaut
        editButton.setDisable(true);
        deleteButton.setDisable(true);

        // Ajouter un écouteur de sélection à la TableView
        avisTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                editButton.setDisable(false);
                deleteButton.setDisable(false);
            } else {
                editButton.setDisable(true);
                deleteButton.setDisable(true);
            }
        });

        // Ajouter un écouteur de texte pour la recherche
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterAvis(newValue);
        });

        // Vérifier que mainContainer n'est pas null avant d'ajouter l'écouteur
        if (mainContainer != null) {
            mainContainer.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
                if (!avisTable.getBoundsInParent().contains(event.getX(), event.getY())) {
                    avisTable.getSelectionModel().clearSelection();
                }
            });
        } else {
            System.err.println("Erreur : mainContainer est null. Vérifiez le fichier FXML.");
        }
    }

    private void filterAvis(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            avisTable.setItems(avisList);
        } else {
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
    private void handleReturnAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ewaste/views/Avis.fxml"));
            Parent root = loader.load();
            Scene currentScene = returnButton.getScene();
            currentScene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger la page Avis.");
        }
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
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Modifier Avis");
        dialog.setHeaderText("Modifier les détails de l'avis");

        // Appliquer le style CSS à la boîte de dialogue
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/com/example/ewaste/styles/ListAvis.css").toExternalForm());

        // Créer les champs de texte
        TextField nameField = new TextField(avis.getName());
        TextField descriptionField = new TextField(avis.getDescription());
        TextField ratingField = new TextField(String.valueOf(avis.getRating()));

        // Créer le contenu de la boîte de dialogue
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

        // Afficher la boîte de dialogue et attendre la réponse
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

    private void handleTranslateAction(Avis avis, Button translateButton) {
        // Créer un ContextMenu pour les langues
        ContextMenu contextMenu = new ContextMenu();

        // Ajouter les langues disponibles
        MenuItem frenchItem = new MenuItem("Français");
        MenuItem englishItem = new MenuItem("Anglais");
        MenuItem arabicItem = new MenuItem("Arabe");

        // Gérer les actions pour chaque langue
        frenchItem.setOnAction(event -> translateAvis(avis, "fr")); // Traduire en français
        englishItem.setOnAction(event -> translateAvis(avis, "en")); // Traduire en anglais
        arabicItem.setOnAction(event -> translateAvis(avis, "ar")); // Traduire en arabe

        // Ajouter les MenuItem au ContextMenu
        contextMenu.getItems().addAll(frenchItem, englishItem, arabicItem);

        // Afficher le ContextMenu près du bouton "Traduire"
        contextMenu.show(translateButton, javafx.geometry.Side.BOTTOM, 0, 0);
    }

    private void translateAvis(Avis avis, String targetLanguage) {
        // Vérifier si la traduction est déjà dans le cache
        String cacheKey = avis.getDescription() + "|" + targetLanguage;
        if (translationCache.containsKey(cacheKey)) {
            avis.setDescription(translationCache.get(cacheKey));
            avisTable.refresh();
            return;
        }

        // Traduire l'avis
        try {
            String translatedText = translationService.translateText(avis.getDescription(), targetLanguage);
            avis.setDescription(translatedText);
            translationCache.put(cacheKey, translatedText); // Ajouter la traduction au cache
            avisTable.refresh();
        } catch (Exception e) {
            System.err.println("Erreur lors de la traduction : " + e.getMessage());
            showAlert("Erreur", "Impossible de traduire le texte. Vérifiez votre connexion ou votre clé API.");
        }
    }
}