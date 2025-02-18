package com.example.ewaste.contollers;


import com.example.ewaste.entities.Traitement;
import com.example.ewaste.repository.TraitementRepository;
import com.example.ewaste.utils.AlertUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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

public class TraitementController implements Initializable {
    private static TraitementController instance;
    private int currentDemandeId=0;

    public static TraitementController getInstance() {
        return instance;
    }
    @FXML
    private TableView<Traitement> traitementTable;

    @FXML
    private TableColumn<Traitement, Integer> idColumn;

    @FXML
    private TableColumn<Traitement, String> statusColumn;

    @FXML
    private TableColumn<Traitement, String> dateColumn;

    @FXML
    private TableColumn<Traitement, String> commentaireColumn;

    private final TraitementRepository traitementRepository = new TraitementRepository();
    private ObservableList<Traitement> traitementList = FXCollections.observableArrayList();



    public void loadTraitementByDemande(int idDemande) {
        try {
            List<Traitement> traitements = traitementRepository.getTraitementByDemande(idDemande);
            traitementList.setAll(traitements);
            traitementTable.setItems(traitementList);
        } catch (SQLException e) {
            AlertUtil.showAlert("Erreur", "Impossible de charger les traitements.", Alert.AlertType.ERROR);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        instance = this;
        setupTableColumns();
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("dateTraitement"));
        commentaireColumn.setCellValueFactory(new PropertyValueFactory<>("commentaire"));

        TableColumn<Traitement, Void> actionsColumn = new TableColumn<>("Actions");

        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Modifier");
            private final Button deleteButton = new Button("Supprimer");

            {
                editButton.setOnAction(event -> {
                    Traitement selectedTraitement = getTableView().getItems().get(getIndex());
                    openEditTraitementForm(selectedTraitement);
                });

                deleteButton.setOnAction(event -> {
                    Traitement selectedTraitement = getTableView().getItems().get(getIndex());
                    confirmAndDeleteTraitement(selectedTraitement);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(5, editButton, deleteButton);
                    setGraphic(buttons);
                }
            }
        });

        traitementTable.getColumns().add(actionsColumn);
    }
    @FXML
    private void openAddTraitementForm(ActionEvent event) {
//        Traitement selectedTraitement = traitementTable.getSelectionModel().getSelectedItem();
//
//        if (selectedTraitement == null) {
//            AlertUtil.showAlert("Erreur", "Veuillez sélectionner un traitement pour ajouter une action.", Alert.AlertType.ERROR);
//            return;
//        }
//
//        openAddTraitementForm(selectedTraitement.getIdDemande());
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ewaste/views/TraitementForm.fxml"));
            Parent root = loader.load();
            TraitementFormController controller = loader.getController();
             controller.setDemandeId(currentDemandeId);
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter un Traitement");
            stage.show();
        } catch (IOException e) {
            AlertUtil.showAlert("Erreur", "Impossible d'ouvrir le formulaire.", Alert.AlertType.ERROR);
        }

    }


    public void openAddTraitementForm(int idDemande) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ewaste/views/TraitementForm.fxml"));
            Parent root = loader.load();
            TraitementFormController controller = loader.getController();
            controller.setDemandeId(idDemande);
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter un Traitement");
            stage.show();
        } catch (IOException e) {
            AlertUtil.showAlert("Erreur", "Impossible d'ouvrir le formulaire.", Alert.AlertType.ERROR);
        }
    }
    private void openEditTraitementForm(Traitement traitement) {
        Traitement selectedTraitement = traitementTable.getSelectionModel().getSelectedItem();
      System.out.println(selectedTraitement);
        if (selectedTraitement == null) {
            AlertUtil.showAlert("Erreur", "Veuillez sélectionner un traitement à modifier.", Alert.AlertType.ERROR);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ewaste/views/TraitementForm.fxml"));
            Parent root = loader.load();


            TraitementFormController controller = loader.getController();
            controller.setTraitementToEdit(selectedTraitement);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Modifier le Traitement");
            stage.show();
        } catch (IOException e) {
            AlertUtil.showAlert("Erreur", "Impossible d'ouvrir la modification.", Alert.AlertType.ERROR);
        }
    }

    private void confirmAndDeleteTraitement(Traitement traitement) {
        // Create a confirmation alert
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
                    traitementRepository.supprimer(traitement.getId());
                    loadTraitementByDemande(traitement.getIdDemande());
                    AlertUtil.showAlert("Succès", "Traitement supprimé avec succès.", Alert.AlertType.INFORMATION);
                } catch (SQLException e) {
                    AlertUtil.showAlert("Erreur", "Impossible de supprimer le traitement.", Alert.AlertType.ERROR);
                }
            }
        });

    }


    public void setDemandeId(int idDemande) {
        this.currentDemandeId = idDemande;
    }
}
