package com.example.ewaste.controllers;

import com.example.ewaste.Main;
import com.example.ewaste.entities.ApplicationContext;
import com.example.ewaste.entities.User;
import com.example.ewaste.entities.UserRole;
import com.example.ewaste.entities.UserSession;
import com.example.ewaste.repository.AuthRepository;
import com.example.ewaste.repository.UserRepository;
import com.example.ewaste.utils.Modals;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.dialogs.MFXGenericDialog;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import static com.example.ewaste.utils.Validator.isValidEmail;

public class UseraccountController implements Initializable {
    public Button Home_Btn;
    public Button Users_Btn;
    public Button Reclamations_Btn;
    public Button Events_Btn;
    public Button Logout_Btn;
    public Pane general_pane;
    public MFXButton Confirmer_AccountUser;
    public MFXTextField tf_UserAccountNumero;
    public MFXTextField tf_UserAccountEmail;
    public MFXButton Update_AccountUser;
    public MFXButton ConfirmerD0_AccountUser1;
    public ImageView ImageviewUser;
    public MFXGenericDialog DialogConfirm_Delete;
    public MFXButton AnnulerDelete1_User;
    public MFXButton ConfirmDelete1_User;
    public Label user_name;
    public MFXGenericDialog Dialog_UpdatePassword_User;
    public MFXButton UpdatePassword_User;
    public MFXButton AnnulerUpdateP_User;

    public MFXTextField tf_UserOldPassword;
    public MFXTextField tf_UserNewPassword;
    @FXML
    private MFXButton ChangeImageBtn;

    private File selectedImageFile;

    AuthRepository au = new AuthRepository();
    UserRepository ur = new UserRepository();
    private double xOffset = 0;
    private double yOffset = 0;
    final  int x = 1315 ;
    final  int y = 890 ;
    int userId ;
    String userName ;
    String userPrenom ;
    UserRole userRole ;
//    static UserSession us = ApplicationContext.getInstance().getUserSession();


    public void getUserSession() {
        UserSession userSession = ApplicationContext.getInstance().getUserSession();
        if(userSession != null) {
            userId = userSession.getUserId() ;
            userName = userSession.getUserName() ;
            userPrenom = userSession.getPrenom() ;
            userRole = userSession.getRole() ;
        }
    }

    public void onHomeButtonClick(ActionEvent actionEvent) {
    }

    public void updateUser(ActionEvent actionEvent) {
        User a = ur.getUserById(userId);
        if(actionEvent.getSource() == Confirmer_AccountUser) {
            String email = tf_UserAccountEmail.getText();
            if (!isValidEmail(email)) {
                Modals.displayError("Invalid Email", "Please enter a valid email address.");
                return;
            }

            String numero = tf_UserAccountNumero.getText();
            // Optionally validate phone number...

            a.setEmail(email);
            a.setTelephone(Integer.parseInt(numero));

            // If an image was selected, update the user's image.
            if (selectedImageFile != null) {
                // For example, store the file path or convert the file to a byte[] as needed.
                a.setPhotoUrl(selectedImageFile.getAbsolutePath());
            }

            // Update the user data in your persistence layer.
            ur.updateEmailPhoneAndImage(a);
            Modals.displaySuccess("Profile updated successfully", "Update");
            afficherdetails();
        }
    }


