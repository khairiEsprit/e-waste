package com.example.ewaste.Controllers;

import com.example.ewaste.Entities.Historique_Poubelle;
import com.example.ewaste.Entities.poubelle;
import com.example.ewaste.Entities.type;
import com.example.ewaste.Repository.CapteurpRepository;
import com.example.ewaste.Repository.HistoriquePoubelleRepository;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.css.PseudoClass;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import java.util.function.UnaryOperator;

public class HistoriquePoubelleController {
    @FXML
    private ListView<poubelle> listPoubelles;
    @FXML
    private TextField poubelleIdField;

    @FXML
    private TextField quantiteDechetsField;

    @FXML
    private Label statusLabel;

    @FXML
    private TableView<Historique_Poubelle> historiqueTable;

    @FXML
    private TableColumn<Historique_Poubelle, Integer> idColumn;

    @FXML
    private TableColumn<Historique_Poubelle, Integer> idPoubelleColumn;

    @FXML
    private TableColumn<Historique_Poubelle, Date> dateEvenementColumn;

    @FXML
    private TableColumn<Historique_Poubelle, type> typeEvenementColumn;

    @FXML
    private TableColumn<Historique_Poubelle, String> descriptionColumn;

    @FXML
    private TableColumn<Historique_Poubelle, Float> quantiteDechetsColumn;

    @FXML
    private ComboBox<type> typeFilterComboBox;

    @FXML
    private ProgressIndicator loadingIndicator;
    @FXML
    private Button downloadButton;
    @FXML
    private ImageView qrCodeImageView;

    private BufferedImage qrCodeBufferedImage;
    private poubelle selectedPoubelle;

    private HistoriquePoubelleRepository historiquePoubelleRepository = new HistoriquePoubelleRepository();
    private CapteurpRepository capteurpRepository = new CapteurpRepository();
    private Timeline timeline;
    private int poubelleId; // Variable pour stocker l'ID de la poubelle

    // Variable pour stocker la poubelle sélectionnée

    // Méthode pour définir la poubelle sélectionnée
    public void setSelectedPoubelle(poubelle selectedPoubelle) {
        this.selectedPoubelle = selectedPoubelle;
        generateQRCode();
        loadHistorique();
    }

