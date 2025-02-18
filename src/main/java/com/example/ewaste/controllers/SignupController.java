package com.example.ewaste.controllers;


import com.example.ewaste.entities.User;
import com.example.ewaste.entities.UserRole;
import com.example.ewaste.entities.UserSession;
import com.example.ewaste.repository.AuthRepository;
import com.example.ewaste.repository.UserRepository;
import com.example.ewaste.utils.Modals;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

import static com.example.ewaste.utils.RoleNavigation.navigateUser;
import static com.example.ewaste.utils.Validator.isValidEmail;

public class SignupController {

    @FXML
    private TextField fullNameField;

    @FXML
    private TextField emailField;
    @FXML
    private TextField passwordField;

    @FXML
    private DatePicker birthDateField;
    // Error labels
    @FXML private Label emailErrorLabel;
    @FXML private Label birthDateErrorLabel;
    @FXML private Label roleErrorLabel;
    @FXML private Label passwordErrorLabel;
    @FXML private Label fullNameErrorLabel;

    @FXML
    private ComboBox<UserRole> roleComboBox;

    @FXML
    private Button sign_up_button;
    @FXML
    private void initialize() {

        roleComboBox.getItems().setAll(UserRole.values());
        roleComboBox.getSelectionModel().select(UserRole.ADMIN);
    }




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


                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    String formattedDate = selectedDate.format(formatter);
                    Date date = java.sql.Date.valueOf(formattedDate);

                    User u1 = new User(fullNameField.getText(), emailField.getText(), passwordField.getText(), date, roleComboBox.getSelectionModel().getSelectedItem());
                    u.addEntity(u1);
                    if(u.getUserByEmail(emailField.getText()) != null){
                        Modals.displaySuccess("Account Created", "Your account has been created successfully");

                    }
                    System.out.println(u1);

                    UserSession us = UserSession.initializeUserSession(u1);

                    System.out.println("the session user role is " + us.getRole());



                    Stage window = (Stage) sign_up_button.getScene().getWindow();
                    navigateUser(window, us.getRole());
                }

            }
    }







}
