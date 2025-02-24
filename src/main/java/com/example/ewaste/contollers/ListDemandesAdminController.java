package com.example.ewaste.contollers;


import com.example.ewaste.entities.Demande;
import com.example.ewaste.repository.DemandeRepository;
import com.example.ewaste.utils.AlertUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;


import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class ListDemandesAdminController implements Initializable {
    private static ListDemandesAdminController instance;
    public TextField searchField;

    public static ListDemandesAdminController getInstance() {
        return instance;
    }

    @FXML
    private TableView<Demande> demandeTable;

    @FXML
    private TableColumn<Demande, Integer> idColumn;

    @FXML
    private TableColumn<Demande, String> adresseColumn;

    @FXML
    private TableColumn<Demande, String> emailColumn;

    @FXML
    private TableColumn<Demande, String> messageColumn;

    @FXML
    private TableColumn<Demande, String> typeColumn;

    @FXML
    private TableColumn<Demande, Void> actionsColumn;
    private final DemandeRepository demandeRepository = new DemandeRepository();
    private ObservableList<Demande> demandeList = FXCollections.observableArrayList();
    private FilteredList<Demande> filteredDemandeList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        instance = this;
        setupTableColumns();
        loadDemandeData();
        setupFiltering();

    }

    private void setupFiltering() {
    }

    public void loadDemandeData() {
        try {
            List<Demande> demandes = demandeRepository.afficher();
            demandeList.setAll(demandes);

            // Wrap it in a FilteredList
            filteredDemandeList = new FilteredList<>(demandeList, p -> true);

            // Wrap the filtered list in a SortedList
            SortedList<Demande> sortedDemandeList = new SortedList<>(filteredDemandeList);
            sortedDemandeList.comparatorProperty().bind(demandeTable.comparatorProperty());

            demandeTable.setItems(sortedDemandeList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        adresseColumn.setCellValueFactory(new PropertyValueFactory<>("adresse"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("emailUtilisateur"));
        messageColumn.setCellValueFactory(new PropertyValueFactory<>("message"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));


        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Modifier");
            private final Button deleteButton = new Button("Supprimer");
            private final Button detailButton = new Button("Détail");
            private final Button traitementButton = new Button("Traitement");

            {
                editButton.setOnAction(event -> editDemande(getTableView().getItems().get(getIndex())));
                deleteButton.setOnAction(event -> deleteDemande(getTableView().getItems().get(getIndex())));
                detailButton.setOnAction(event -> showDemandeDetail(getTableView().getItems().get(getIndex())));
                traitementButton.setOnAction(event -> {
                    Demande selectedDemande = getTableView().getItems().get(getIndex());
                    showTraitementDetails(selectedDemande.getId());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(5, detailButton, editButton, deleteButton,traitementButton);
                    setGraphic(buttons);
                }
            }
        });
    }
    private void editDemande(Demande demande) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ewaste/views/FormDemande.fxml"));
            Parent root = loader.load();


            FormDemandeController controller = loader.getController();
            controller.setDemandeToEdit(demande);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Modifier la Demande");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtil.showAlert("Erreur", "Impossible d'ouvrir la fenêtre de modification.", Alert.AlertType.ERROR);
        }
    }

    private void deleteDemande(Demande demande) {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmation de suppression");
        confirmationAlert.setHeaderText("Voulez-vous vraiment supprimer ce traitement ?");
        confirmationAlert.setContentText("Cette action est irréversible.");

        // Add OK and CANCEL buttons
        ButtonType buttonTypeOK = new ButtonType("Oui", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirmationAlert.getButtonTypes().setAll(buttonTypeOK, buttonTypeCancel);

        // Show the alert and wait for user response
        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == buttonTypeOK) {
                try {
                    demandeRepository.supprimer(demande.getId());
                    loadDemandeData();
                    AlertUtil.showAlert("Suppression", "Demande supprimée avec succès.", Alert.AlertType.INFORMATION);
                } catch (SQLException e) {
                    e.printStackTrace();
                    AlertUtil.showAlert("Erreur", "Impossible de supprimer la demande.", Alert.AlertType.ERROR);
                }
            }
        });

    }

    private void showDemandeDetail(Demande demande) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ewaste/views/DemandeDetail.fxml"));
            Parent root = loader.load();

            // Get the controller and pass data
            DemandeDetailController controller = loader.getController();
            controller.setDemandeDetails(demande);

            // Show new stage
            Stage stage = new Stage();
            stage.setTitle("Détail de la Demande");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void showTraitementDetails(int idDemande) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ewaste/views/TaitementByDemandeAdmin.fxml"));
            Parent root = loader.load();

            TaitementByDemandeAdminController traitementController = loader.getController();
            traitementController.setDemandeId(idDemande);
            traitementController.loadTraitementByDemande(idDemande);


            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Détails du Traitement");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtil.showAlert("Erreur", "Impossible d'ouvrir la fenêtre du traitement.", Alert.AlertType.ERROR);
        }
    }

    public void filterList() {
        // ta5ou ay heja da5elha fel input mta3 recherche w tfiltrilik el list mta3 demande selon id address type w email
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredDemandeList.setPredicate(demande -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                return (String.valueOf(demande.getId()).contains(lowerCaseFilter) ||
                        demande.getAdresse().toLowerCase().contains(lowerCaseFilter) ||
                        demande.getEmailUtilisateur().toLowerCase().contains(lowerCaseFilter) ||
                        demande.getMessage().toLowerCase().contains(lowerCaseFilter) ||
                        demande.getType().toLowerCase().contains(lowerCaseFilter));
            });
        });
    }
}