    @FXML
    private void changeImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Profile Image");
        // Limit selection to image files
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        File file = fileChooser.showOpenDialog(ChangeImageBtn.getScene().getWindow());
        if (file != null) {
            selectedImageFile = file;  // Save the selected file for later update
            Image image = new Image(file.toURI().toString());
            ImageviewUser.setImage(image);
        }
    }


    public void afficherUpdate(ActionEvent actionEvent) {
        // Ensure the UI updates are done on the JavaFX Application Thread
        Platform.runLater(() -> {
            // Ensure Dialog_Camera is not affecting the visibility of the update password dialog

            // Apply blur effect to the general pane
            BoxBlur boxBlur = new BoxBlur(5, 5, 3);
            general_pane.setEffect(boxBlur);


            // Make the update password dialog visible and apply fade-in transition
            Dialog_UpdatePassword_User.setOpacity(0);
            Dialog_UpdatePassword_User.setVisible(true);

            // Create a fade-in effect
            FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.5), Dialog_UpdatePassword_User);
            fadeIn.setToValue(1.0);
            fadeIn.setOnFinished(event -> general_pane.setEffect(boxBlur)); // Apply blur after fade-in
            fadeIn.play();
        });
    }

    public void afficherDeleteDialog(ActionEvent actionEvent) {
        DialogConfirm_Delete.setOpacity(0);
        DialogConfirm_Delete.setVisible(true);
        // Créer une transition de fondu pour simuler l'effet de coup d'éponge
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.5), DialogConfirm_Delete);
        fadeIn.setToValue(1.0);
        fadeIn.play();

        BoxBlur boxBlur = new BoxBlur();
        boxBlur.setWidth(5);
        boxBlur.setHeight(5);
        boxBlur.setIterations(3);
        general_pane.setEffect(boxBlur);
    }

    public void closeDeleteD(ActionEvent actionEvent) {
        DialogConfirm_Delete.setVisible(false);
        general_pane.setEffect(null);
    }

    public void deleteUser(ActionEvent actionEvent) throws IOException {
        if(actionEvent.getSource()==ConfirmDelete1_User)
        {
            ur.deleteEntity(userId);
            Parent root = FXMLLoader.load(Main.class.getResource("views/mainLoginSignUp.fxml"));
            Stage window = (Stage) ConfirmDelete1_User.getScene().getWindow();
            window.setScene(new Scene(root,x,y));
        }
    }



    void afficherdetails() {
        User a = ur.getUserById(userId);
        if (a.getPhotoUrl() != null) {
            try {
                Image image = new Image(a.getPhotoUrl());
                ImageviewUser.setImage(image);
            } catch (Exception ex) {
                ex.printStackTrace();
                System.out.println("Error loading image: " + ex.getMessage());
            }
        }
        tf_UserAccountEmail.setText(String.valueOf(a.getEmail()));
        tf_UserAccountNumero.setText(String.valueOf(a.getTelephone()));
    }


    public void ondialogueupdatebtnClick(ActionEvent actionEvent) {
        Dialog_UpdatePassword_User.setVisible(false);
        general_pane.setEffect(null);
    }

    public void UpdatePassword(ActionEvent actionEvent) {
        if(actionEvent.getSource()==UpdatePassword_User)
        {
            if(au.validerPassword(userId,tf_UserOldPassword.getText()))
            {
                au.modifyPassword(userId,tf_UserNewPassword.getText());
            }
        }
    }






    public void onUserButtonClick(ActionEvent actionEvent) {
    }

    public void onReclamationsButtonClick(ActionEvent actionEvent) {
    }

    public void onEventsButtonClick(ActionEvent actionEvent) {
    }

    public void onLogoutButtonClick(ActionEvent actionEvent) {
        try {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Message");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to logout?");
            Optional<ButtonType> option = alert.showAndWait();
            if (option.get().equals(ButtonType.OK)) {
                Logout_Btn.getScene().getWindow().hide();
                Parent root = FXMLLoader.load(Main.class.getResource("views/mainLoginSignUp.fxml"));
                Stage stage = new Stage();
                Scene scene = new Scene(root);
                stage.setTitle("Login | Student Management System");
                // Make the window draggable
                root.setOnMousePressed((MouseEvent event) -> {
                    xOffset = event.getSceneX();
                    yOffset = event.getSceneY();
                });
                root.setOnMouseDragged((MouseEvent event) -> {
                    stage.setX(event.getScreenX() - xOffset);
                    stage.setY(event.getScreenY() - yOffset);
                    stage.setOpacity(.6);
                });

                // Reset opacity on mouse release
                root.setOnMouseReleased((MouseEvent event) -> {
                    stage.setOpacity(1);
                });
                stage.initStyle(StageStyle.TRANSPARENT);
                stage.setScene(scene);
                stage.show();
            }else return;

        }catch (Exception e) {e.printStackTrace();}
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        getUserSession();
//        general_pane.setBackground(new Background(
//                new BackgroundFill(Color.web("#29AB87"), CornerRadii.EMPTY, Insets.EMPTY)
//        ));

        System.out.println(userId);
        Dialog_UpdatePassword_User.setVisible(false);
        DialogConfirm_Delete.setVisible(false);
        afficherdetails();

    }
}
