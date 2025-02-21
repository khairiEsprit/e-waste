package com.example.ewaste.Controllers;

import com.example.ewaste.Entities.Contrat;
import com.example.ewaste.Repository.ContratRepository;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.sql.SQLException;

public class ContratListCellController extends ListCell<Contrat> {

    private ContratRepository contratRepository = new ContratRepository();

    @Override
    protected void updateItem(Contrat contrat, boolean empty) {
        super.updateItem(contrat, empty);

        if (empty || contrat == null) {
            setGraphic(null);
            setText(null);
        } else {
            try {
                // Récupérer les noms du centre et de l'employé à partir de leurs IDs
                String centreNom = contratRepository.getCentreNameById(contrat.getIdCentre());
                String employeNom = contratRepository.getEmployeNameById(contrat.getIdEmploye());

                // Créer des labels pour chaque attribut du Contrat
                Label idLabel = new Label("🆔 ID Contrat: " + contrat.getId());
                Label centreLabel = new Label("🏢 Centre: " + centreNom);  // Afficher le nom du centre
                Label employeLabel = new Label("👤 Employé: " + employeNom);  // Afficher le nom de l'employé
                Label dateDebutLabel = new Label("📅 Début: " + contrat.getDateDebut());
                Label dateFinLabel = new Label("📅 Fin: " + contrat.getDateFin());

                // Appliquer des styles CSS pour améliorer l'affichage
                idLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #888;");
                centreLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");
                employeLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");
                dateDebutLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");
                dateFinLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");

                // Organiser les labels dans une VBox
                VBox vbox = new VBox(idLabel, centreLabel, employeLabel, dateDebutLabel, dateFinLabel);
                vbox.setSpacing(5); // Espacement entre les éléments
                vbox.setPadding(new Insets(10, 10, 10, 10)); // Espacement interne

                // Ajouter un HBox pour le conteneur global de la cellule
                HBox cellContent = new HBox(vbox);
                // Appliquer une couleur de fond et une bordure
                cellContent.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 10px; "
                        + "-fx-border-radius: 10px; -fx-border-color: #00693e; -fx-border-width: 2px;");
                cellContent.setPadding(new Insets(5, 5, 5, 5)); // Espacement de la cellule

                // Définir le contenu graphique de la cellule
                setGraphic(cellContent);

            } catch (SQLException e) {
                e.printStackTrace();
                setGraphic(null);
                setText("Erreur lors du chargement des données");
            }
        }
    }
}
