package com.example.ewaste.Controllers;

import com.example.ewaste.Entities.Avis;
import com.example.ewaste.Entities.TextAnalysisResult;
import com.example.ewaste.Repository.AvisRepository;
import com.example.ewaste.Utils.OpenAiApiAvis;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class AvisController {

    @FXML
    private TextField nameField;

    @FXML
    private TextArea descriptionField;

    @FXML
    private ToggleButton star1, star2, star3, star4, star5;

    @FXML
    private Button submitButton;

    @FXML
    private Label totalReviewsLabel;

    @FXML
    private Label averageRatingLabel;

    @FXML
    private Label warningLabel;

    @FXML
    private Label star1CountLabel, star2CountLabel, star3CountLabel, star4CountLabel, star5CountLabel;

    @FXML
    private ProgressBar progressBar1, progressBar2, progressBar3, progressBar4, progressBar5;

    private final AvisRepository avisRepository;
    private final OpenAiApiAvis profanityDetectionService;

    private static final Set<String> BAD_WORDS = new HashSet<>();

    static {
        BAD_WORDS.add("idiot");
        BAD_WORDS.add("imbécile");
        BAD_WORDS.add("abruti");
        BAD_WORDS.add("crétin");
        BAD_WORDS.add("débile");
        BAD_WORDS.add("nul");
        BAD_WORDS.add("stupide");
        BAD_WORDS.add("merde");
        BAD_WORDS.add("putain");
        BAD_WORDS.add("bordel");
        BAD_WORDS.add("enfoiré");
        BAD_WORDS.add("con");
        BAD_WORDS.add("salopard");
        BAD_WORDS.add("raciste");
        BAD_WORDS.add("sexiste");
        BAD_WORDS.add("homophobe");
        BAD_WORDS.add("misogyne");
        BAD_WORDS.add("bouffon");
        BAD_WORDS.add("minable");
        BAD_WORDS.add("looser");
        BAD_WORDS.add("incapable");
        BAD_WORDS.add("incompétent");
    }

    public AvisController() {
        Connection conn = DataBaseConn.getInstance().getConnection();
        this.avisRepository = new AvisRepository(conn);
        this.profanityDetectionService = new OpenAiApiAvis();
    }

    @FXML
    public void initialize() {
        updateDynamicFields();
    }

    private void updateDynamicFields() {
        try {
            int totalReviews = avisRepository.getTotalReviews();
            totalReviewsLabel.setText("+" + totalReviews + " reviews");

            double averageRating = avisRepository.getAverageRating();
            averageRatingLabel.setText(String.format("%.1f", averageRating));

            int star1Count = avisRepository.getReviewCountByRating(1);
            int star2Count = avisRepository.getReviewCountByRating(2);
            int star3Count = avisRepository.getReviewCountByRating(3);
            int star4Count = avisRepository.getReviewCountByRating(4);
            int star5Count = avisRepository.getReviewCountByRating(5);

            star1CountLabel.setText(String.valueOf(star1Count));
            star2CountLabel.setText(String.valueOf(star2Count));
            star3CountLabel.setText(String.valueOf(star3Count));
            star4CountLabel.setText(String.valueOf(star4Count));
            star5CountLabel.setText(String.valueOf(star5Count));

            // Mettre à jour les ProgressBar
            if (totalReviews > 0) {
                progressBar1.setProgress((double) star1Count / totalReviews);
                progressBar2.setProgress((double) star2Count / totalReviews);
                progressBar3.setProgress((double) star3Count / totalReviews);
                progressBar4.setProgress((double) star4Count / totalReviews);
                progressBar5.setProgress((double) star5Count / totalReviews);
            } else {
                progressBar1.setProgress(0);
                progressBar2.setProgress(0);
                progressBar3.setProgress(0);
                progressBar4.setProgress(0);
                progressBar5.setProgress(0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger les données des avis.");
        }
    }

    @FXML
    public void handleSubmit() {
        String description = descriptionField.getText().toLowerCase().trim();
        String name = nameField.getText().trim();

        if (name.isEmpty() || description.isEmpty()) {
            warningLabel.setText("Le nom et la description sont obligatoires.");
            warningLabel.setVisible(true);
            return;
        }

        if (containsBadWords(description)) {
            warningLabel.setText("Votre avis contient des mots interdits. Veuillez le modifier.");
            warningLabel.setVisible(true);
            return;
        }

        TextAnalysisResult result = profanityDetectionService.detectBadWords(description);
        if (result.isContainsBadWords()) {
            warningLabel.setText("Votre avis contient des termes inappropriés selon l'analyse IA.");
            warningLabel.setVisible(true);
            System.out.println("Catégories détectées : " + result.getCategories());
            return;
        }

        warningLabel.setVisible(false);

        Avis avis = new Avis(0, name, description, getRatingFromStars());

        try {
            if (avisRepository.exists(avis)) {
                warningLabel.setText("Un avis similaire existe déjà.");
                warningLabel.setVisible(true);
                return;
            }

            boolean success = avisRepository.create(avis);

            if (success) {
                showAlert("Succès", "Votre avis a été soumis avec succès.");
                updateDynamicFields();
                redirectToListAvis();
            } else {
                warningLabel.setText("Erreur lors de la soumission de l'avis.");
                warningLabel.setVisible(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            warningLabel.setText("Erreur base de données : " + e.getMessage());
            warningLabel.setVisible(true);
        }
    }

    private boolean containsBadWords(String text) {
        for (String word : BAD_WORDS) {
            String regex = "\\b" + Pattern.quote(word) + "\\b";
            if (Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(text).find()) {
                return true;
            }
        }
        return false;
    }

    private int getRatingFromStars() {
        if (star5.isSelected()) return 5;
        if (star4.isSelected()) return 4;
        if (star3.isSelected()) return 3;
        if (star2.isSelected()) return 2;
        if (star1.isSelected()) return 1;
        return 0;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void redirectToListAvis() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com.example.ewaste/views/ListAvis-view.fxml"));
            Parent root = loader.load();
            Scene currentScene = submitButton.getScene();
            currentScene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger la page ListAvis.");
        }
    }
}