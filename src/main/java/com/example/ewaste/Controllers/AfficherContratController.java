package com.example.ewaste.Controllers;

import com.example.ewaste.Entities.Centre;
import com.example.ewaste.Entities.Contrat;
import com.example.ewaste.Repository.ContratRepository;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.layout.element.Image;
import com.itextpdf.io.image.ImageDataFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class AfficherContratController {

    @FXML
    private DatePicker DateFin;

    @FXML
    private ComboBox<String> idCentre;

    @FXML
    private Button btnModifier;

    @FXML
    private ListView<Contrat> afficher;

    @FXML
    private DatePicker DateDebut;

    @FXML
    private Button btnAjouter;

    @FXML
    private ComboBox<String> idEmploye;

    @FXML
    private Button btnSupprimer;

    @FXML
    private Canvas signature;

    private ContratRepository contratRepository = new ContratRepository();

    private boolean signatureAffichee = false;

    private Contrat contratToModify; // Field to store the contract being modified

    @FXML
    public void initialize() {
        loadData();
        loadComboBoxData();

        if (signature == null) {
            System.out.println("Canvas non trouvé !");
        } else {
            System.out.println("Canvas chargé avec succès !");
            GraphicsContext gc = signature.getGraphicsContext2D();
            gc.setFill(Color.WHITE);
            gc.fillRect(0, 0, signature.getWidth(), signature.getHeight());

            signature.setOnMousePressed(e -> {
                if (signatureAffichee) {
                    gc.setFill(Color.WHITE);
                    gc.fillRect(0, 0, signature.getWidth(), signature.getHeight());
                    signatureAffichee = false;
                }
                gc.beginPath();
                gc.moveTo(e.getX(), e.getY());
                gc.stroke();
            });

            signature.setOnMouseDragged(e -> {
                gc.lineTo(e.getX(), e.getY());
                gc.stroke();
            });
        }

        afficher.setCellFactory(param -> new ContratListCellController());

        DateDebut.getEditor().setDisable(true);
        DateDebut.getEditor().setOpacity(1);
        DateFin.getEditor().setDisable(true);
        DateFin.getEditor().setOpacity(1);

        // Listener for ListView selection to open Modifier window and preserve contract
        afficher.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newSelection) -> {
            if (newSelection != null) {
                contratToModify = newSelection; // Preserve the selected contract
                afficherDetailsContrat(newSelection);
                afficherSignature(newSelection.getId());
                openModifierContratWindow(newSelection); // Open Modifier Contrat window
            }
        });
    }

    private void afficherDetailsContrat(Contrat contrat) {
        if (contrat != null) {
            try {
                String centreNom = contratRepository.getCentreNameById(contrat.getIdCentre());
                String employeNom = contratRepository.getEmployeNameById(contrat.getIdEmploye());
                idCentre.setValue(centreNom);
                idEmploye.setValue(employeNom);
                DateDebut.setValue(contrat.getDateDebut());
                DateFin.setValue(contrat.getDateFin());
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Erreur lors du chargement des détails du contrat.");
            }
        }
    }

    private void loadComboBoxData() {
        try {
            List<Centre> centreList = contratRepository.getCentres();
            ObservableList<String> centreNames = FXCollections.observableArrayList();
            for (Centre centre : centreList) {
                centreNames.add(centre.getNom());
            }
            idCentre.setItems(centreNames);

            List<String> employeNames = contratRepository.getEmployeNames();
            idEmploye.setItems(FXCollections.observableArrayList(employeNames));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadData() {
        try {
            List<Contrat> contratList = contratRepository.afficher();
            ObservableList<Contrat> observableContrats = FXCollections.observableArrayList(contratList);
            afficher.setItems(observableContrats);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur lors du chargement des données.");
        }
    }

    @FXML
    void GoToAjout(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ewaste/views/AjouterContrat.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Ajouter un Contrat");
            dialogStage.setScene(scene);
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(((Node) event.getSource()).getScene().getWindow());
            dialogStage.showAndWait();
            loadData();
            Contrat selected = afficher.getSelectionModel().getSelectedItem();
            if (selected != null) {
                contratToModify = selected; // Preserve selection after adding
                afficherDetailsContrat(selected);
                afficherSignature(selected.getId());
            } else {
                clearForm();
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur lors de l'ouverture de la fenêtre d'ajout.");
        }
    }

    private void openModifierContratWindow(Contrat selectedContrat) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ewaste/views/ModifierContrat.fxml"));
            Parent root = loader.load();
            AfficherContratController controller = loader.getController();
            controller.setContratData(selectedContrat);
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Modifier Contrat");
            dialogStage.setScene(new Scene(root));
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(afficher.getScene().getWindow());
            dialogStage.showAndWait();
            loadData();
            Contrat selected = afficher.getSelectionModel().getSelectedItem();
            if (selected != null) {
                contratToModify = selected; // Preserve selection after modifying
                afficherDetailsContrat(selected);
                afficherSignature(selected.getId());
            } else {
                clearForm();
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur Impossible de charger la fenêtre de modification.");
        }
    }

    public void setContratData(Contrat contrat) {
        contratToModify = contrat;
        try {
            idCentre.setValue(contratRepository.getCentreNameById(contrat.getIdCentre()));
            idEmploye.setValue(contratRepository.getEmployeNameById(contrat.getIdEmploye()));
            DateDebut.setValue(contrat.getDateDebut());
            DateFin.setValue(contrat.getDateFin());
            afficherSignature(contrat.getId());
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur lors du chargement des données du contrat.");
        }
    }

    @FXML
    void Ajouter(ActionEvent event) {
        String centreNom = idCentre.getValue();
        String employeNom = idEmploye.getValue();
        LocalDate dateDebut = DateDebut.getValue();
        LocalDate dateFin = DateFin.getValue();

        try {
            if (centreNom == null || employeNom == null || dateDebut == null || dateFin == null) {
                showAlert("Erreur : Tous les champs doivent être remplis.");
                return;
            }

            if (dateDebut.isAfter(dateFin)) {
                showAlert("Erreur : La date de début doit être antérieure à la date de fin.");
                return;
            }

            if (signatureIsEmpty()) {
                showAlert("Erreur : Vous devez signer avant d'ajouter le contrat.");
                return;
            }

            Integer centreId = contratRepository.getCentreIdByName(centreNom);
            Integer employeId = contratRepository.getEmployeIdByName(employeNom);

            if (centreId == null || employeId == null) {
                showAlert("Erreur : Centre ou employé introuvable.");
                return;
            }

            if (contratRepository.existeContrat(centreId, employeId, dateDebut, dateFin)) {
                showAlert("Erreur : Ce contrat existe déjà dans la base de données.");
                return;
            }

            Contrat nouveauContrat = new Contrat(centreId, employeId, dateDebut, dateFin, null);
            contratRepository.ajouter(nouveauContrat);

            int dernierId = contratRepository.getLastInsertedContratId();
            String signaturePath = enregistrerSignature(dernierId);
            if (signaturePath != null) {
                contratRepository.updateSignaturePath(dernierId, signaturePath);
            } else {
                showAlert("Erreur : Impossible d'enregistrer la signature.");
                return;
            }

            String recipientEmail = contratRepository.getEmployeEmailById(employeId);
            if (recipientEmail != null && !recipientEmail.isEmpty()) {
                String pdfPath = generatePDF(nouveauContrat);
                if (pdfPath != null) {
                    String subject = "[Contrat] Détails de votre contrat";
                    String message = "Bonjour " + employeNom + ",\n\n" +
                            "Veuillez trouver ci-joint les détails de votre contrat.\n\n" +
                            "Cordialement,\nL'équipe E-WASTE";
                    // Uncomment if mail sending is implemented
                    // MailRepository.sendEmail(recipientEmail, subject, message, pdfPath);
                }
            }

            loadData();
            idCentre.setValue(null);
            idEmploye.setValue(null);
            DateDebut.setValue(null);
            DateFin.setValue(null);
            GraphicsContext gc = signature.getGraphicsContext2D();
            gc.setFill(Color.WHITE);
            gc.fillRect(0, 0, signature.getWidth(), signature.getHeight());
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur : Une erreur s'est produite lors de l'ajout du contrat.");
        }
    }

    @FXML
    void Modifier(ActionEvent event) {
        if (contratToModify != null) {
            try {
                String newCentre = idCentre.getValue();
                String newEmploye = idEmploye.getValue();
                LocalDate newDateDebut = DateDebut.getValue();
                LocalDate newDateFin = DateFin.getValue();

                if (newCentre == null || newEmploye == null || newDateDebut == null || newDateFin == null) {
                    showAlert("Veuillez remplir tous les champs avant de modifier.");
                    return;
                }

                if (newDateDebut.isAfter(newDateFin)) {
                    showAlert("La date de début doit être antérieure à la date de fin.");
                    return;
                }

                int newCentreId = contratRepository.getCentreIdByName(newCentre);
                int newEmployeId = contratRepository.getEmployeIdByName(newEmploye);

                boolean isSameData = contratToModify.getIdCentre() == newCentreId &&
                        contratToModify.getIdEmploye() == newEmployeId &&
                        contratToModify.getDateDebut().equals(newDateDebut) &&
                        contratToModify.getDateFin().equals(newDateFin);

                boolean hasSignature = contratToModify.getSignaturePath() != null;

                if (isSameData && hasSignature && signatureIsEmpty()) {
                    showAlert("Aucune modification détectée. Veuillez modifier au moins un champ ou signer à nouveau.");
                    return;
                }

                contratToModify.setIdCentre(newCentreId);
                contratToModify.setIdEmploye(newEmployeId);
                contratToModify.setDateDebut(newDateDebut);
                contratToModify.setDateFin(newDateFin);

                if (!signatureIsEmpty()) {
                    String path = enregistrerSignature(contratToModify.getId());
                    if (path != null) {
                        contratToModify.setSignaturePath(path);
                        contratRepository.updateSignaturePath(contratToModify.getId(), path);
                    } else {
                        showAlert("Erreur lors de l'enregistrement de la signature !");
                        return;
                    }
                }

                contratRepository.modifier(contratToModify);
                loadData();
                afficherSignature(contratToModify.getId());
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Erreur lors de la modification du contrat.");
            }
        } else {
            showAlert("Aucun contrat sélectionné.");
        }
    }

    @FXML
    void ModifierContrat(ActionEvent event) {
        if (contratToModify != null) {
            try {
                String newCentre = idCentre.getValue();
                String newEmploye = idEmploye.getValue();
                LocalDate newDateDebut = DateDebut.getValue();
                LocalDate newDateFin = DateFin.getValue();

                if (newCentre == null || newEmploye == null || newDateDebut == null || newDateFin == null) {
                    showAlert("Veuillez remplir tous les champs avant de modifier.");
                    return;
                }

                if (newDateDebut.isAfter(newDateFin)) {
                    showAlert("La date de début doit être antérieure à la date de fin.");
                    return;
                }

                int newCentreId = contratRepository.getCentreIdByName(newCentre);
                int newEmployeId = contratRepository.getEmployeIdByName(newEmploye);

                boolean isSameData = contratToModify.getIdCentre() == newCentreId &&
                        contratToModify.getIdEmploye() == newEmployeId &&
                        contratToModify.getDateDebut().equals(newDateDebut) &&
                        contratToModify.getDateFin().equals(newDateFin);

                boolean hasSignature = contratToModify.getSignaturePath() != null;

                if (isSameData && hasSignature && signatureIsEmpty()) {
                    showAlert("Aucune modification détectée. Veuillez modifier au moins un champ ou signer à nouveau.");
                    return;
                }

                contratToModify.setIdCentre(newCentreId);
                contratToModify.setIdEmploye(newEmployeId);
                contratToModify.setDateDebut(newDateDebut);
                contratToModify.setDateFin(newDateFin);

                if (!signatureIsEmpty()) {
                    String path = enregistrerSignature(contratToModify.getId());
                    if (path != null) {
                        contratToModify.setSignaturePath(path);
                        contratRepository.updateSignaturePath(contratToModify.getId(), path);
                    } else {
                        showAlert("Erreur lors de l'enregistrement de la signature !");
                        return;
                    }
                }

                contratRepository.modifier(contratToModify);
                showAlert("Succès Le contrat a été modifié avec succès.");
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.close(); // Close the modal window
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Erreur lors de la modification du contrat.");
            }
        } else {
            showAlert("Aucun contrat sélectionné.");
        }
    }

    @FXML
    void Supprimer(ActionEvent event) {
        Contrat contratSelectionne = afficher.getSelectionModel().getSelectedItem();

        if (contratSelectionne != null) {
            try {
                String signaturePath = contratSelectionne.getSignaturePath();
                contratRepository.supprimer(contratSelectionne.getId());

                if (signaturePath != null) {
                    File signatureFile = new File(signaturePath);
                    if (signatureFile.exists()) {
                        if (signatureFile.delete()) {
                            System.out.println("✅ Signature supprimée : " + signaturePath);
                        } else {
                            System.err.println("❌ Échec de la suppression de la signature.");
                        }
                    }
                }

                afficher.getItems().remove(contratSelectionne);
                contratToModify = null; // Clear contratToModify after deletion
                GraphicsContext gc = signature.getGraphicsContext2D();
                gc.setFill(Color.WHITE);
                gc.fillRect(0, 0, signature.getWidth(), signature.getHeight());
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Erreur lors de la suppression du contrat.");
            }
        } else {
            showAlert("Erreur : Aucun contrat sélectionné pour la suppression.");
        }
    }

    @FXML
    void GoToCentre(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ewaste/views/Afficher_Centre.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static final String SIGNATURE_FOLDER = "signatures";

    private String enregistrerSignature(int contratId) {
        try {
            File signatureFile = new File("signatures/contrat_" + contratId + ".png");
            if (!signatureFile.getParentFile().exists()) {
                signatureFile.getParentFile().mkdirs();
            }
            WritableImage image = signature.snapshot(null, null);
            BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
            ImageIO.write(bufferedImage, "png", signatureFile);
            return signatureFile.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean signatureIsEmpty() {
        WritableImage snapshot = new WritableImage((int) signature.getWidth(), (int) signature.getHeight());
        signature.snapshot(null, snapshot);
        for (int y = 0; y < snapshot.getHeight(); y++) {
            for (int x = 0; x < snapshot.getWidth(); x++) {
                if (snapshot.getPixelReader().getColor(x, y).getOpacity() > 0) {
                    return false;
                }
            }
        }
        return true;
    }

    private void afficherSignature(int contratId) {
        File file = new File(SIGNATURE_FOLDER + "/contrat_" + contratId + ".png");
        if (file.exists()) {
            try {
                javafx.scene.image.Image image = new javafx.scene.image.Image(file.toURI().toString());
                GraphicsContext gc = signature.getGraphicsContext2D();
                gc.setFill(Color.WHITE);
                gc.fillRect(0, 0, signature.getWidth(), signature.getHeight());
                gc.drawImage(image, 0, 0, signature.getWidth(), signature.getHeight());
                signatureAffichee = true;
                System.out.println("✅ Signature chargée depuis : " + file.getAbsolutePath());
            } catch (Exception e) {
                System.err.println("❌ Erreur lors du chargement de la signature !");
                e.printStackTrace();
            }
        } else {
            System.out.println("⚠️ Aucune signature trouvée pour le contrat " + contratId);
            signatureAffichee = false;
            GraphicsContext gc = signature.getGraphicsContext2D();
            gc.setFill(Color.WHITE);
            gc.fillRect(0, 0, signature.getWidth(), signature.getHeight());
        }
    }

    public String generatePDF(Contrat contrat) {
        try {
            int lastId = contratRepository.getLastInsertedContratId();
            if (lastId == -1) {
                showAlert("Aucun contrat trouvé !");
                return null;
            }

            String filePath = "contrats/contrat_" + lastId + ".pdf";
            File file = new File(filePath);
            file.getParentFile().mkdirs();

            PdfWriter writer = new PdfWriter(new FileOutputStream(file));
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document = new Document(pdfDocument);

            PdfFont titleFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            PdfFont normalFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
            PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);

            // Ajouter le contenu
            addTitle(document, titleFont);
            addCompanyInfo(document, normalFont, boldFont);
            addEmployeeInfo(document, contrat, normalFont, boldFont);
            addContractDetails(document, contrat, normalFont, boldFont);
            addSignatures(document, contrat, normalFont, boldFont);

            document.close();
            System.out.println("✅ PDF généré : " + filePath);
            return filePath;
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            showAlert("Erreur lors de la génération du PDF.");
            return null;
        }
    }

    private void addTitle(Document document, PdfFont titleFont) {
        document.add(new Paragraph("Contrat de travail à durée déterminée").setFont(titleFont).setFontSize(16));
        document.add(new Paragraph("\n"));
    }

    private void addCompanyInfo(Document document, PdfFont normalFont, PdfFont boldFont) {
        document.add(new Paragraph("ENTRE :").setFont(boldFont));
        document.add(new Paragraph("E-WASTE, société à responsabilité limitée au capital de 10 000 euros,").setFont(normalFont));
        document.add(new Paragraph("inscrite au R.C.S. de Tunis sous le numéro 123 456 789,").setFont(normalFont));
        document.add(new Paragraph("dont le siège social est situé à ESPRIT Ghazela, Tunis,").setFont(normalFont));
        document.add(new Paragraph("(ci-après désigné l’« Employeur »)").setFont(normalFont));
        document.add(new Paragraph("\nD’UNE PART,").setFont(boldFont));
        document.add(new Paragraph("\n"));
    }

    private void addEmployeeInfo(Document document, Contrat contrat, PdfFont normalFont, PdfFont boldFont) throws SQLException {
        document.add(new Paragraph("ET :").setFont(boldFont));

        String employeNom = contratRepository.getEmployeNameById(contrat.getIdEmploye());
        String employePrenom = contratRepository.getEmployePrenameById(contrat.getIdEmploye());
        String employMail = contratRepository.getEmployeEmailById(contrat.getIdEmploye());
        String employTelephone = contratRepository.getEmployTelephoneById(contrat.getIdEmploye());

        document.add(new Paragraph("L'employé : " + employeNom + " " + employePrenom).setFont(normalFont));
        document.add(new Paragraph("Avec Mail : " + employMail).setFont(normalFont));
        document.add(new Paragraph("Téléphone : " + employTelephone).setFont(normalFont));
        document.add(new Paragraph("(ci-après désigné le « Salarié »),").setFont(normalFont));
        document.add(new Paragraph("\nD’AUTRE PART,").setFont(boldFont));
        document.add(new Paragraph("(ci-après collectivement désignés les « Parties »),").setFont(normalFont));
        document.add(new Paragraph("\n"));
    }

    private void addContractDetails(Document document, Contrat contrat, PdfFont normalFont, PdfFont boldFont) throws SQLException {
        document.add(new Paragraph("IL A ÉTÉ CONVENU CE QUI SUIT :").setFont(boldFont));
        document.add(new Paragraph("\nArticle 1 : ENGAGEMENT ET DURÉE DU CONTRAT").setFont(boldFont));
        document.add(new Paragraph("Le présent contrat est conclu dans le cadre d'un contrat à durée déterminée.").setFont(normalFont));
        String duree = contratRepository.getContratDatesById(contrat.getIdEmploye()).toString();
        document.add(new Paragraph(duree).setFont(normalFont));
        document.add(new Paragraph("\nArticle 2 : RÉMUNÉRATION").setFont(boldFont));
        document.add(new Paragraph("En contrepartie de son travail, le Salarié percevra une rémunération brute mensuelle de :").setFont(normalFont));
        document.add(new Paragraph("\nArticle 3 : LIEU ET HORAIRES DE TRAVAIL").setFont(boldFont));
        document.add(new Paragraph("Le Salarié exercera ses fonctions à l’adresse suivante :").setFont(normalFont));
        document.add(new Paragraph("\nFait à Tunis, le " + LocalDate.now()).setFont(normalFont));
        document.add(new Paragraph("\n"));
    }

    private void addSignatures(Document document, Contrat contrat, PdfFont normalFont, PdfFont boldFont) {
        document.add(new Paragraph("Signature de l’Employeur :").setFont(boldFont));
        document.add(new Paragraph("\n\n\n"));
        document.add(new Paragraph("Signature du Salarié :").setFont(boldFont));
        document.add(new Paragraph("\n\n\n"));

        String signaturePath = contrat.getSignaturePath();
        if (signaturePath != null && !signaturePath.isEmpty()) {
            File signatureFile = new File(signaturePath);
            if (signatureFile.exists()) {
                try {
                    ImageData imageData = ImageDataFactory.create(signatureFile.getAbsolutePath());
                    Image signatureImage = new Image(imageData);

                    // Utiliser setWidth() et setHeight() au lieu de scaleToFit()
                    signatureImage.setWidth(150);
                    signatureImage.setHeight(50);

                    document.add(new Paragraph("\n\nSignature :").setFont(normalFont));
                    document.add(signatureImage);
                } catch (IOException e) {
                    System.err.println("Erreur lors du chargement de l'image de signature.");
                }

            }
        }
    }

    private void clearForm() {
        idCentre.setValue(null);
        idEmploye.setValue(null);
        DateDebut.setValue(null);
        DateFin.setValue(null);
        GraphicsContext gc = signature.getGraphicsContext2D();
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, signature.getWidth(), signature.getHeight());
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Avertissement");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void checkEndingContracts() throws SQLException {
        LocalDate today = LocalDate.now();
        LocalDate twoDaysLater = today.plusDays(2);
        List<Contrat> contrats = contratRepository.afficher();

        for (Contrat contrat : contrats) {
            LocalDate dateFin = contrat.getDateFin();
            if (!dateFin.isBefore(today) && !dateFin.isAfter(twoDaysLater)) {
                String employeEmail = contratRepository.getEmployeEmailById(contrat.getIdEmploye());
                if (employeEmail != null) {
                    sendExpirationEmail(employeEmail, contrat);
                }
            }
        }
    }

    private void sendExpirationEmail(String email, Contrat contrat) throws SQLException {
        String subject = "Votre contrat arrive bientôt à sa fin";
        String body = "Bonjour " + contratRepository.getEmployeNameById(contrat.getIdEmploye()) + " " +
                contratRepository.getEmployePrenameById(contrat.getIdEmploye()) +
                ",\n\nVotre contrat avec le centre " + contratRepository.getCentreNameById(contrat.getIdCentre()) +
                " se termine le " + contrat.getDateFin() + ".\n\nVeuillez prendre les mesures nécessaires.";
        // Uncomment if mail sending is implemented
        // MailRepository.sendEmailWithoutAttachment(email, subject, body);
    }
}

