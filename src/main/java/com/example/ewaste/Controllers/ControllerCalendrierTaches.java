package com.example.ewaste.Controllers;

import com.example.ewaste.Entities.PlanificationTache;
import com.example.ewaste.Repository.PlanificationTacheRepository;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public class ControllerCalendrierTaches {

    @FXML
    private ComboBox<String> moisCombo;

    @FXML
    private ComboBox<String> anneeCombo;

    @FXML
    private GridPane calendrierGrid;

    @FXML
    private Button btnAfficher, btnRafraichir, btnFermer;

    private final PlanificationTacheRepository servicePlanification = new PlanificationTacheRepository();

    @FXML
    private void initialize() throws SQLException {
        // Initialisation des ComboBox
        moisCombo.setItems(FXCollections.observableArrayList(
                "Janvier", "Février", "Mars", "Avril", "Mai", "Juin",
                "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre"
        ));

        List<String> annees = new ArrayList<>();
        int currentYear = LocalDate.now().getYear();
        for (int i = currentYear - 5; i <= currentYear + 5; i++) {
            annees.add(String.valueOf(i));
        }
        anneeCombo.setItems(FXCollections.observableArrayList(annees));

        moisCombo.getSelectionModel().select(LocalDate.now().getMonthValue() - 1); // Index 0-based
        anneeCombo.getSelectionModel().select(String.valueOf(currentYear));

        afficherCalendrier();
    }

    @FXML
    private void afficherCalendrier() throws SQLException {
        calendrierGrid.getChildren().clear();

        // Ajouter les jours de la semaine
        String[] jours = {"Lun", "Mar", "Mer", "Jeu", "Ven", "Sam", "Dim"};
        for (int i = 0; i < 7; i++) {
            Label jour = new Label(jours[i]);
            jour.setStyle("-fx-font-weight: bold;");
            calendrierGrid.add(jour, i, 0);
        }

        // Calculer le mois et l'année sélectionnés
        int moisIndex = moisCombo.getSelectionModel().getSelectedIndex(); // Index basé sur 0
        if (moisIndex < 0) {
            moisIndex = LocalDate.now().getMonthValue() - 1; // Par défaut : mois actuel si rien n'est sélectionné
        }
        int mois = moisIndex + 1; // Mois basé sur 1 (1-12)
        int annee = Integer.parseInt(anneeCombo.getValue() != null ? anneeCombo.getValue() : String.valueOf(LocalDate.now().getYear()));

        YearMonth yearMonth = YearMonth.of(annee, mois);

        LocalDate premierJour = yearMonth.atDay(1);
        int jourSemaineDebut = premierJour.getDayOfWeek().getValue() - 1; // 0 = Lundi

        List<PlanificationTache> taches;
        try {
            taches = servicePlanification.afficher(0);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement des tâches.");
            return;
        }

        // Remplir le calendrier
        int jour = 1;
        for (int row = 1; row <= 6 && jour <= yearMonth.lengthOfMonth(); row++) {
            for (int col = 0; col < 7 && jour <= yearMonth.lengthOfMonth(); col++) {
                if (row == 1 && col < jourSemaineDebut) {
                    continue;
                }

                VBox cell = new VBox();
                cell.setPrefSize(100, 100);
                Label jourLabel = new Label(String.valueOf(jour));
                cell.getChildren().add(jourLabel);

                LocalDate date = LocalDate.of(annee, mois, jour);
                StringBuilder tachesText = new StringBuilder();
                for (PlanificationTache tache : taches) {
                    if (tache.getDate_limite() != null &&
                            tache.getDate_limite().toLocalDate().equals(date)) {
                        String nomTache = servicePlanification.getTacheNomById(tache.getId_tache());
                        Label tacheLabel = new Label(nomTache);
                        cell.getChildren().add(tacheLabel);
                        tachesText.append(nomTache)
                                .append(" (").append(tache.getPriorite()).append(")\n");
                    }
                }

                if (tachesText.length() > 0) {
                    cell.setStyle("-fx-background-color: #ff9999;");
                    Tooltip tooltip = new Tooltip(tachesText.toString());
                    Tooltip.install(cell, tooltip);
                }

                calendrierGrid.add(cell, col, row);
                jour++;
            }
        }
    }

    @FXML
    private void rafraichirCalendrier() throws SQLException {
        afficherCalendrier();
        showAlert(Alert.AlertType.INFORMATION, "Succès", "Calendrier mis à jour.");
    }

    @FXML
    private void fermerFenetre() {
        Stage stage = (Stage) btnFermer.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}