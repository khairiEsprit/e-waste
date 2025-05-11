package com.example.ewaste.Controllers;

import com.example.ewaste.Entities.Centre;
import com.example.ewaste.Entities.Contrat;
import com.example.ewaste.Main;
import com.example.ewaste.Repository.ContratRepository;
import com.example.ewaste.Utils.SendMail;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
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
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class AfficherContratController {

    @FXML
    private ComboBox<String> idCentreMod;

    @FXML
    private ComboBox<String> idEmployeMod;

    @FXML
    private DatePicker DateDebutMod;

    @FXML
    private DatePicker DateFinMod;

    @FXML
    private Canvas signatureMod;

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

    @FXML
    private TextField Recherche;

    private ContratRepository contratRepository = new ContratRepository();

    private boolean signatureAffichee = false;

    private Contrat contratToModify;

    @FXML
    private Button chatbotSubmitButton;

    @FXML
    private TextArea chatbotResponseArea;

    @FXML
    private TextField chatbotQuestionField;

    private static final String QUESTION_FILE = "question.txt";
    private static final String RESPONSE_FILE = "response.txt";

    @FXML
    private VBox mainContent;

    @FXML
    private VBox ajouterForm;

    @FXML
    private VBox modifierForm;

    private static final String BASE_DIR = "C:\\Users\\Asus\\Desktop\\JAVA CORRECTION\\e-waste\\signatures\\signatures";

    @FXML
    public void initialize() throws SQLException {
        loadData();
        loadComboBoxData();
        setupSearch();
        checkEndingContracts();

        if (signature == null) {
            System.out.println("Canvas non trouvé !");
        } else {
            System.out.println("Canvas chargé avec succès !");
            GraphicsContext gc = signature.getGraphicsContext2D();
            gc.setFill(Color.WHITE);
            gc.fillRect(0, 0, signature.getWidth(), signature.getHeight());
            setupSignatureCanvas(signature);
        }

        if (signatureMod == null) {
            System.out.println("Canvas de modification non trouvé !");
        } else {
            System.out.println("Canvas de modification chargé avec succès !");
            GraphicsContext gc = signatureMod.getGraphicsContext2D();
            gc.setFill(Color.WHITE);
            gc.fillRect(0, 0, signatureMod.getWidth(), signatureMod.getHeight());
            setupSignatureCanvas(signatureMod);
        }

        afficher.setCellFactory(param -> new ContratListCellController());

        DateDebut.getEditor().setDisable(true);
        DateDebut.getEditor().setOpacity(1);
        DateFin.getEditor().setDisable(true);
        DateFin.getEditor().setOpacity(1);
        DateDebutMod.getEditor().setDisable(true);
        DateDebutMod.getEditor().setOpacity(1);
        DateFinMod.getEditor().setDisable(true);
        DateFinMod.getEditor().setOpacity(1);

        afficher.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newSelection) -> {
            if (newSelection != null) {
                contratToModify = newSelection;
                afficherDetailsContrat(newSelection);
                afficherSignature(newSelection.getId(), signature);
                showModifierForm(null);
            }
        });
    }

    private void setupSignatureCanvas(Canvas canvas) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        canvas.setOnMousePressed(e -> {
            if (signatureAffichee) {
                gc.setFill(Color.WHITE);
                gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
                signatureAffichee = false;
            }
            gc.beginPath();
            gc.moveTo(e.getX(), e.getY());
            gc.stroke();
        });

        canvas.setOnMouseDragged(e -> {
            gc.lineTo(e.getX(), e.getY());
            gc.stroke();
        });
    }

    @FXML
    private void submitQuestion(ActionEvent event) {
        String question = chatbotQuestionField.getText();
        if (question == null || question.trim().isEmpty()) {
            chatbotResponseArea.setText("Veuillez entrer une question.");
            return;
        }

        try {
            Files.write(Paths.get(QUESTION_FILE), question.getBytes());
            String response = pollForResponse();
            chatbotResponseArea.setText(response.isEmpty() ? "Aucune réponse reçue." : response);
        } catch (IOException e) {
            chatbotResponseArea.setText("Erreur: Impossible d'écrire/lire le fichier - " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String pollForResponse() throws IOException {
        int maxAttempts = 10;
        int attempt = 0;
        long sleepTime = 1000;

        while (attempt < maxAttempts) {
            if (Files.exists(Paths.get(RESPONSE_FILE))) {
                String response = new String(Files.readAllBytes(Paths.get(RESPONSE_FILE)));
                Files.deleteIfExists(Paths.get(RESPONSE_FILE));
                return response.trim();
            }
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return "Erreur: Interruption lors de l'attente de la réponse.";
            }
            attempt++;
        }
        return "Aucune réponse reçue après " + maxAttempts + " tentatives.";
    }

    private void afficherDetailsContrat(Contrat contrat) {
        if (contrat != null) {
            try {
                String centreNom = contratRepository.getCentreNameById(contrat.getCentreId());
                String employeFullName = contratRepository.getEmployePrenameById(contrat.getEmployeId()) + " " +
                        contratRepository.getEmployeNameById(contrat.getEmployeId());
                idCentre.setValue(centreNom);
                idEmploye.setValue(employeFullName);
                DateDebut.setValue(contrat.getDateDebut());
                DateFin.setValue(contrat.getDateFin());
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Erreur lors du chargement des détails du contrat.");
            }
        }
    }

    private void loadComboBoxData() throws SQLException {
        List<Centre> centreList = contratRepository.getCentres();
        ObservableList<String> centreNames = FXCollections.observableArrayList();
        for (Centre centre : centreList) {
            centreNames.add(centre.getNom());
        }
        idCentre.setItems(centreNames);
        idCentreMod.setItems(centreNames);

        List<String> employeNames = contratRepository.getEmployeNames();
        ObservableList<String> employeNamesList = FXCollections.observableArrayList(employeNames);
        idEmploye.setItems(employeNamesList);
        idEmployeMod.setItems(employeNamesList);
    }

    private void loadData() throws SQLException {
        List<Contrat> contratList = contratRepository.afficher();
        ObservableList<Contrat> observableContrats = FXCollections.observableArrayList(contratList);
        afficher.setItems(observableContrats);
    }

    @FXML
    void showAjouterForm(ActionEvent event) {
        mainContent.setEffect(new GaussianBlur(5));
        ajouterForm.setVisible(true);
        ajouterForm.setManaged(true);
        clearForm();
    }

    @FXML
    void hideAjouterForm(ActionEvent event) {
        mainContent.setEffect(null);
        ajouterForm.setVisible(false);
        ajouterForm.setManaged(false);
    }

    @FXML
    void showModifierForm(ActionEvent event) {
        if (contratToModify != null) {
            mainContent.setEffect(new GaussianBlur(5));
            modifierForm.setVisible(true);
            modifierForm.setManaged(true);
            setContratData(contratToModify);
        } else {
            showAlert("Veuillez sélectionner un contrat à modifier.");
        }
    }

    @FXML
    void hideModifierForm(ActionEvent event) {
        mainContent.setEffect(null);
        modifierForm.setVisible(false);
        modifierForm.setManaged(false);
    }

    public void setContratData(Contrat contrat) {
        contratToModify = contrat;
        try {
            idCentreMod.setValue(contratRepository.getCentreNameById(contrat.getCentreId()));
            idEmployeMod.setValue(contratRepository.getEmployePrenameById(contrat.getEmployeId()) + " " +
                    contratRepository.getEmployeNameById(contrat.getEmployeId()));
            DateDebutMod.setValue(contrat.getDateDebut());
            DateFinMod.setValue(contrat.getDateFin());
            afficherSignature(contrat.getId(), signatureMod);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur lors du chargement des données du contrat.");
        }
    }

    @FXML
    void Ajouter(ActionEvent event) {
        String centreNom = idCentre.getValue();
        String employeFullName = idEmploye.getValue();
        LocalDate dateDebut = DateDebut.getValue();
        LocalDate dateFin = DateFin.getValue();

        try {
            if (centreNom == null || employeFullName == null || dateDebut == null || dateFin == null) {
                showAlert("Erreur : Tous les champs doivent être remplis.");
                return;
            }

            if (dateDebut.isAfter(dateFin)) {
                showAlert("Erreur : La date de début doit être antérieure à la date de fin.");
                return;
            }

            if (signatureIsEmpty(signature)) {
                showAlert("Erreur : Vous devez signer avant d'ajouter le contrat.");
                return;
            }

            Integer centreId = contratRepository.getCentreIdByName(centreNom);
            Integer employeId = contratRepository.getEmployeIdByName(employeFullName);

            if (centreId == null || employeId == null) {
                showAlert("Erreur : Centre ou employé introuvable.");
                return;
            }

            if (contratRepository.existeContrat(centreId, employeId, dateDebut, dateFin)) {
                showAlert("Erreur : Ce contrat existe déjà dans la base de données.");
                return;
            }

            // Utiliser un ID temporaire pour sauvegarder la signature avant l'insertion
            int tempId = contratRepository.getLastInsertedContratId() + 1;
            String signaturePath = enregistrerSignature(tempId, signature);
            if (signaturePath == null) {
                showAlert("Erreur : Impossible d'enregistrer la signature.");
                return;
            }

            Contrat nouveauContrat = new Contrat(centreId, employeId, dateDebut, dateFin, signaturePath);
            contratRepository.ajouter(nouveauContrat);

            // Vérifier l'ID réel après insertion
            int dernierId = contratRepository.getLastInsertedContratId();
            if (dernierId != tempId) {
                // Renommer le fichier si l'ID a changé
                File oldFile = new File(signaturePath);
                String newSignaturePath = enregistrerSignature(dernierId, signature);
                if (newSignaturePath != null) {
                    contratRepository.updateSignaturePath(dernierId, newSignaturePath);
                    nouveauContrat.setSignaturePath(newSignaturePath);
                    oldFile.delete();
                } else {
                    contratRepository.supprimer(dernierId);
                    oldFile.delete();
                    showAlert("Erreur : Impossible de renommer la signature.");
                    return;
                }
            }

            String recipientEmail = contratRepository.getEmployeEmailById(employeId);
            if (recipientEmail != null && !recipientEmail.isEmpty()) {
                String pdfPath = generatePDF(nouveauContrat);
                if (pdfPath != null) {
                    String employeName = contratRepository.getEmployePrenameById(employeId) + " " +
                            contratRepository.getEmployeNameById(employeId);
                    String subject = "[Contrat] Détails de votre contrat";
                    String message = "Bonjour " + employeName + ",\n\n" +
                            "Veuillez trouver ci-joint les détails de votre contrat.\n\n" +
                            "Cordialement,\nL'équipe E-WASTE";
                    SendMail.sendEmail(recipientEmail, subject, message, pdfPath);
                }
            }
            hideAjouterForm(event);
            loadData();
            clearForm();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur : Une erreur s'est produite lors de l'ajout du contrat.");
        }
    }

    @FXML
    void ModifierContrat(ActionEvent event) {
        if (contratToModify != null) {
            try {
                String newCentre = idCentreMod.getValue();
                String newEmploye = idEmployeMod.getValue();
                LocalDate newDateDebut = DateDebutMod.getValue();
                LocalDate newDateFin = DateFinMod.getValue();

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

                boolean isSameData = contratToModify.getCentreId() == newCentreId &&
                        contratToModify.getEmployeId() == newEmployeId &&
                        contratToModify.getDateDebut().equals(newDateDebut) &&
                        contratToModify.getDateFin().equals(newDateFin);

                boolean hasSignature = contratToModify.getSignaturePath() != null;

                if (isSameData && hasSignature && signatureIsEmpty(signatureMod)) {
                    showAlert("Aucune modification détectée. Veuillez modifier au moins un champ ou signer à nouveau.");
                    return;
                }

                contratToModify.setCentreId(newCentreId);
                contratToModify.setEmployeId(newEmployeId);
                contratToModify.setDateDebut(newDateDebut);
                contratToModify.setDateFin(newDateFin);

                if (!signatureIsEmpty(signatureMod)) {
                    String oldSignaturePath = contratToModify.getSignaturePath();
                    String path = enregistrerSignature(contratToModify.getId(), signatureMod);
                    if (path != null) {
                        contratToModify.setSignaturePath(path);
                        contratRepository.updateSignaturePath(contratToModify.getId(), path);
                        // Supprimer l'ancienne signature si elle existe
                        if (oldSignaturePath != null) {
                            File oldFile = new File(oldSignaturePath);
                            if (oldFile.exists()) {
                                oldFile.delete();
                            }
                        }
                    } else {
                        showAlert("Erreur lors de l'enregistrement de la signature !");
                        return;
                    }
                }

                contratRepository.modifier(contratToModify);
                hideModifierForm(event);
                loadData();
                showAlert("Succès : Le contrat a été modifié avec succès.");
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
                contratToModify = null;
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
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/Afficher_Centre.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String enregistrerSignature(int contratId, Canvas canvas) {
        try {
            File signatureDir = new File(BASE_DIR);
            if (!signatureDir.exists()) {
                signatureDir.mkdirs();
            }
            File signatureFile = new File(signatureDir, "contrat_" + contratId + ".png");
            WritableImage image = canvas.snapshot(null, null);
            BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
            ImageIO.write(bufferedImage, "png", signatureFile);
            System.out.println("Chemin de signature enregistré : " + signatureFile.getAbsolutePath());
            return signatureFile.getAbsolutePath();
        } catch (IOException e) {
            System.out.println("Erreur lors de l'enregistrement de la signature : " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private boolean signatureIsEmpty(Canvas canvas) {
        WritableImage snapshot = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
        canvas.snapshot(null, snapshot);
        for (int y = 0; y < snapshot.getHeight(); y++) {
            for (int x = 0; x < snapshot.getWidth(); x++) {
                if (!snapshot.getPixelReader().getColor(x, y).equals(Color.WHITE)) {
                    return false;
                }
            }
        }
        return true;
    }

    private void afficherSignature(int contratId, Canvas canvas) {
        File file = new File(BASE_DIR, "contrat_" + contratId + ".png");
        System.out.println("Chemin utilisé pour afficher la signature : " + file.getAbsolutePath());
        GraphicsContext gc = canvas.getGraphicsContext2D();

        if (file.exists()) {
            try {
                Image image = new Image(file.toURI().toString());
                gc.setFill(Color.WHITE);
                gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
                gc.drawImage(image, 0, 0, canvas.getWidth(), canvas.getHeight());
                signatureAffichee = true;
                System.out.println("✅ Signature chargée dans le canvas depuis : " + file.getAbsolutePath());
            } catch (Exception e) {
                System.err.println("❌ Erreur lors du chargement de la signature !");
                e.printStackTrace();
            }
        } else {
            System.out.println("⚠️ Aucune signature trouvée pour le contrat " + contratId);
            signatureAffichee = false;
            gc.setFill(Color.WHITE);
            gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        }
    }

    private String generatePDF(Contrat contrat) {
        try {
            String filePath = BASE_DIR + "\\contrats\\contrat_" + contrat.getId() + ".pdf";
            Document document = new Document();
            File file = new File(filePath);
            file.getParentFile().mkdirs();
            PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();

            Font titleFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
            Font normalFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);
            Font boldFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);

            document.add(new Paragraph("Contrat de travail à durée déterminée", titleFont));
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("ENTRE :", boldFont));
            document.add(new Paragraph("E-WASTE, société à responsabilité limitée au capital de 10 000 euros,", normalFont));
            document.add(new Paragraph("inscrite au R.C.S. de Tunis sous le numéro 123 456 789,", normalFont));
            document.add(new Paragraph("dont le siège social est situé à ESPRIT Ghazela, Tunis,", normalFont));
            document.add(new Paragraph("(ci-après désigné l’« Employeur »)", normalFont));
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("D’UNE PART,", boldFont));
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("ET :", boldFont));

            String employeNom = contratRepository.getEmployeNameById(contrat.getEmployeId());
            String employePrenom = contratRepository.getEmployePrenameById(contrat.getEmployeId());
            String employMail = contratRepository.getEmployeEmailById(contrat.getEmployeId());
            String employTelephone = contratRepository.getEmployTelephoneById(contrat.getEmployeId());

            document.add(new Paragraph("L'employé : " + employePrenom + " " + employeNom, normalFont));
            document.add(new Paragraph("Avec Mail " + employMail, normalFont));
            document.add(new Paragraph("Téléphone " + employTelephone, normalFont));
            document.add(new Paragraph("(ci-après désigné le « Salarié »),", normalFont));
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("D’AUTRE PART,", boldFont));
            document.add(new Paragraph("(ci-après collectivement désignés les « Parties »),", normalFont));
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("IL A ETE CONVENU CE QUI SUIT :", boldFont));
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("Article 1 : ENGAGEMENT ET DUREE DU CONTRAT", boldFont));
            document.add(new Paragraph("Le présent contrat est conclu dans le cadre d'un contrat à durée déterminée", normalFont));
            String duree = contrat.getDateDebut() + " au " + contrat.getDateFin();
            document.add(new Paragraph(duree, normalFont));
            document.add(new Paragraph("L’entreprise exerçant l’activité suivante :", normalFont));
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("Article 2 : REMUNERATION", boldFont));
            document.add(new Paragraph("En contrepartie de son travail, le Salarié percevra une rémunération brute mensuelle de :", normalFont));
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("Article 3 : LIEU ET HORAIRES DE TRAVAIL", boldFont));
            document.add(new Paragraph("Le Salarié exercera ses fonctions à l’adresse suivante :", normalFont));

            document.add(new Paragraph("Signature de l’Employeur :", boldFont));
            String signaturePath = contrat.getSignaturePath();
            if (signaturePath != null && !signaturePath.isEmpty()) {
                System.out.println("Chemin de signature dans generatePDF : " + signaturePath);
                File signatureFile = new File(signaturePath);
                if (signatureFile.exists()) {
                    System.out.println("Signature trouvée à : " + signatureFile.getAbsolutePath());
                    try {
                        com.itextpdf.text.Image signatureImage = com.itextpdf.text.Image.getInstance(signatureFile.getAbsolutePath());
                        signatureImage.scaleToFit(200, 150);
                        signatureImage.setAlignment(Element.ALIGN_CENTER);
                        document.add(new Paragraph("\n\n", normalFont));
                        document.add(signatureImage);
                    } catch (Exception e) {
                        System.err.println("Erreur lors du chargement de la signature : " + e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("Signature non trouvée à : " + signatureFile.getAbsolutePath());
                }
            } else {
                System.out.println("Chemin de signature null ou vide dans generatePDF");
            }
            document.add(new Paragraph("\n\n\n"));
            document.add(new Paragraph("Signature du Salarié :", boldFont));
            document.add(new Paragraph("\n\n\n"));
            document.add(new Paragraph("                                                        Fait à Tunis, le " + LocalDate.now(), normalFont));
            document.add(new Paragraph("\n"));

            document.close();
            System.out.println("✅ PDF généré : " + filePath);
            return filePath;
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur lors de la génération du PDF.");
            return null;
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
                String employeEmail = contratRepository.getEmployeEmailById(contrat.getEmployeId());
                if (employeEmail != null) {
                    sendExpirationEmail(employeEmail, contrat);
                }
            }
        }
    }

    private void sendExpirationEmail(String email, Contrat contrat) throws SQLException {
        String subject = "Votre contrat arrive bientôt à sa fin";
        String body = "Bonjour " + contratRepository.getEmployePrenameById(contrat.getEmployeId()) + " " +
                contratRepository.getEmployeNameById(contrat.getEmployeId()) +
                ",\n\nVotre contrat avec le centre " + contratRepository.getCentreNameById(contrat.getCentreId()) +
                " se termine le " + contrat.getDateFin() + ".\n\nVeuillez prendre les mesures nécessaires.";
        SendMail.sendEmailWithoutAttachment(email, subject, body);
    }

    private void setupSearch() {
        Recherche.textProperty().addListener((observable, oldValue, newValue) -> {
            List<Contrat> filteredContrats;
            try {
                ContratRepository contratRepository = new ContratRepository();
                filteredContrats = contratRepository.afficher().stream()
                        .filter(contrat -> {
                            if (newValue == null || newValue.isEmpty()) {
                                return true;
                            }
                            try {
                                String centreNom = contratRepository.getCentreNameById(contrat.getCentreId());
                                String employePrenom = contratRepository.getEmployePrenameById(contrat.getEmployeId());
                                String employeNom = contratRepository.getEmployeNameById(contrat.getEmployeId());

                                String searchText = newValue.toLowerCase();
                                return (centreNom != null && centreNom.toLowerCase().contains(searchText)) ||
                                        (employeNom != null && employeNom.toLowerCase().contains(searchText)) ||
                                        (employePrenom != null && employePrenom.toLowerCase().contains(searchText));
                            } catch (SQLException e) {
                                e.printStackTrace();
                                return false;
                            }
                        })
                        .toList();
                afficher.setItems(FXCollections.observableArrayList(filteredContrats));
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Erreur lors du filtrage des contrats.");
            }
        });
    }
}