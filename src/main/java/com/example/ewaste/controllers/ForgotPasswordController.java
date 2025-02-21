package com.example.ewaste.Controllers;

import com.example.ewaste.Repository.AuthRepository;
import com.example.ewaste.Repository.UserRepository;
import com.example.ewaste.Utils.Navigate;
import com.example.ewaste.Utils.SendMail;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.dialogs.MFXGenericDialog;
import javafx.animation.FadeTransition;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

public class ForgotPasswordController implements Initializable {
    public MFXButton SendCodeBtn;
    public TextField tf_Email;
    public Pane general_pane;
    public MFXGenericDialog Dialog_Password;
    public TextField tf_passwordUpdate;
    public MFXButton savePassword;
    public MFXGenericDialog Verif_Dialog;
    public TextField tf_codee;
    public MFXButton ValidateCodeBtn;



    AuthRepository au = new AuthRepository();
    UserRepository u = new UserRepository();

    int x  ;
    int userId ;
    public void sendcode(javafx.event.ActionEvent event) {

        general_pane.setVisible(false);



        userId = u.getUserIdByEmail(tf_Email.getText()) ;
        System.out.println(userId);
        if(userId != -1)
        {


            Random random = new Random();
            int randomNumber = random.nextInt(999999);
            x=randomNumber ;
            System.out.println(x);
            String message = "Your verification code is: " + randomNumber;
            String recipientEmail = tf_Email.getText();

            boolean emailSent = SendMail.send(recipientEmail, "Verification Code", message);
            if (emailSent) {
                Verif_Dialog.setVisible(true);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Email Sending Failed");
                alert.setHeaderText(null);
                alert.setContentText("Failed to send verification code. Please check your internet connection or email settings and try again.");
                alert.showAndWait();
                Navigate.navigate(SendCodeBtn,"/views/mainLoginSignUp.fxml", (Stage) SendCodeBtn.getScene().getWindow());

            }
        }
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("email not found");
            alert.showAndWait();
//            Navigate.navigate(SendCodeBtn,"/views/mainLoginSignUp.fxml", (Stage) SendCodeBtn.getScene().getWindow());

        }
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Dialog_Password.setVisible(false);
        Verif_Dialog.setVisible(false);
    }


    public void validateCode()

    {

        if(Integer.parseInt(tf_codee.getText())==x)
        {
//            Dialog_Password.setVisible(true);
            Verif_Dialog.setVisible(false);

            Dialog_Password.setOpacity(0);
            Dialog_Password.setVisible(true);

            // Créer une transition de fondu pour simuler l'effet de coup d'éponge
            FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.5), Dialog_Password);
            fadeIn.setToValue(1.0);
            fadeIn.play();

            BoxBlur boxBlur = new BoxBlur();
            boxBlur.setWidth(5);
            boxBlur.setHeight(5);
            boxBlur.setIterations(3);
            general_pane.setEffect(boxBlur);


        }
    }
    public void modifierpass() throws IOException {
        au.modifyPassword(userId,tf_passwordUpdate.getText());
        /*Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
        Stage window = (Stage) savePassword.getScene().getWindow();
        window.setScene(new Scene(root,1098,667));*/
        Stage window = (Stage) savePassword.getScene().getWindow();

        Navigate.navigate(savePassword,"/views/mainLoginSignUp.fxml",window);

        general_pane.setEffect(null);
    }



}
