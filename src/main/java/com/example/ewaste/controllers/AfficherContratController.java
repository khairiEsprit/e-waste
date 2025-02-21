    package com.example.ewaste.controllers;

    import com.example.ewaste.entities.Centre;
    import com.example.ewaste.entities.Contrat;
    import com.example.ewaste.repository.ContratRepository;
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
    import javafx.scene.image.Image;
    import javafx.scene.image.WritableImage;
    import javafx.scene.paint.Color;
    import javafx.stage.Stage;

    import javax.imageio.ImageIO;
    import java.awt.image.BufferedImage;
    import java.io.File;
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

        private boolean signatureAffichee = false; // Suivi de l'affichage de la signature

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
                gc.fillRect(0, 0, signature.getWidth(), signature.getHeight()); // Fond blanc

                // Capture du dessin avec la souris
                signature.setOnMousePressed(e -> {
                    if (signatureAffichee) {
                        // Effacer la signature affichée et redessiner le fond blanc
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

            // Définir le mode d'affichage des contrats dans la ListView
            afficher.setCellFactory(param -> new ContratListCellController());

            // Associer les actions aux boutons
            btnAjouter.setOnAction(this::Ajouter);
            btnModifier.setOnAction(this::Modifier);
            btnSupprimer.setOnAction(this::Supprimer);

            // Désactiver la saisie manuelle des dates
            DateDebut.getEditor().setDisable(true);
            DateDebut.getEditor().setOpacity(1);
            DateFin.getEditor().setDisable(true);
            DateFin.getEditor().setOpacity(1);

            // Charger les détails du contrat sélectionné
            afficher.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    afficherDetailsContrat(newValue);
                    afficherSignature(newValue.getId()); // Charger la signature du contrat sélectionné
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
                  showAlert("Erreur Tous les champs doivent etre remplis ");
                  return;
              }

              if (dateDebut.isAfter(dateFin)) {
                  showAlert("La date de début doit être antérieure à la date de fin.");
                  return;
              }
              if (signatureIsEmpty()) {
                  showAlert("Erreur : Vous devez signer avant d'ajouter le contrat.");
                  return;
              }
              Integer centreId = contratRepository.getCentreIdByName(centreNom);
              Integer employeId = contratRepository.getEmployeIdByName(employeNom);

              if (centreId == null || employeId == null) {
                  showAlert("Erreur : Centre ou employé introuvable !");
                  return;
              }

              if (contratRepository.existeContrat(centreId, employeId, dateDebut, dateFin)) {
                  showAlert("Ce contrat existe déjà dans la base de données.");
              } else {
                  Contrat nouveauContrat = new Contrat(centreId, employeId, dateDebut, dateFin,null);
                  contratRepository.ajouter(nouveauContrat);
                  int dernierId = contratRepository.getLastInsertedContratId();

                  if (dernierId != -1) {
                      String signaturePath = enregistrerSignature(dernierId);

                      if (signaturePath != null) {
                          System.out.println("Signature enregistrée à : " + signaturePath); // 🔹 Debug
                          contratRepository.updateSignaturePath(dernierId, signaturePath);
                      } else {
                          showAlert("Erreur lors de l'enregistrement de la signature.");
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

                    // Vérifier si aucune modification n'a été effectuée
                    boolean isSameData = contratSelectionne.getIdCentre() == newCentreId &&
                            contratSelectionne.getIdEmploye() == newEmployeId &&
                            contratSelectionne.getDateDebut().equals(newDateDebut) &&
                            contratSelectionne.getDateFin().equals(newDateFin);

                    boolean hasSignature = contratSelectionne.getSignaturePath() != null;

                    if (isSameData && hasSignature && signatureIsEmpty()) {
                        showAlert("Aucune modification détectée. Veuillez modifier au moins un champ ou signer à nouveau.");
                        return;
                    }

                    // Mettre à jour les valeurs du contrat
                    contratSelectionne.setIdCentre(newCentreId);
                    contratSelectionne.setIdEmploye(newEmployeId);
                    contratSelectionne.setDateDebut(newDateDebut);
                    contratSelectionne.setDateFin(newDateFin);

                    // Vérifier si une nouvelle signature a été ajoutée
                    if (!signatureIsEmpty()) {
                        String path = enregistrerSignature(contratSelectionne.getId());
                        if (path != null) {
                            contratSelectionne.setSignaturePath(path);
                            contratRepository.updateSignaturePath(contratSelectionne.getId(), path);
                            System.out.println("✅ Nouvelle signature enregistrée à : " + path);
                        } else {
                            showAlert("Erreur lors de l'enregistrement de la signature !");
                            return;
                        }
                    }

                    // Mise à jour du contrat en base de données
                    contratRepository.modifier(contratSelectionne);
                    System.out.println("✅ Modification terminée !");

                    // Recharger la liste et la signature après modification
                    loadData();
                    afficherSignature(contratSelectionne.getId());

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
                    // Récupérer le chemin du fichier de signature
                    String signaturePath = contratSelectionne.getSignaturePath();

                    // Supprimer le contrat de la base de données
                    contratRepository.supprimer(contratSelectionne.getId());

                    // Supprimer le fichier de signature s'il existe
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

                    // Supprimer l'élément de la ListView
                    afficher.getItems().remove(contratSelectionne);

                    // Effacer le canvas après suppression
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
        private static final String SIGNATURE_FOLDER = "signatures"; // Dossier où enregistrer les signatures

        private String enregistrerSignature(int contratId) {
            try {
                File signatureFile = new File("signatures/contrat_" + contratId + ".png");
                if (!signatureFile.getParentFile().exists()) {
                    signatureFile.getParentFile().mkdirs(); // Créer le dossier s'il n'existe pas
                }

                WritableImage image = signature.snapshot(null, null);
                BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
                ImageIO.write(bufferedImage, "png", signatureFile);

                return signatureFile.getAbsolutePath(); // Retourne le chemin complet
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
                        return false; // Il y a bien un dessin
                    }
                }
            }
            return true; // Le canvas est vide
        }


       private void afficherSignature(int contratId) {
           // Construire le chemin correct
           File file = new File(SIGNATURE_FOLDER + "/contrat_" + contratId + ".png");

           if (file.exists()) {
               try {
                   // Charger l'image
                   Image image = new Image(file.toURI().toString());

                   // Récupérer le contexte graphique du Canvas
                   GraphicsContext gc = signature.getGraphicsContext2D();

                   // Effacer complètement le Canvas avant d'afficher la signature
                   gc.setFill(Color.WHITE);
                   gc.fillRect(0, 0, signature.getWidth(), signature.getHeight());

                   // Dessiner l'image sur le Canvas
                   gc.drawImage(image, 0, 0, signature.getWidth(), signature.getHeight());

                   signatureAffichee = true; // Marquer que la signature est bien affichée

                   System.out.println("✅ Signature chargée depuis : " + file.getAbsolutePath());
               } catch (Exception e) {
                   System.err.println("❌ Erreur lors du chargement de la signature !");
                   e.printStackTrace();
               }
           } else {
               System.out.println("⚠️ Aucune signature trouvée pour le contrat ."+contratId);
               signatureAffichee = false;

               // Effacer le canvas si aucune signature n'est trouvée
               GraphicsContext gc = signature.getGraphicsContext2D();
               gc.setFill(Color.WHITE);
               gc.fillRect(0, 0, signature.getWidth(), signature.getHeight());
           }
       }










    }

