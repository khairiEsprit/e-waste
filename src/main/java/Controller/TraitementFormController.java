package Controller;

import entities.Traitement;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import repository.TraitementRepository;
import utils.AlertUtil;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

public class TraitementFormController implements Initializable {
    @FXML
    private ComboBox<String> statusField;

    @FXML
    private TextArea commentaireField;

    @FXML
    private Button saveButton;

    @FXML
    private Button cancelButton;
    @FXML
    private TextField idDemandeField;
    private final TraitementRepository traitementRepository = new TraitementRepository();
    private int idDemande;
    private Traitement traitementToEdit = null;

    public void setDemandeId(int idDemande) {
        this.idDemande = idDemande;
        if (idDemandeField != null) {

            idDemandeField.setEditable(false); // Ensure it's non-editable
        }
    }
    public void handleSubmit(ActionEvent actionEvent) {
        try {
            String status = statusField.getValue();
            String commentaire = commentaireField.getText();


            if (status == null || status.isEmpty() || commentaire.isEmpty()) {
                AlertUtil.showAlert("Erreur", "Veuillez remplir tous les champs.", Alert.AlertType.ERROR);
                return;
            }

            if (traitementToEdit == null) {

                Traitement newTraitement = new Traitement(idDemande, status, LocalDateTime.now(), commentaire);
                traitementRepository.ajouter(newTraitement);
                AlertUtil.showAlert("Succès", "Traitement ajouté avec succès.", Alert.AlertType.INFORMATION);
            } else {

                traitementToEdit.setStatus(status);
                traitementToEdit.setCommentaire(commentaire);
                traitementToEdit.setDateTraitement(LocalDateTime.now());
                traitementRepository.modifier(traitementToEdit);
                AlertUtil.showAlert("Succès", "Traitement modifié avec succès.", Alert.AlertType.INFORMATION);
            }


            closeForm();
            refreshTraitementTable();

        } catch (SQLException e) {
            AlertUtil.showAlert("Erreur", "Une erreur s'est produite lors de l'enregistrement.", Alert.AlertType.ERROR);
        }
    }

    public void handleCancel(ActionEvent actionEvent) {
        closeForm();
    }
    private void closeForm() {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }

    private void refreshTraitementTable() {
        TraitementController traitementController = TraitementController.getInstance();
        if (traitementController != null) {
            traitementController.loadTraitementByDemande(idDemande);
            System.out.println("hdsdsdsds"+idDemande);
        }
    }
    public void setTraitementToEdit(Traitement traitement) {

        if (traitement == null) {
            AlertUtil.showAlert("Erreur", "Aucun traitement sélectionné pour modification.", Alert.AlertType.ERROR);
            return;
        }


        // Store the traitement for updates
        this.traitementToEdit = traitement;
        this.idDemande = traitement.getIdDemande();




            statusField.setValue(traitement.getStatus());
            commentaireField.setText(traitement.getCommentaire());
            saveButton.setText("Modifier");

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        System.out.println(idDemande);
    }


}
