package com.example.ewaste.controllers;

import com.example.ewaste.utils.DataBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

import com.example.ewaste.entities.Centre;
import com.example.ewaste.repository.CentreRepository;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class AfficherCentreController {

    @FXML
    private ListView<Centre> affichage;

    @FXML
    private TextField TelephoneCentre;

    @FXML
    private TextField LongitudeCentre;

    @FXML
    private TextField AltitudeCentre;

    @FXML
    private TextField NomCentre;

    @FXML
    private TextField EmailCentre;

    private CentreRepository centreRepository = new CentreRepository();

    @FXML
    public void initialize() {
        loadData();
        affichage.setCellFactory(param -> new CentreListCellController());

        affichage.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                NomCentre.setText(newSelection.getNom());
                LongitudeCentre.setText(String.valueOf(newSelection.getLongitude()));
                AltitudeCentre.setText(String.valueOf(newSelection.getLatitude()));
                TelephoneCentre.setText(String.valueOf(newSelection.getTelephone()));
                EmailCentre.setText(newSelection.getEmail());
            }
        });
    }



    private void loadData() {
        try {
            List<Centre> centreList = centreRepository.afficher();
            ObservableList<Centre> observableCentres = FXCollections.observableArrayList(centreList);
            affichage.setItems(observableCentres);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors du chargement des données.");
        }
    }

    public void Supprimer(ActionEvent actionEvent) {
        Centre centreSelectionne = affichage.getSelectionModel().getSelectedItem();

        if (centreSelectionne != null) {
            try {
                centreRepository.supprimer(centreSelectionne.getId());
                affichage.getItems().remove(centreSelectionne);
                System.out.println("Centre supprimé : " + centreSelectionne);
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Erreur", "Erreur lors de la suppression du centre.");
            }
        } else {
            showAlert("Erreur", "Aucun centre sélectionné pour la suppression.");
        }
    }

    @FXML
    void ModifierCentre(ActionEvent event) {
        Centre selectedCentre = affichage.getSelectionModel().getSelectedItem();
        if (selectedCentre != null) {
            if (validateInputs()) {
                selectedCentre.setNom(NomCentre.getText());
                selectedCentre.setLongitude(Float.parseFloat(LongitudeCentre.getText()));
                selectedCentre.setLatitude(Float.parseFloat(AltitudeCentre.getText()));
                selectedCentre.setTelephone(Integer.parseInt(TelephoneCentre.getText()));
                selectedCentre.setEmail(EmailCentre.getText());

                String sql = "UPDATE centre SET nom=?, longitude=?, altitude=?, telephone=?, email=? WHERE id=?";
                try (Connection conn = DataBase.getInstance().getConnection();
                     PreparedStatement stmt = conn.prepareStatement(sql)) {

                    stmt.setString(1, selectedCentre.getNom());
                    stmt.setFloat(2, selectedCentre.getLongitude());
                    stmt.setFloat(3, selectedCentre.getLatitude());
                    stmt.setInt(4, selectedCentre.getTelephone());
                    stmt.setString(5, selectedCentre.getEmail());
                    stmt.setInt(6, selectedCentre.getId());

                    stmt.executeUpdate();
                    showAlert("Succès", "Le centre a été modifié avec succès.");

                    loadData();
                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert("Erreur", "Erreur lors de la modification du centre.");
                }
            } else {
                System.out.println("Les données sont invalides.");
            }
        } else {
            showAlert("Erreur", "Veuillez sélectionner un centre à modifier.");
        }
    }

    @FXML
    void AjouterCentre(ActionEvent event) {

        if (validateInputs()) {
            String nom = NomCentre.getText();
            float longitude = Float.parseFloat(LongitudeCentre.getText());
            float altitude = Float.parseFloat(AltitudeCentre.getText());
            String email = EmailCentre.getText();
            String phoneText = TelephoneCentre.getText();

            try {
                int telephone = Integer.parseInt(phoneText);

                if (centreRepository.existeCentre(altitude,longitude)) {
                    showAlert("Erreur", "Un centre avec ces coordonnées existe déjà !");
                    return;
                }

                Centre centre = new Centre(nom, longitude, altitude, telephone, email);
                centreRepository.ajouter(centre);

                NomCentre.clear();
                LongitudeCentre.clear();
                AltitudeCentre.clear();
                EmailCentre.clear();
                TelephoneCentre.clear();

                loadData();
                System.out.println("Centre ajouté : " + centre);

            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Erreur", "Erreur lors de l'ajout du centre.");
            } catch (NumberFormatException e) {
                e.printStackTrace();
                showAlert("Erreur", "Veuillez entrer un numéro de téléphone et des coordonnées valides.");
            }
        }
    }

    private boolean validateInputs() {
        String nom = NomCentre.getText();
        String longitudeText = LongitudeCentre.getText();
        String altitudeText = AltitudeCentre.getText();
        String email = EmailCentre.getText();
        String phoneText = TelephoneCentre.getText();

        if (nom.isEmpty() || longitudeText.isEmpty() || altitudeText.isEmpty() || email.isEmpty() || phoneText.isEmpty()) {
            showAlert("Erreur", "Tous les champs doivent être remplis !");
            return false;
        }

        try {
            if (!nom.matches("[A-Za-z\\s]+")) {
                showAlert("Erreur", "Le nom ne doit contenir que des lettres et des espaces.");
                return false;
            }

            float longitude = Float.parseFloat(longitudeText);
            float altitude = Float.parseFloat(altitudeText);

            if (phoneText.length() != 8) {
                showAlert("Erreur", "Le numéro de téléphone doit contenir 8 chiffres.");
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Veuillez entrer un numéro de téléphone et des coordonnées valides.");
            return false;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showAlert("Erreur", "Veuillez entrer un email valide.");
            return false;
        }

        return true;
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    void GoToContrat(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ewaste/views/Afficher_Contrat.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