    // Méthode pour obtenir la poubelle sélectionnée
    public poubelle getSelectedPoubelle() {
        return selectedPoubelle;
    }
    @FXML
    public void initialize() {
         // Initialiser les colonnes du tableau
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idPoubelleColumn.setCellValueFactory(new PropertyValueFactory<>("id_poubelle"));
        dateEvenementColumn.setCellValueFactory(new PropertyValueFactory<>("date_evenement"));
        typeEvenementColumn.setCellValueFactory(new PropertyValueFactory<>("type_evenement"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        quantiteDechetsColumn.setCellValueFactory(new PropertyValueFactory<>("quantite_dechets"));

        // Configuration initiale
        loadingIndicator.setVisible(false);
        downloadButton.setDisable(true);
        qrCodeImageView.setVisible(false);
        // Vérification des injections FXML
        System.out.println("QR ImageView initialized: " + (qrCodeImageView != null));
        System.out.println("Loading indicator: " + (loadingIndicator != null));
        System.out.println("Download button: " + (downloadButton != null));



        // Style alternatif pour le ProgressIndicator
        loadingIndicator.setStyle("-fx-progress-color: #2196F3; -fx-scale-x: 1.5; -fx-scale-y: 1.5;");


        // Charger l'historique initial
        loadHistorique();

        // Initialiser le Timeline pour exécuter une tâche toutes les 30 secondes
        timeline = new Timeline(new KeyFrame(Duration.seconds(30), event -> {
            try {
                mettreAJourPoubelleAleatoire();
            } catch (SQLException e) {
                e.printStackTrace();
                Platform.runLater(() -> statusLabel.setText("Erreur lors de la mise à jour de la poubelle."));
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE); // Répéter indéfiniment
        timeline.play(); // Démarrer le Timeline
    }
    public void setPoubelleId(int poubelleId) {
        this.poubelleId = poubelleId; // Mettre à jour l'ID de la poubelle
        loadHistorique(); // Charger l'historique de la poubelle
    }

    private void mettreAJourPoubelleAleatoire() throws SQLException {
        // Choisir une poubelle aléatoirement
        int idPoubelle = capteurpRepository.choisirPoubelleAleatoire();

        // Simuler une quantité ajoutée (par exemple, entre 1 et 10 kg)
        Random random = new Random();
        float quantiteAjoutee = random.nextFloat() * 10;

        // Mettre à jour la quantité dans capteurp
        capteurpRepository.mettreAJourQuantiteDechets(idPoubelle, quantiteAjoutee);

        // Enregistrer l'événement dans l'historique
        Historique_Poubelle historique = new Historique_Poubelle(
                idPoubelle,
                new Date(),
                type.REMPLISSAGE,
                "Remplissage détecté",
                quantiteAjoutee
        );
        historiquePoubelleRepository.ajouter(historique);

        // Afficher un message dans l'interface utilisateur
        Platform.runLater(() -> {
            statusLabel.setText("Poubelle " + idPoubelle + " mise à jour : " + quantiteAjoutee + " kg ajoutés.");
            loadHistorique(); // Recharger l'historique
        });
    }

    private void loadHistorique() {
        try {
            List<Historique_Poubelle> historiqueList = historiquePoubelleRepository.recupererr();
            ObservableList<Historique_Poubelle> observableList = FXCollections.observableArrayList(historiqueList);
            historiqueTable.setItems(observableList);
        } catch (SQLException e) {
            e.printStackTrace();
            statusLabel.setText("Erreur lors du chargement de l'historique.");
        }
    }

    @FXML
    public void handleRemplissageEvent() {
        try {
            // Vérifier si les champs sont vides
            if (poubelleIdField.getText().isEmpty() || quantiteDechetsField.getText().isEmpty()) {
                statusLabel.setText("Erreur : Veuillez remplir tous les champs.");
                return;
            }

            int idPoubelle = Integer.parseInt(poubelleIdField.getText());
            float quantiteDechets = Float.parseFloat(quantiteDechetsField.getText());

            Historique_Poubelle historique = new Historique_Poubelle(
                    idPoubelle,
                    new Date(),
                    type.REMPLISSAGE,
                    "Remplissage détecté",
                    quantiteDechets
            );

            historiquePoubelleRepository.ajouter(historique);
            statusLabel.setText("Événement de remplissage enregistré avec succès.");
            loadHistorique(); // Recharger l'historique
        } catch (NumberFormatException e) {
            statusLabel.setText("Erreur : Veuillez entrer des valeurs numériques valides.");
        } catch (SQLException e) {
            statusLabel.setText("Erreur lors de l'enregistrement de l'événement.");
            e.printStackTrace();
        }
    }

    @FXML
    public void handleVidageEvent() {
        try {
            // Vérifier si les champs sont vides
            if (poubelleIdField.getText().isEmpty() || quantiteDechetsField.getText().isEmpty()) {
                statusLabel.setText("Erreur : Veuillez remplir tous les champs.");
                return;
            }

            int idPoubelle = Integer.parseInt(poubelleIdField.getText());
            float quantiteDechets = Float.parseFloat(quantiteDechetsField.getText());

            Historique_Poubelle historique = new Historique_Poubelle(
                    idPoubelle,
                    new Date(),
                    type.VIDAGE,
                    "Vidage détecté",
                    quantiteDechets
            );

            historiquePoubelleRepository.ajouter(historique);
            statusLabel.setText("Événement de vidage enregistré avec succès.");
            loadHistorique(); // Recharger l'historique
        } catch (NumberFormatException e) {
            statusLabel.setText("Erreur : Veuillez entrer des valeurs numériques valides.");
        } catch (SQLException e) {
            statusLabel.setText("Erreur lors de l'enregistrement de l'événement.");
            e.printStackTrace();
        }
    }

    @FXML
    public void handlePanneEvent() {
        try {
            // Vérifier si le champ est vide
            if (poubelleIdField.getText().isEmpty()) {
                statusLabel.setText("Erreur : Veuillez entrer l'ID de la poubelle.");
                return;
            }

            int idPoubelle = Integer.parseInt(poubelleIdField.getText());

            Historique_Poubelle historique = new Historique_Poubelle(
                    idPoubelle,
                    new Date(),
                    type.PANNE,
                    "Panne détectée",
                    0
            );

            historiquePoubelleRepository.ajouter(historique);
            statusLabel.setText("Événement de panne enregistré avec succès.");
            loadHistorique(); // Recharger l'historique
        } catch (NumberFormatException e) {
            statusLabel.setText("Erreur : Veuillez entrer un ID valide.");
        } catch (SQLException e) {
            statusLabel.setText("Erreur lors de l'enregistrement de l'événement.");
            e.printStackTrace();
        }
    }

    @FXML
    public void handleReparationEvent() {
        try {
            // Vérifier si le champ est vide
            if (poubelleIdField.getText().isEmpty()) {
                statusLabel.setText("Erreur : Veuillez entrer l'ID de la poubelle.");
                return;
            }

            int idPoubelle = Integer.parseInt(poubelleIdField.getText());

            Historique_Poubelle historique = new Historique_Poubelle(
                    idPoubelle,
                    new Date(),
                    type.REPARATION,
                    "Réparation effectuée",
                    0
            );

            historiquePoubelleRepository.ajouter(historique);
            statusLabel.setText("Événement de réparation enregistré avec succès.");
            loadHistorique(); // Recharger l'historique
        } catch (NumberFormatException e) {
            statusLabel.setText("Erreur : Veuillez entrer un ID valide.");
        } catch (SQLException e) {
            statusLabel.setText("Erreur lors de l'enregistrement de l'événement.");
            e.printStackTrace();
        }
    }

    @FXML
    public void handleExportCSV() {
        if (selectedPoubelle == null) {
            statusLabel.setText("Erreur : Aucune poubelle sélectionnée.");
            return;
        }

        try {
            // Récupérer l'historique de la poubelle sélectionnée
            int idPoubelle = selectedPoubelle.getId();
            List<Historique_Poubelle> historiqueList = historiquePoubelleRepository.recupererParPoubelle(idPoubelle);

            // Créer un fichier CSV
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Exporter en CSV");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers CSV", "*.csv"));
            File file = fileChooser.showSaveDialog(null);

            if (file != null) {
                try (PrintWriter writer = new PrintWriter(file)) {
                    writer.println("ID,ID Poubelle,Date Événement,Type Événement,Description,Quantité Déchets (kg)");
                    for (Historique_Poubelle h : historiqueList) {
                        writer.println(h.getId() + "," + h.getId_poubelle() + "," + h.getDate_evenement() + "," +
                                h.getType_evenement() + "," + h.getDescription() + "," + h.getQuantite_dechets());
                    }
                    statusLabel.setText("Export CSV réussi : " + file.getAbsolutePath());
                } catch (FileNotFoundException e) {
                    statusLabel.setText("Erreur : Fichier non trouvé ou accès refusé.");
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            statusLabel.setText("Erreur lors de l'export CSV.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSupprimer(ActionEvent event) {
        Historique_Poubelle selectedItem = historiqueTable.getSelectionModel().getSelectedItem();

        if (selectedItem != null) {
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Confirmation de suppression");
            confirmation.setHeaderText("Voulez-vous vraiment supprimer cet historique ?");
            confirmation.setContentText("Cette action est irréversible.");

            Optional<ButtonType> result = confirmation.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    historiquePoubelleRepository.supprimer(selectedItem.getId());
                    historiqueTable.getItems().remove(selectedItem);
                    statusLabel.setText("Événement supprimé avec succès.");
                } catch (SQLException e) {
                    statusLabel.setText("Erreur lors de la suppression de l'événement.");
                    e.printStackTrace();
                }
            }
        } else {
            Alert warning = new Alert(Alert.AlertType.WARNING);
            warning.setTitle("Aucune sélection");
            warning.setHeaderText(null);
            warning.setContentText("Veuillez sélectionner un élément à supprimer.");
            warning.show();
        }
    }
    @FXML
    public void handleAfficherHistorique() {
        try {
            int idPoubelle = Integer.parseInt(poubelleIdField.getText());

            // Vérifier si la poubelle existe
            if (!historiquePoubelleRepository.existePoubelle(idPoubelle)) {
                statusLabel.setText("Erreur : La poubelle avec l'ID " + idPoubelle + " n'existe pas.");
                return;
            }

            // Récupérer et afficher l'historique
            List<Historique_Poubelle> historiqueList = historiquePoubelleRepository.recupererParPoubelle(idPoubelle);
            ObservableList<Historique_Poubelle> observableList = FXCollections.observableArrayList(historiqueList);
            historiqueTable.setItems(observableList);
            statusLabel.setText("Historique chargé avec succès.");
        } catch (NumberFormatException e) {
            statusLabel.setText("Erreur : L'ID de la poubelle doit être un nombre.");
        } catch (SQLException e) {
            statusLabel.setText("Erreur lors du chargement de l'historique.");
            e.printStackTrace();
        }
    }
    @FXML
    private void handleGemini(ActionEvent event) {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ewaste/views/Gemini.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (IOException e) {
            afficherAlerte(" Gemini erreur",e.getMessage(),Alert.AlertType.ERROR);
        }
    }
    private void afficherAlerte(String titre, String message, Alert.AlertType type) {
        Alert alerte = new Alert(type);
        alerte.setTitle(titre);
        alerte.setHeaderText(null);
        alerte.setContentText(message);
        alerte.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alerte.getDialogPane().setMinWidth(Region.USE_PREF_SIZE);
        alerte.showAndWait();
    }
    // Modifiez la méthode generateQRCode


    private void generateQRCode() {
        if (selectedPoubelle == null) return;

        // Réinitialiser l'interface
        qrCodeImageView.setImage(null);
        qrCodeImageView.setVisible(true);
        loadingIndicator.setVisible(true);
        downloadButton.setDisable(true);

        Task<Image> task = new Task<>() {
            @Override
            protected Image call() throws Exception {
                String qrData = String.format(
                        "ID: %d\nCentre: %d\nAdresse: %s\nNiveau: %d%%\nÉtat: %s\nInstallation: %s\nHauteur: %dcm",
                        selectedPoubelle.getId(),
                        selectedPoubelle.getId_centre(),
                        selectedPoubelle.getAdresse(),
                        selectedPoubelle.getNiveau(),
                        selectedPoubelle.getEtat(),
                        selectedPoubelle.getDate_installation(),
                        selectedPoubelle.getHauteurTotale()
                );

                QRCodeWriter qrCodeWriter = new QRCodeWriter();
                BitMatrix bitMatrix = qrCodeWriter.encode(qrData, BarcodeFormat.QR_CODE, 200, 200);

                BufferedImage bufferedImage = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
                for (int x = 0; x < 200; x++) {
                    for (int y = 0; y < 200; y++) {
                        bufferedImage.setRGB(x, y, bitMatrix.get(x, y) ? 0x000000 : 0xFFFFFF);
                    }
                }
                return SwingFXUtils.toFXImage(bufferedImage, null);
            }
        };

        task.setOnSucceeded(e -> {
            qrCodeImageView.setImage(task.getValue());
            loadingIndicator.setVisible(false);
            downloadButton.setDisable(false);
        });

        task.setOnFailed(e -> {
            loadingIndicator.setVisible(false);
            downloadButton.setDisable(true);
            afficherAlerte("Erreur", "Échec de génération du QR Code", Alert.AlertType.ERROR);
        });

        new Thread(task).start();
    }

    @FXML
    private void handleDownload(ActionEvent event) {
        if (qrCodeImageView.getImage() == null) return;

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer le QR Code");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers PNG", "*.png"));

        File file = fileChooser.showSaveDialog(qrCodeImageView.getScene().getWindow());
        if (file != null) {
            try {
                BufferedImage bufferedImage = SwingFXUtils.fromFXImage(qrCodeImageView.getImage(), null);
                ImageIO.write(bufferedImage, "png", file);
                afficherAlerte("Succès", "QR Code enregistré avec succès!", Alert.AlertType.INFORMATION);
            } catch (IOException ex) {
                afficherAlerte("Erreur", "Échec de l'enregistrement: " + ex.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }
}