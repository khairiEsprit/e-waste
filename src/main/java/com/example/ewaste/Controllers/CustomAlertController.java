package com.example.ewaste.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class CustomAlertController {
    @FXML
    private Label messageLabel;

    @FXML
    private Label detailsLabel;

    @FXML
    private Button okButton;

    @FXML
    private void initialize() {
        okButton.setOnAction(event -> {
            Stage stage = (Stage) okButton.getScene().getWindow();
            stage.close();
        });
    }

    public void setMessage(String message) {
        messageLabel.setText(message);
    }

    public void setDetails(String details) {
        detailsLabel.setText(details);
    }
}