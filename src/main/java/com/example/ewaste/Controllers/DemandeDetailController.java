package com.example.ewaste.Controllers;

import com.example.ewaste.Entities.Demande;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import java.io.FileOutputStream;
import java.io.IOException;

public class DemandeDetailController {
    @FXML
    private Label lblutilisateur_id;
    @FXML
    private Label lblIdCentre;
    @FXML
    private Label lblAdresse;
    @FXML
    private Label lblEmailUtilisateur;
    @FXML
    private Label lblMessage;
    @FXML
    private Label lblType;

    private Demande demande;

    // Method to set Demande details
    public void setDemandeDetails(Demande demande) {
        this.demande = demande;
        lblutilisateur_id.setText("ID Utilisateur: " + demande.getutilisateur_id());
        lblIdCentre.setText("ID Centre: " + demande.getcentre_id());
        lblAdresse.setText("Adresse: " + demande.getAdresse());
        lblEmailUtilisateur.setText("Email: " + demande.getEmailUtilisateur());
        lblMessage.setText("Message: " + demande.getMessage());
        lblType.setText("Type: " + demande.getType());
    }

    // Method to generate PDF for the specific demande
    @FXML
    private void generatePDFForDemande() {
        if (demande == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Aucune demande sélectionnée");
            alert.setContentText("Veuillez sélectionner une demande pour générer un PDF.");
            alert.showAndWait();
            return;
        }

        Document document = new Document();
        try {
            // Specify the file path where the PDF will be saved
            String fileName = "Demande_" + demande.getId() + ".pdf";
            PdfWriter.getInstance(document, new FileOutputStream(fileName));
            document.open();

            // Add a title with custom font and styling
            com.itextpdf.text.Font titleFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 18, com.itextpdf.text.Font.BOLD);
            Paragraph title = new Paragraph("Détail de la Demande", titleFont);
            title.setAlignment(Paragraph.ALIGN_CENTER);
            title.setSpacingAfter(20); // Add space after the title
            document.add(title);

            // Create a table with 2 columns (Field and Value)
            PdfPTable pdfTable = new PdfPTable(2);
            pdfTable.setWidthPercentage(100); // Table width as 100% of the page
            pdfTable.setSpacingBefore(10); // Add space before the table
            pdfTable.setSpacingAfter(10); // Add space after the table

            // Define custom fonts for headers and cells
            com.itextpdf.text.Font headerFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 12, com.itextpdf.text.Font.BOLD);
            com.itextpdf.text.Font cellFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 10);

            // Ajoute chaque information de la demande dans le tableau.
            addRowToTable(pdfTable, "ID Utilisateur", String.valueOf(demande.getutilisateur_id()), headerFont, cellFont);
            addRowToTable(pdfTable, "ID Centre", String.valueOf(demande.getcentre_id()), headerFont, cellFont);
            addRowToTable(pdfTable, "Adresse", demande.getAdresse(), headerFont, cellFont);
            addRowToTable(pdfTable, "Email Utilisateur", demande.getEmailUtilisateur(), headerFont, cellFont);
            addRowToTable(pdfTable, "Message", demande.getMessage(), headerFont, cellFont);
            addRowToTable(pdfTable, "Type", demande.getType(), headerFont, cellFont);

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
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText("PDF généré avec succès");
            alert.setContentText("Le fichier PDF a été enregistré sous : " + fileName);
            alert.showAndWait();
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Erreur lors de la génération du PDF");
            alert.setContentText("Une erreur s'est produite lors de la génération du PDF.");
            alert.showAndWait();
        }
    }

    // Helper method to add a row to the PDF table
    private void addRowToTable(PdfPTable table, String field, String value, com.itextpdf.text.Font headerFont, com.itextpdf.text.Font cellFont) {
        PdfPCell cell;

        cell = new PdfPCell(new Phrase(field, headerFont));
        cell.setBackgroundColor(new com.itextpdf.text.BaseColor(200, 200, 200)); // Light gray background
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(value, cellFont));
        table.addCell(cell);
    }
}