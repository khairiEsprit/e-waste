package com.example.ewaste.Controllers;


import com.example.ewaste.Entities.User;
import com.example.ewaste.Entities.UserRole;
import com.example.ewaste.Entities.UserSession;
import com.example.ewaste.Main;
import com.example.ewaste.Repository.AuthRepository;
import com.example.ewaste.Repository.FaceRecognitionRepository;
import com.example.ewaste.Repository.GoogleAuthRepository;
import com.example.ewaste.Repository.UserRepository;
import com.example.ewaste.Utils.FaceDetector;
import com.example.ewaste.Utils.Modals;
import com.example.ewaste.Utils.OAuthCallbackServer;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.util.Duration;


import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_videoio.VideoCapture;


import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.ByteBuffer;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.Map;

import static com.example.ewaste.Utils.RoleNavigation.navigateUser;
import static com.example.ewaste.Utils.Validator.isValidEmail;

import static org.bytedeco.opencv.global.opencv_imgcodecs.*;
public class SignupController {

    public Button google_sign_up_button;
    public ProgressBar passwordStrengthBar;
    public Label passwordStrengthLabel;
    @FXML
    private TextField fullNameField;
    @FXML
    private PasswordField passwordField;

  
    @FXML
    private TextField emailField;


    @FXML
    private DatePicker birthDateField;
    // Error labels
    @FXML private Label emailErrorLabel;
    @FXML private Label birthDateErrorLabel;
    @FXML private Label roleErrorLabel;
    @FXML private Label passwordErrorLabel;
    @FXML private Label fullNameErrorLabel;
    @FXML
    private FontAwesomeIconView eyeIcon;
    @FXML
    private ComboBox<UserRole> roleComboBox;

    @FXML
    private HBox passwordHBox;


    @FXML
    private Button sign_up_button;



