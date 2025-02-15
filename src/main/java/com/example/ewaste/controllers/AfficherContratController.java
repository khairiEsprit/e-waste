package com.example.ewaste.controllers;

import com.example.ewaste.entities.Contrat;
import com.example.ewaste.repository.ContratRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class AfficherContratController {

    @FXML
    private DatePicker DateFin;

    @FXML
    private ComboBox<Integer> idCentre;

    @FXML
    private Button btnModifier;

    @FXML
    private ListView<Contrat> afficher;

    @FXML
    private DatePicker DateDebut;

    @FXML
    private Button btnAjouter;

    @FXML
    private ComboBox<Integer> idEmploye;

    @FXML
    private Button btnSupprimer;

    private ContratRepository contratRepository = new ContratRepository();

    @FXML
    public void initialize() {
        // Charger les données dans la ListView
        loadData();

        // Ajouter les éléments pour les ComboBoxes (idCentre et idEmploye)
        loadComboBoxData();

        // Appliquer l'affichage personnalisé des cellules
        afficher.setCellFactory(param -> new ContratListCellController());

        // Configurer les actions des boutons
        btnAjouter.setOnAction(this::Ajouter);
        btnModifier.setOnAction(this::Modifier);
        btnSupprimer.setOnAction(this::Supprimer);
    }

    private void loadComboBoxData() {
        try {
            List<Integer> centreList = contratRepository.getCentreIds();
            List<Integer> employeList = contratRepository.getEmployeIds();

            idCentre.setItems(FXCollections.observableArrayList(centreList));
            idEmploye.setItems(FXCollections.observableArrayList(employeList));
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
        }
    }

    @FXML
    void Ajouter(ActionEvent event) {
        Integer centreId = idCentre.getValue();
        Integer employeId = idEmploye.getValue();
        LocalDate dateDebut = DateDebut.getValue();
        LocalDate dateFin = DateFin.getValue();

        try {
            if (contratRepository.existeContrat(centreId, employeId, dateDebut, dateFin)) {
                showAlert("Ce contrat existe déjà dans la base de données.");
            } else {
                Contrat nouveauContrat = new Contrat(centreId, employeId, dateDebut, dateFin);
                contratRepository.ajouter(nouveauContrat);
                loadData();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Avertissement");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /*@FXML
    void Modifier(ActionEvent event) {
        Contrat contratSelectionne = afficher.getSelectionModel().getSelectedItem();

        if (contratSelectionne != null) {
            contratSelectionne.setIdCentre(idCentre.getValue());
            contratSelectionne.setIdEmploye(idEmploye.getValue());
            contratSelectionne.setDateDebut(DateDebut.getValue());
            contratSelectionne.setDateFin(DateFin.getValue());

            try {
                contratRepository.modifier(contratSelectionne);
                loadData();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }*/

    @FXML
    void Modifier(ActionEvent event) {
        Contrat contratSelectionne = afficher.getSelectionModel().getSelectedItem();

        if (contratSelectionne != null) {
            try {
                // Vérifier si toutes les valeurs sont bien sélectionnées
                if (idCentre.getValue() == null || idEmploye.getValue() == null || DateDebut.getValue() == null || DateFin.getValue() == null) {
                    showAlert("Veuillez remplir tous les champs avant de modifier.");
                    return;
                }

                // Mettre à jour l'objet en mémoire
                contratSelectionne.setIdCentre(idCentre.getValue());
                contratSelectionne.setIdEmploye(idEmploye.getValue());
                contratSelectionne.setDateDebut(DateDebut.getValue());
                contratSelectionne.setDateFin(DateFin.getValue());

                // Mise à jour de la base de données
                contratRepository.modifier(contratSelectionne);

                // Rafraîchir la ListView
                loadData();

                // Afficher un message de succès
                showAlert("Contrat modifié avec succès !");
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
                contratRepository.supprimer(contratSelectionne.getId());
                afficher.getItems().remove(contratSelectionne);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    void GoToCentre(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ewaste/views/Afficher_Centre.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow(); // Récupérer la fenêtre actuelle
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    }

