package com.example.ewaste.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import java.util.Timer;
import java.util.TimerTask;

public class SimulationCapteurControllers {

    @FXML
    private Label labelIdPoubelle;

    @FXML
    private Label labelDistance;

    @FXML
    private ProgressBar progressBarRemplissage;

    @FXML
    private Label labelPourcentage;

    private int idPoubelle = 1; // Exemple d'ID de poubelle
    private float hauteurTotale = 100; // Exemple de hauteur totale

    public void initialize() {
        // Simuler la mise à jour des données toutes les 3 secondes
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateUI();
            }
        }, 0, 3000); // Toutes les 3 secondes
    }

    private void updateUI() {
        // Récupérer les données du capteur (simulées ici)
        float distanceMesuree = (float) (Math.random() * hauteurTotale);
        float niveauRemplissage = (hauteurTotale - distanceMesuree) / hauteurTotale;

        // Mettre à jour l'interface
        javafx.application.Platform.runLater(() -> {
            labelIdPoubelle.setText("ID Poubelle : " + idPoubelle);
            labelDistance.setText("Distance Mesurée : " + String.format("%.2f cm", distanceMesuree));
            progressBarRemplissage.setProgress(niveauRemplissage);
            labelPourcentage.setText(String.format("%.2f%%", niveauRemplissage * 100));

            // Changer la couleur de la ProgressBar
            if (niveauRemplissage >= 1.0) {
                progressBarRemplissage.getStyleClass().removeAll("orange", "green");
                progressBarRemplissage.getStyleClass().add("red");
            } else if (niveauRemplissage >= 0.8) {
                progressBarRemplissage.getStyleClass().removeAll("red", "green");
                progressBarRemplissage.getStyleClass().add("orange");
            } else {
                progressBarRemplissage.getStyleClass().removeAll("red", "orange");
                progressBarRemplissage.getStyleClass().add("green");
            }
        });
    }

    @FXML
    private void viderPoubelle() {
        // Réinitialiser le niveau de remplissage
        progressBarRemplissage.setProgress(0);
        labelPourcentage.setText("0%");
        labelDistance.setText("Distance Mesurée : 0 cm");
    }
}