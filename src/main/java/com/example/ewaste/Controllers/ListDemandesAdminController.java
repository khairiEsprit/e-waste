package com.example.ewaste.Controllers;

import com.example.ewaste.Entities.Demande;
import com.example.ewaste.Repository.DemandeRepository;
import com.example.ewaste.Utils.AlertUtil;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
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
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileOutputStream;

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
        //Cette méthode récupère les demandes depuis la base et les affiche dans la table.
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
    //Cette méthode définit comment chaque colonne affiche les données.
    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        adresseColumn.setCellValueFactory(new PropertyValueFactory<>("adresse"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("emailUtilisateur"));
        messageColumn.setCellValueFactory(new PropertyValueFactory<>("message"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
//Ajout des boutons d'action
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
                    HBox buttons = new HBox(5, detailButton, editButton, deleteButton, traitementButton);
                    setGraphic(buttons);
                }
            }
        });
    }
    //Ouvre un formulaire pour modifier une demande.
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
            Scene scene=new Scene(root,900,600);
            stage.setScene(scene);
            stage.setTitle("Détails du Traitement");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtil.showAlert("Erreur", "Impossible d'ouvrir la fenêtre du traitement.", Alert.AlertType.ERROR);
        }
    }
    // ta5ou ay heja da5elha fel input mta3 recherche w tfiltrilik el list mta3 demande selon id address type w email
    public void filterList() {
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

    @FXML
    private void generatePDF() {
        Document document = new Document();
        try {
            // Specify the file path where the PDF will be saved
            PdfWriter.getInstance(document, new FileOutputStream("ListeDesDemandes.pdf"));
            document.open();

            // Add a title with custom font and styling
            com.itextpdf.text.Font titleFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 18, com.itextpdf.text.Font.BOLD);
            Paragraph title = new Paragraph("Liste des Demandes", titleFont);
            title.setAlignment(Paragraph.ALIGN_CENTER);
            title.setSpacingAfter(20); // Add space after the title
            document.add(title);

            // Create a table with the same columns as the TableView
            PdfPTable pdfTable = new PdfPTable(5); // 5 columns: ID, Adresse, Email, Message, Type
            pdfTable.setWidthPercentage(100); // Table width as 100% of the page
            pdfTable.setSpacingBefore(10); // Add space before the table
            pdfTable.setSpacingAfter(10); // Add space after the table

            // Define custom fonts for headers and cells
            com.itextpdf.text.Font headerFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 12, com.itextpdf.text.Font.BOLD);
            com.itextpdf.text.Font cellFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 10);

            // Add table headers with custom font and background color
            PdfPCell headerCell;
            headerCell = new PdfPCell(new Phrase("ID", headerFont));
            headerCell.setBackgroundColor(new com.itextpdf.text.BaseColor(200, 200, 200)); // Light gray background
            pdfTable.addCell(headerCell);

            headerCell = new PdfPCell(new Phrase("Adresse", headerFont));
            headerCell.setBackgroundColor(new com.itextpdf.text.BaseColor(200, 200, 200));
            pdfTable.addCell(headerCell);

            headerCell = new PdfPCell(new Phrase("Email Utilisateur", headerFont));
            headerCell.setBackgroundColor(new com.itextpdf.text.BaseColor(200, 200, 200));
            pdfTable.addCell(headerCell);

            headerCell = new PdfPCell(new Phrase("Message", headerFont));
            headerCell.setBackgroundColor(new com.itextpdf.text.BaseColor(200, 200, 200));
            pdfTable.addCell(headerCell);

            headerCell = new PdfPCell(new Phrase("Type", headerFont));
            headerCell.setBackgroundColor(new com.itextpdf.text.BaseColor(200, 200, 200));
            pdfTable.addCell(headerCell);

            // Add rows from the TableView data with custom font
            //Ajout des données du TableView dans le PDF
            for (Demande demande : demandeList) {
                pdfTable.addCell(new Phrase(String.valueOf(demande.getId()), cellFont));
                pdfTable.addCell(new Phrase(demande.getAdresse(), cellFont));
                pdfTable.addCell(new Phrase(demande.getEmailUtilisateur(), cellFont));
                pdfTable.addCell(new Phrase(demande.getMessage(), cellFont));
                pdfTable.addCell(new Phrase(demande.getType(), cellFont));
            }

            // Add the table to the document
            document.add(pdfTable);

            // Add a footer
            Paragraph footer = new Paragraph("Généré le: " + new java.util.Date(), cellFont);
            footer.setAlignment(Paragraph.ALIGN_CENTER);
            footer.setSpacingBefore(20); // Add space before the footer
            document.add(footer);

            // Close the document
            document.close();

            // Show a success message
            AlertUtil.showAlert("Succès", "PDF généré avec succès.", Alert.AlertType.INFORMATION);
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
            AlertUtil.showAlert("Erreur", "Erreur lors de la génération du PDF.", Alert.AlertType.ERROR);
        }
    }
}