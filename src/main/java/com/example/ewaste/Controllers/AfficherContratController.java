    package com.example.ewaste.Controllers;

    import com.example.ewaste.Entities.Centre;
    import com.example.ewaste.Entities.Contrat;
    import com.example.ewaste.Repository.ContratRepository;
    import com.example.ewaste.Repository.MailRepository;
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
    import java.util.Arrays;
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
           /* try {
                // Appel de la méthode checkEndingContracts pour vérifier les contrats qui arrivent à expiration
                checkEndingContracts();
            } catch (SQLException e) {
                e.printStackTrace();
                System.err.println("Erreur lors de la vérification des contrats : " + e.getMessage());
            }*/


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
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com.example.ewaste/views/Afficher_Centre.fxml"));
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

                // Créer le document et le fichier
                Document document = new Document();
                File file = new File(filePath);
                file.getParentFile().mkdirs(); // Créer le dossier s'il n'existe pas

                PdfWriter.getInstance(document, new FileOutputStream(file));
                document.open();

                // Ajouter les détails du contrat
                Font titleFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
                Font normalFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);
                Font boldFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);

                document.add(new Paragraph("Contrat de travail à durée déterminée", titleFont));
                document.add(new Paragraph("\n"));

                // Section Employeur (statique)
                document.add(new Paragraph("ENTRE :", boldFont));
                document.add(new Paragraph("E-WASTE, société à responsabilité limitée au capital de 10 000 euros,", normalFont));
                document.add(new Paragraph("inscrite au R.C.S. de Tunis sous le numéro 123 456 789,", normalFont)); // R.C.S. mis à jour
                document.add(new Paragraph("dont le siège social est situé à ESPRIT Ghazela, Tunis,", normalFont)); // Adresse mise à jour pour Tunis
                document.add(new Paragraph("(ci-après désigné l’« Employeur »)", normalFont));
                document.add(new Paragraph("\n"));


                // Section Salarié (dynamique)
                document.add(new Paragraph("D’UNE PART,", boldFont));
                document.add(new Paragraph("\n"));
                document.add(new Paragraph("ET :", boldFont));

                String employeNom = contratRepository.getEmployeNameById(contrat.getIdEmploye());
                String employePrenom = contratRepository.getEmployePrenameById(contrat.getIdEmploye());
                String employMail = contratRepository.getEmployeEmailById(contrat.getIdEmploye());
                String employTelephone = contratRepository.getEmployTelephoneById(contrat.getIdEmploye());

                document.add(new Paragraph("L'employé : " + employeNom + " " + employePrenom, normalFont));
                document.add(new Paragraph("Avec Mail " +employMail, normalFont));
                document.add(new Paragraph("Téléphone " +employTelephone, normalFont));

                document.add(new Paragraph("(ci-après désigné le « Salarié »),", normalFont));
                document.add(new Paragraph("\n"));

                // Section Parties (statique)
                document.add(new Paragraph("D’AUTRE PART,", boldFont));
                document.add(new Paragraph("(ci-après collectivement désignés les « Parties »),", normalFont));
                document.add(new Paragraph("\n"));

                // Article 1 : Engagement et durée du contrat (dynamique)
                document.add(new Paragraph("IL A ETE CONVENU CE QUI SUIT :", boldFont));
                document.add(new Paragraph("\n"));
                document.add(new Paragraph("Article 1 : ENGAGEMENT ET DUREE DU CONTRAT", boldFont));
                document.add(new Paragraph("Le présent contrat est conclu dans le cadre d'un contrat à durée déterminée", normalFont));
                String duree = Arrays.toString(contratRepository.getContratDatesById(contrat.getIdEmploye()));
                document.add(new Paragraph(duree, normalFont));
                document.add(new Paragraph("L’entreprise exerçant l’activité suivante :", normalFont));
                document.add(new Paragraph("\n"));

                // Article 2 : Rémunération
                document.add(new Paragraph("Article 2 : REMUNERATION", boldFont));
                document.add(new Paragraph("En contrepartie de son travail, le Salarié percevra une rémunération brute mensuelle de :", normalFont));
                document.add(new Paragraph("\n"));

                // Article 3 : Lieu et horaires de travail
                document.add(new Paragraph("Article 3 : LIEU ET HORAIRES DE TRAVAIL", boldFont));
                document.add(new Paragraph("Le Salarié exercera ses fonctions à l’adresse suivante :", normalFont));
                document.add(new Paragraph("\n"));

                // Signature
                document.add(new Paragraph("Fait à Tunis, le " + LocalDate.now(), normalFont));
                document.add(new Paragraph("\n"));
                document.add(new Paragraph("Signature de l’Employeur :", boldFont));
                document.add(new Paragraph("\n\n\n"));
                document.add(new Paragraph("Signature du Salarié :", boldFont));
                document.add(new Paragraph("\n\n\n"));

                // Ajouter la signature si elle existe (dynamique)
                String signaturePath = contrat.getSignaturePath();
                if (signaturePath != null && !signaturePath.isEmpty()) {
                    File signatureFile = new File(signaturePath);

                    if (signatureFile.exists() && signatureFile.isFile()) {
                        try {
                            // Vérifier le format de l'image
                            String extension = signaturePath.substring(signaturePath.lastIndexOf(".") + 1);
                            if (!extension.equalsIgnoreCase("png") && !extension.equalsIgnoreCase("jpg") && !extension.equalsIgnoreCase("jpeg")) {
                                System.err.println("⚠️ Format de signature non supporté : " + extension);
                            } else {
                                // Ajouter l'image de la signature
                                com.itextpdf.text.Image signatureImage = com.itextpdf.text.Image.getInstance(signatureFile.getAbsolutePath());

                                // Vérifier si l’image est bien lisible
                                if (signatureImage == null) {
                                    System.err.println("⚠️ Impossible de charger l’image !");
                                } else {
                                    signatureImage.scaleToFit(150, 50); // Redimensionner l’image
                                    signatureImage.setAlignment(Element.ALIGN_CENTER); // Centrer l’image
                                    document.add(new Paragraph("\n\nSignature :", normalFont));
                                    document.add(signatureImage);
                                }
                            }
                        } catch (Exception e) {
                            System.err.println("⚠️ Erreur lors de l'ajout de la signature !");
                            e.printStackTrace();
                        }
                    } else {
                        System.err.println("⚠️ Fichier de signature introuvable : " + signatureFile.getAbsolutePath());
                    }
                } else {
                    System.err.println("⚠️ Aucun chemin de signature trouvé.");
                }

                document.close();
                System.out.println("✅ PDF généré : " + filePath);

            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Erreur lors de la génération du PDF.");
            }
        }



        public void checkEndingContracts() throws SQLException {
            // Date actuelle
            LocalDate today = LocalDate.now();
            LocalDate twoDaysLater = today.plusDays(2);

            // Récupérer tous les contrats
            List<Contrat> contrats = contratRepository.afficher();

            // Parcourir chaque contrat et vérifier la date de fin
            for (Contrat contrat : contrats) {
                LocalDate dateFin = contrat.getDateFin();

                // Vérifier si la date de fin est dans les 2 prochains jours
                if (!dateFin.isBefore(today) && !dateFin.isAfter(twoDaysLater)) {
                    // Récupérer l'email de l'employé
                    String employeEmail = contratRepository.getEmployeEmailById(contrat.getIdEmploye());

                    if (employeEmail != null) {
                        // Envoyer un email
                        sendExpirationEmail(employeEmail, contrat);

                    }
                }
            }
        }

        private void sendExpirationEmail(String email, Contrat contrat) throws SQLException {
            String subject = "Votre contrat arrive bientôt à sa fin";
            String body = "Bonjour,"+ contratRepository.getEmployeNameById(contrat.getIdEmploye())+" "+contratRepository.getEmployePrenameById(contrat.getIdEmploye()) +"Votre contrat avec le centre " + contratRepository.getCentreNameById(contrat.getIdCentre())
                    + " se termine le " + contrat.getDateFin() + ".\n\nVeuillez prendre les mesures nécessaires.";

            // Appel du service d'envoi d'email avec les paramètres nécessaires
            MailRepository.sendEmailWithoutAttachment(email, subject, body);
        }






    }