    private TextField textField; // To store the TextField when showing password
    private boolean isPasswordVisible = false;
    @FXML
    private void initialize() {



        roleComboBox.getItems().setAll(UserRole.values());
        roleComboBox.getSelectionModel().select(UserRole.ADMIN);


        textField = new TextField();
        textField.setStyle("-fx-prompt-text-fill: black;");
        textField.setPromptText("Password");
        textField.getStyleClass().add("tf_box");
        textField.setPrefHeight(40.0);
        HBox.setHgrow(textField, Priority.ALWAYS);

        passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
            updatePasswordStrength(newValue);
        });

        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            updatePasswordStrength(newValue);
            if (!isPasswordVisible) {
                passwordField.setText(newValue);
            }
        });
    }






    @FXML
    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            // Switch to PasswordField (hide password)
            passwordField.setText(textField.getText());
            passwordHBox.getChildren().set(0, passwordField);
            eyeIcon.setGlyphName("EYE");
            isPasswordVisible = false;
        } else {
            // Switch to TextField (show password)
            textField.setText(passwordField.getText());
            passwordHBox.getChildren().set(0, textField);
            eyeIcon.setGlyphName("EYE_SLASH");
            isPasswordVisible = true;
        }
    }


    private void updatePasswordStrength(String password) {
        double strength = calculatePasswordStrength(password);
        passwordStrengthBar.setProgress(strength);
        FadeTransition ft = new FadeTransition(Duration.millis(300), passwordStrengthBar);
        ft.setFromValue(0.5);
        ft.setToValue(1.0);
        ft.play();
        // Update label and color based on strength
        if (strength < 0.4) {
            passwordStrengthLabel.setText("Weak");
            passwordStrengthLabel.setTextFill(Color.RED);
            passwordStrengthBar.setStyle("-fx-accent: red;");
        } else if (strength < 0.7) {
            passwordStrengthLabel.setText("Medium");
            passwordStrengthLabel.setTextFill(Color.ORANGE);
            passwordStrengthBar.setStyle("-fx-accent: orange;");
        } else {
            passwordStrengthLabel.setText("Strong");
            passwordStrengthLabel.setTextFill(Color.GREEN);
            passwordStrengthBar.setStyle("-fx-accent: green;");
        }
    }

    private double calculatePasswordStrength(String password) {
        if (password == null || password.isEmpty()) return 0.0;

        double score = 0.0;
        int length = password.length();

        // Length check
        if (length >= 8) score += 0.3;
        if (length >= 12) score += 0.2;

        // Character type checks
        if (password.matches(".*[A-Z].*")) score += 0.2; // Uppercase
        if (password.matches(".*[0-9].*")) score += 0.2; // Numbers
        if (password.matches(".*[!@#$%^&*].*")) score += 0.1; // Special characters

        // Cap the score at 1.0
        return Math.min(score, 1.0);
    }



    private final GoogleAuthRepository googleAuthRepository = new GoogleAuthRepository();

    AuthRepository auth = new AuthRepository();
    UserRepository u = new UserRepository();


    private boolean isInputValid() {
        boolean valid = true;
        StringBuilder errorMessage = new StringBuilder();

        emailErrorLabel.setText("");
        birthDateErrorLabel.setText("");
        roleErrorLabel.setText("");
        passwordErrorLabel.setText("");
        fullNameErrorLabel.setText("");

        String email = emailField.getText();
        if (!isValidEmail(email)) {
            errorMessage.append("Invalid Email: Please enter a valid email address.\n");
            emailErrorLabel.setText("Invalid email address.");
            valid = false;
        }

        String nom = fullNameField.getText();
        if (nom == null || nom.isEmpty()) {
            errorMessage.append("Name cannot be empty!\n");
            fullNameErrorLabel.setText("Name cannot be empty!");
            valid = false;
        }

        if (passwordField == null || passwordField.getText().isEmpty()){
            errorMessage.append("Password cannot be empty!\n");
            passwordErrorLabel.setText("Password cannot be empty!");
            valid = false;
        }

        LocalDate selectedDate = birthDateField.getValue();
        if (selectedDate == null) {
            errorMessage.append("Please select a birth date!\n");
            birthDateErrorLabel.setText("Birth date is required!");
            valid = false;
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
            try {
                String formattedDate = selectedDate.format(formatter);
                LocalDate parsedDate = LocalDate.parse(formattedDate, formatter);
                if (!parsedDate.equals(selectedDate)) {
                    throw new DateTimeParseException("Invalid date format", formattedDate, 0);
                }
            } catch (DateTimeParseException e) {
                errorMessage.append("Date must be in the format YYYY/MM/DD!\n");
                birthDateErrorLabel.setText("Date must be in the format YYYY/MM/DD!");
                valid = false;
            }
        }

        if (roleComboBox.getSelectionModel().getSelectedItem() == null) {
            errorMessage.append("Please select a role!\n");
            roleErrorLabel.setText("Role selection is required!");
            valid = false;
        }

        if (!valid) {
            Modals.displayError(errorMessage.toString(), "Invalid input");
        }
        return valid;
    }




    @FXML
        public void  sign_up_button(ActionEvent event) throws SQLException, ParseException, IOException {
            System.out.println("the inputtt"+fullNameField.getText());
            System.out.println("the inputtt"+emailField.getText());
            System.out.println("the inputtt"+birthDateField.getValue());
            System.out.println("the inputtt"+roleComboBox.getSelectionModel().getSelectedItem());


            if(event.getSource() == sign_up_button) {
                if (isInputValid()) {
                    String email = emailField.getText();
                    if (auth.emailExists(email)) {
                        Modals.displayError("Invalid Email", "un compte associcé a cet email deja crée ");
                        return;
                    }

                    LocalDate selectedDate = birthDateField.getValue();
                    if (selectedDate == null) {
                        Modals.displayError("Invalid Date", "Veuillez sélectionner une date de naissance.");
                        return;
                    }

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    String formattedDate = selectedDate.format(formatter);
                    Date date = java.sql.Date.valueOf(formattedDate);

                    User u1 = new User(fullNameField.getText(), emailField.getText(), passwordField.getText(), date, roleComboBox.getSelectionModel().getSelectedItem());
                    u.addEntity(u1);
                    if(u.getUserByEmail(emailField.getText()) != null){
                        Modals.displaySuccess("Account Created", "Your account has been created successfully");

                    }
                    System.out.println(u1.toString());

                    UserSession us = UserSession.initializeUserSession(u1);

                    System.out.println("the session user role is " + us.getRole());



                    Stage window = (Stage) sign_up_button.getScene().getWindow();
                    navigateUser(window, us.getRole());
                }

            }
    }

    @FXML
    private void handleGoogleButtonAction() {
        new Thread(() -> {
            try {
                // Start local HTTP server to listen for OAuth callback
                OAuthCallbackServer server = new OAuthCallbackServer();

                // Open system browser to Google auth URL
                String authUrl = googleAuthRepository.buildAuthUrl();
                Desktop.getDesktop().browse(new URI(authUrl));

                // Wait for the authorization code (blocking call)
                while (server.getAuthCode() == null) {
                    Thread.sleep(500); // Wait until the code is received
                }

                String authorizationCode = server.getAuthCode();
                System.out.println("Authorization Code: " + authorizationCode);

                // Exchange authorization code for access token
                String accessToken = googleAuthRepository.getAccessToken(authorizationCode);
                System.out.println("Access Token: " + accessToken);

                // Fetch user info using the access token
                Map<String, Object> userInfo = googleAuthRepository.getUserInfo(accessToken);
                String email = userInfo.get("email").toString();

                // Check if the email already exists in the system
                if (auth.emailExists(email)) {
                    // Display error modal on the JavaFX Application Thread
                    Platform.runLater(() -> Modals.displayError(
                            "Invalid Email",
                            "un compte associé à cet email est déjà créé"
                    ));
                } else {
                    // Create the user with the fetched information
                    googleAuthRepository.createUser(
                            userInfo.get("name").toString(),
                            userInfo.get("given_name").toString(),
                            userInfo.get("family_name").toString(),
                            userInfo.get("picture").toString(),
                            email,
                            String.valueOf(userInfo.get("email_verified")),
                            "CITOYEN"
                    );

                    // Load the user account interface on the JavaFX Application Thread
                    Platform.runLater(() -> loadUserAccount());
                }

            } catch (Exception e) {
                e.printStackTrace();
                // Optionally, display an error modal for unexpected exceptions
                Platform.runLater(() -> Modals.displayError(
                        "Authentication Error",
                        "An error occurred during Google sign-up. Please try again."
                ));
            }
        }).start();
    }


    private void loadUserAccount() {
        try {
            // Load dashboard FXML file
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/UserAccount.fxml"));
            Parent root = loader.load();

            // Get current stage from any UI component (e.g., webView)
            Stage stage = (Stage) google_sign_up_button.getScene().getWindow();

            // Replace the current scene with the dashboard scene
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load dashboard interface.");
        }
    }







}
