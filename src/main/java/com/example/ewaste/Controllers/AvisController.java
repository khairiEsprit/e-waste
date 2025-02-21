package com.example.ewaste.Controllers;

import com.example.ewaste.Entities.Avis;
import com.example.ewaste.Repository.AvisRepository;
import com.example.ewaste.Utils.DataBase;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.Connection;

public class AvisController {

    // FXML elements
    @FXML
    private TextField nameField; // For name input

    @FXML
    private TextArea descriptionField; // For description input

    @FXML
    private ToggleButton star1, star2, star3, star4, star5; // For rating input

    @FXML
    private Button submitButton; // For submit button

    private final AvisRepository avisRepository;

    public AvisController() {
        // Initialize repository with the database connection
        Connection conn = DataBase.getInstance().getConnection();
        this.avisRepository = new AvisRepository(conn);
    }

    // Handle the form submission
    @FXML
    public void handleSubmit() {
        // Get user input from the form
        String name = nameField.getText();
        String description = descriptionField.getText();

        // Get rating from the star buttons (1 to 5)
        int rating = getRatingFromStars();

        // Validate the form data
        if (name.isEmpty() || description.isEmpty() || rating == 0) {
            showAlert("Error", "Please fill in all fields and select a rating.");
            return;
        }

        // Create an Avis object with the collected data
        Avis avis = new Avis(0, name, description, rating);

        // Insert the Avis into the database using the repository
        boolean success = avisRepository.create(avis);

        // Show success or error alert based on the result
        if (success) {
            showAlert("Success", "Your feedback has been successfully submitted.");
        } else {
            showAlert("Error", "There was an issue submitting your feedback. Please try again.");
        }
    }

    // Helper method to get the rating from the star buttons (1-5)
    private int getRatingFromStars() {
        if (star1.isSelected()) return 1;
        if (star2.isSelected()) return 2;
        if (star3.isSelected()) return 3;
        if (star4.isSelected()) return 4;
        if (star5.isSelected()) return 5;
        return 0; // No rating selected
    }

    // Helper method to display an alert message
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
