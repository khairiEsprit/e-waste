    package com.example.ewaste.controllers;

    import com.example.ewaste.entities.Centre;
    import com.example.ewaste.entities.Contrat;
    import com.example.ewaste.repository.ContratRepository;
    import com.example.ewaste.repository.MailRepository;
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
    import javafx.scene.image.Image;
    import javafx.scene.image.WritableImage;
    import javafx.scene.paint.Color;
    import javafx.stage.Stage;

    import javax.imageio.ImageIO;
    import java.awt.image.BufferedImage;
    import java.io.File;
    import java.io.FileOutputStream;
    import java.io.IOException;
    import java.sql.SQLException;
    import java.time.LocalDate;
    import java.util.List;





    public class AfficherContratController {

        @FXML
        private Button Ajou;

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
        void Ajou(ActionEvent event) {
            try {
                // Charger le fichier FXML de l'interface "ajoutercentre.fxml"
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ewaste/views/ajoutercentre.fxml"));
                Parent root = loader.load();

                // Créer une nouvelle scène
                Scene scene = new Scene(root);

                // Obtenir la fenêtre actuelle (stage) à partir de l'événement
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

                // Définir la nouvelle scène sur le stage
                stage.setScene(scene);
                stage.setTitle("Ajouter un Centre"); // Titre de la fenêtre
                stage.show(); // Afficher la nouvelle interface
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Erreur : Impossible de charger l'interface ajoutercentre.fxml.");
            }

        }

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
                // Validation des champs
                if (centreNom == null || employeNom == null || dateDebut == null || dateFin == null) {
                    showAlert("Erreur : Tous les champs doivent être remplis.");
                    return;
                }

                if (dateDebut.isAfter(dateFin)) {
                    showAlert("Erreur : La date de début doit être antérieure à la date de fin.");
                    return;
                }

                if (signatureIsEmpty()) {
                    showAlert("Erreur : Vous devez signer avant d'ajouter le contrat.");
                    return;
                }

                // Récupérer les IDs du centre et de l'employé
                Integer centreId = contratRepository.getCentreIdByName(centreNom);
                Integer employeId = contratRepository.getEmployeIdByName(employeNom);

                if (centreId == null || employeId == null) {
                    showAlert("Erreur : Centre ou employé introuvable.");
                    return;
                }

                // Vérifier si le contrat existe déjà
                if (contratRepository.existeContrat(centreId, employeId, dateDebut, dateFin)) {
                    showAlert("Erreur : Ce contrat existe déjà dans la base de données.");
                    return;
                }

                // Ajouter le contrat
                Contrat nouveauContrat = new Contrat(centreId, employeId, dateDebut, dateFin, null);
                contratRepository.ajouter(nouveauContrat);

                // Générer le PDF
                generatePDF(nouveauContrat);

                // Récupérer l'ID du dernier contrat ajouté
                int dernierId = contratRepository.getLastInsertedContratId();

                // Enregistrer la signature
                String signaturePath = enregistrerSignature(dernierId);
                if (signaturePath != null) {
                    contratRepository.updateSignaturePath(dernierId, signaturePath);
                } else {
                    showAlert("Erreur : Impossible d'enregistrer la signature.");
                    return;
                }

                // Envoyer l'e-mail avec le PDF en pièce jointe
                String recipientEmail = contratRepository.getEmployeEmailById(employeId);
                if (recipientEmail == null || recipientEmail.isEmpty()) {
                    showAlert("Erreur : L'e-mail de l'employé est introuvable.");
                    return;
                }

                String subject = "[Contrat] Détails de votre contrat";
                String message = "Bonjour " + employeNom + ",\n\n" +
                        "Veuillez trouver ci-joint les détails de votre contrat.\n\n" +
                        "Cordialement,\n" +
                        "L'équipe E-WASTE";

                String pdfPath = "contrats/contrat_" + dernierId + ".pdf"; // Chemin du PDF généré

                // Vérifier si le fichier PDF existe
                File pdfFile = new File(pdfPath);
                if (!pdfFile.exists()) {
                    showAlert("Erreur : Le fichier PDF du contrat est introuvable.");
                    return;
                }

                // Envoyer l'e-mail avec le PDF en pièce jointe
              // MailRepository.sendEmail(recipientEmail, subject, message, pdfPath);

                // Réinitialiser le formulaire
                loadData();
                idCentre.setValue(null);
                idEmploye.setValue(null);
                DateDebut.setValue(null);
                DateFin.setValue(null);

                // Effacer la signature
                GraphicsContext gc = signature.getGraphicsContext2D();
                gc.setFill(Color.WHITE);
                gc.fillRect(0, 0, signature.getWidth(), signature.getHeight());

            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Erreur : Une erreur s'est produite lors de l'ajout du contrat.");
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Erreur : Une erreur s'est produite lors de l'envoi de l'e-mail.");
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

                    // Supprimer le contrat de la base de données²
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

        private void generatePDF(Contrat contrat) {
            try {
                int lastId = contratRepository.getLastInsertedContratId(); // Récupérer le dernier ID
                if (lastId == -1) {
                    showAlert("Aucun contrat trouvé !");
                    return;
                }

                String filePath = "contrats/contrat_" + lastId + ".pdf"; // Utiliser le dernier ID

                Document document = new Document();
                File file = new File(filePath);
                file.getParentFile().mkdirs(); // Créer le dossier s'il n'existe pas

                PdfWriter.getInstance(document, new FileOutputStream(file));
                document.open();

                // Ajouter les détails du contrat
                Font titleFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
                Font normalFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);

                document.add(new Paragraph("Contrat de Travail", titleFont));
                document.add(new Paragraph("Centre : " + contratRepository.getCentreNameById(contrat.getIdCentre()), normalFont));
                document.add(new Paragraph("Employé : " + contratRepository.getEmployeNameById(contrat.getIdEmploye()), normalFont));
                document.add(new Paragraph("Date de début : " + contrat.getDateDebut(), normalFont));
                document.add(new Paragraph("Date de fin : " + contrat.getDateFin(), normalFont));

                // Ajouter la signature si elle existe
                String signaturePath = contrat.getSignaturePath();
                if (signaturePath != null) {
                    File signatureFile = new File(signaturePath);

                    if (signatureFile.exists()) { // Vérifier si l'image existe
                        try {
                            com.itextpdf.text.Image signatureImage = com.itextpdf.text.Image.getInstance(signatureFile.getAbsolutePath());
                            signatureImage.scaleToFit(150, 50); // Redimensionner l’image
                            signatureImage.setAlignment(Element.ALIGN_CENTER); // Centrer l’image

                            document.add(new Paragraph("\n\nSignature :", normalFont)); // Ajouter un label
                            document.add(signatureImage); // Ajouter l’image au PDF
                        } catch (Exception e) {
                            e.printStackTrace();
                            System.out.println("⚠️ Erreur lors de l'ajout de la signature au PDF !");
                        }
                    } else {
                        System.out.println("⚠️ Fichier de signature introuvable : " + signatureFile.getAbsolutePath());
                    }
                } else {
                    System.out.println("⚠️ Aucun chemin de signature trouvé pour le contrat.");
                }

                document.close();
                System.out.println("✅ PDF généré : " + filePath);

            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Erreur lors de la génération du PDF.");
            }
        }






    }

