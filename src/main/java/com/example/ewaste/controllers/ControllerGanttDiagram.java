package com.example.ewaste.controllers;

import com.example.ewaste.entities.Tache;
import com.example.ewaste.entities.PlanificationTache;
import com.example.ewaste.repository.TacheRepository;
import com.example.ewaste.repository.PlanificationTacheRepository;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import java.net.URL;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class ControllerGanttDiagram implements Initializable {

    @FXML
    private LineChart<String, Number> ganttChart; // Graphique pour afficher les tâches

    @FXML
    private CategoryAxis xAxis; // Axe des X pour les noms des tâches

    @FXML
    private NumberAxis yAxis; // Axe des Y pour la durée en jours

    private final TacheRepository serviceTache = new TacheRepository(); // Service pour les tâches
    private final PlanificationTacheRepository servicePlannification = new PlanificationTacheRepository(); // Service pour les planifications

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialisation des axes du graphique
        xAxis.setLabel("Tâches"); // Libellé de l'axe des X
        yAxis.setLabel("Durée (Jours)"); // Libellé de l'axe des Y

        // Charger les données pour le diagramme de Gantt
        try {
            loadGanttChartData();
        } catch (SQLException e) {
            e.printStackTrace(); // Afficher les erreurs SQL
        }
    }

    private void loadGanttChartData() throws SQLException {
        // Récupérer la liste des tâches depuis la base de données
        List<Tache> taches = serviceTache.afficher(1); // Remplacez "1" par l'ID du centre si nécessaire
        System.out.println("Tâches chargées : " + taches);

        // Créer une série de données pour le graphique
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Tâches"); // Nom de la série

        Date currentDate = new Date(); // Date actuelle pour calculer la durée

        for (Tache tache : taches) {
            // Récupérer les informations de la tâche
            String taskName = tache.getMessage(); // Nom de la tâche
            Integer idTache = tache.getId(); // ID de la tâche

            // Récupérer l'objet PlanificationTache lié à cette tâche
            PlanificationTache planification = servicePlannification.afficherPlannification(); // Méthode à implémenter

            // Vérifier que la planification existe
            if (planification != null) {
                Date deadline = planification.getDate_limite();

                // Afficher les dates pour déboguer
                System.out.println("Date actuelle : " + currentDate);
                System.out.println("Date limite : " + deadline);

                // Vérifier que la date limite est dans le futur
                if (deadline.after(currentDate)) {
                    // Calcul de la durée en jours (arrondi à l'entier supérieur)
                    long duration = (long) Math.ceil((deadline.getTime() - currentDate.getTime()) / (1000.0 * 60 * 60 * 24));

                    // Ajouter les données de la tâche au graphique
                    series.getData().add(new XYChart.Data<>(taskName, duration));

                    // Afficher les données dans la console pour déboguer
                    System.out.println("Tâche : " + taskName + ", Durée : " + duration + " jours");
                } else {
                    System.out.println("La date limite de la tâche " + taskName + " est dans le passé.");
                }
            } else {
                System.out.println("Aucune planification trouvée pour la tâche : " + taskName);
                // Ajouter une durée de 0 pour les tâches sans planification
                series.getData().add(new XYChart.Data<>(taskName, 0));
            }
        }

        // Ajouter la série de données au diagramme de Gantt
        ganttChart.getData().add(series);

        // Configurer l'axe des Y pour afficher correctement les durées
        yAxis.setAutoRanging(true); // Ajustement automatique de la plage
    }
}