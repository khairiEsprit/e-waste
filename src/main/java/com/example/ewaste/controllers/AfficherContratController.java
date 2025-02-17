    package com.example.ewaste.controllers;

    import com.example.ewaste.entities.Centre;
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

        private ContratRepository contratRepository = new ContratRepository();


        @FXML
        public void initialize() {
            // Charger les données dans la ListView
            loadData();
            loadComboBoxData();

            // Appliquer l'affichage personnalisé des cellules
            afficher.setCellFactory(param -> new ContratListCellController());

            // Configurer les actions des boutons
            btnAjouter.setOnAction(this::Ajouter);
            btnModifier.setOnAction(this::Modifier);
            btnSupprimer.setOnAction(this::Supprimer);

            // Empêcher la saisie manuelle dans les DatePickers
            DateDebut.getEditor().setDisable(true);
            DateDebut.getEditor().setOpacity(1);
            DateFin.getEditor().setDisable(true);
            DateFin.getEditor().setOpacity(1);

            // Ajouter un listener pour détecter la sélection dans la ListView
            afficher.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    afficherDetailsContrat(newValue);
                }
            });
        }
        private void afficherDetailsContrat(Contrat contrat) {
            if (contrat != null) {
                try {
                    // Récupérer les noms du centre et de l'employé à partir de leurs ID
                    String centreNom = contratRepository.getCentreNameById(contrat.getIdCentre());
                    String employeNom = contratRepository.getEmployeNameById(contrat.getIdEmploye());

                    // Remplir les champs avec les valeurs du contrat sélectionné
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


       /* public void initialize() {
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
            DateDebut.getEditor().setDisable(true);
            DateDebut.getEditor().setOpacity(1); // Maintient la visibilité du texte

            DateFin.getEditor().setDisable(true);
            DateFin.getEditor().setOpacity(1);


        }*/


      private void loadComboBoxData() {
          try {
              // Récupération des centres
              List<Centre> centreList = contratRepository.getCentres();
              ObservableList<String> centreNames = FXCollections.observableArrayList();

              for (Centre centre : centreList) {
                  centreNames.add(centre.getNom()); // Ajout des noms des centres
              }
              idCentre.setItems(centreNames);

              // Récupération des employés depuis la table `utilisateur`
              List<String> employeNames = contratRepository.getEmployeNames(); // Récupérer uniquement les noms

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
            }
        }




      @FXML
      void Ajouter(ActionEvent event) {
          String centreNom = idCentre.getValue(); // Récupération du nom du centre
          String employeNom = idEmploye.getValue(); // Récupération du nom de l'employé
          LocalDate dateDebut = DateDebut.getValue();
          LocalDate dateFin = DateFin.getValue();

          try {
              if (dateDebut.isAfter(dateFin)) {
                  showAlert("La date de début doit être antérieure à la date de fin.");
                  return;
              }
              // Trouver l'ID du centre et de l'employé en fonction de leur nom
              Integer centreId = contratRepository.getCentreIdByName(centreNom);
              Integer employeId = contratRepository.getEmployeIdByName(employeNom); // Nouvelle méthode à ajouter

              if (centreId == null || employeId == null) {
                  showAlert("Erreur : Centre ou employé introuvable !");
                  return;
              }

              if (contratRepository.existeContrat(centreId, employeId, dateDebut, dateFin)) {
                  showAlert("Ce contrat existe déjà dans la base de données.");
              } else {
                  Contrat nouveauContrat = new Contrat(centreId, employeId, dateDebut, dateFin);
                  contratRepository.ajouter(nouveauContrat);
                  loadData();
                  idCentre.setValue(null);  // Réinitialiser le ComboBox
                  idEmploye.setValue(null);  // Réinitialiser le ComboBox
                  DateDebut.setValue(null);  // Réinitialiser le DatePicker
                  DateFin.setValue(null);
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
                    if (DateDebut.getValue().isAfter(DateFin.getValue())) {
                        showAlert("La date de début doit être antérieure à la date de fin.");
                        return;
                    }

                    // Récupérer les ID à partir des noms
                    int centreId = contratRepository.getCentreIdByName(idCentre.getValue());
                    int employeId = contratRepository.getEmployeIdByName(idEmploye.getValue());

                    // Mettre à jour l'objet en mémoire avec les ID corrects
                    contratSelectionne.setIdCentre(centreId);
                    contratSelectionne.setIdEmploye(employeId);
                    contratSelectionne.setDateDebut(DateDebut.getValue());
                    contratSelectionne.setDateFin(DateFin.getValue());

                    // Mise à jour de la base de données
                    contratRepository.modifier(contratSelectionne);

                    // Rafraîchir la ListView
                    loadData();
                    idCentre.setValue(null);  // Réinitialiser le ComboBox
                    idEmploye.setValue(null);  // Réinitialiser le ComboBox
                    DateDebut.setValue(null);  // Réinitialiser le DatePicker
                    DateFin.setValue(null);

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

