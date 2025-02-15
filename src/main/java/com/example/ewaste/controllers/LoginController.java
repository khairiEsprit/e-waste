package com.example.ewaste.controllers;


import com.example.ewaste.entities.User;
import com.example.ewaste.entities.UserSession;
import com.example.ewaste.repository.AuthRepository;
import com.example.ewaste.repository.UserRepository;
import com.example.ewaste.utils.Modals;
import com.example.ewaste.utils.Navigate;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

import static com.example.ewaste.utils.RoleNavigation.navigateUser;


public class LoginController {

    public Button forgot_password;
    @FXML
    private TextField ck_emailField;

    @FXML
    private TextField ck_passwordField;
  @FXML
    private Button signIn_btn;







    AuthRepository ar = new AuthRepository();
    UserRepository ur = new UserRepository();


    public void OnloginBtn(ActionEvent actionEvent) throws IOException {
        System.out.println("test from signin");

        if(actionEvent.getSource() ==  signIn_btn){
            System.out.println("Login button clicked");

            if(ar.authenticateUser(ck_emailField.getText(), ck_passwordField.getText())){
                Modals.displaySuccess("Login Successful", "You have successfully logged in");
               User u1 = ur.getUserByEmail(ck_emailField.getText());
              UserSession us = UserSession.initializeUserSession(u1);

                System.out.println("the session user role is " + us.getRole());
                Stage window = (Stage) signIn_btn.getScene().getWindow();
                navigateUser(window,us.getRole() );
            }
            else {
                Modals.displayError("Login Failed", "Invalid email or password");
            }






        }
    }

    public void onForgotPasswordClick(MouseEvent mouseEvent) {
        System.out.println("Forgot password clicked");
        Stage window = (Stage) signIn_btn.getScene().getWindow();
        Navigate.navigate(forgot_password,"views/ForgotPassword.fxml",window);
    }
}
