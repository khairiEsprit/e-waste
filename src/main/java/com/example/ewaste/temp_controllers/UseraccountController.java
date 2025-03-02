package com.example.ewaste.Controllers;

import com.example.ewaste.Entities.ApplicationContext;
import com.example.ewaste.Entities.User;
import com.example.ewaste.Entities.UserRole;
import com.example.ewaste.Entities.UserSession;
import com.example.ewaste.Repository.AuthRepository;
import com.example.ewaste.Repository.FaceRecognitionRepository;
import com.example.ewaste.Repository.UserRepository;
import com.example.ewaste.Utils.FaceDetector;
import com.example.ewaste.Utils.Modals;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.dialogs.MFXGenericDialog;
import javafx.animation.FadeTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
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
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_videoio.VideoCapture;
import org.bytedeco.opencv.global.opencv_imgcodecs;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import static com.example.ewaste.Utils.Validator.isValidEmail;

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
    public MFXGenericDialog faceSetupDialog;
    public ImageView webcamPreview;
    public Label statusLabel;
    public MFXButton captureButton;
    public MFXButton saveButton;
    public MFXButton closeFaceDialogButton;
    @FXML
    private MFXButton ChangeImageBtn;


    @FXML
    private Button takePhotoButton;

    private File selectedImageFile;

    AuthRepository au = new AuthRepository();
    UserRepository ur = new UserRepository();
    private double xOffset = 0;
    private double yOffset = 0;
    final int x = 1315;
    final int y = 890;
    private Integer userId; // Use Integer for userId
    String userName;
    String userPrenom;
    UserRole userRole;

    // face detector
    private final FaceDetector faceDetector;
    private final FaceRecognitionRepository faceRecognitionRepository;
    private static final int REQUIRED_PHOTOS = 20;
    private static final String FACES_DIR = "C:/Users/User/Documents/e-waste/e-waste/faces/";
    private VideoCapture camera;
    private List<Mat> capturedFaces = new ArrayList<>();
    private boolean isCapturing = false;

    public void getUserSession() {
        UserSession userSession = ApplicationContext.getInstance().getUserSession();
        if (userSession != null) {
            userId = userSession.getUserId();
            userName = userSession.getUserName();
            userPrenom = userSession.getPrenom();
            userRole = userSession.getRole();
        }
    }

    public UseraccountController() {
        URL cascadeUrl = getClass().getResource("/com/example/ewaste/haarcascade_frontalface_default.xml");
        if (cascadeUrl == null) {
            throw new RuntimeException("Haar cascade file not found at /com/example/ewaste/haarcascade_frontalface_default.xml");
        }
        String cascadePath = cascadeUrl.getPath().replaceFirst("^/", "");
        System.out.println("Loading cascade from: " + cascadePath);
        this.faceDetector = new FaceDetector(cascadePath);
        this.faceRecognitionRepository = new FaceRecognitionRepository();
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        getUserSession();
        if (userId == null) {
            showAlert("Error: User session not found. Please log in.");
            return;
        }

        initializeCamera();
        setupFaceRecognitionStatus();
        initializeBindings();
        afficherdetails();
    }

    private void setupFaceRecognitionStatus() {
        File userDir = new File(FACES_DIR + userId.toString());
        if (userDir.exists() && userDir.listFiles() != null && userDir.listFiles().length > 0) {
            takePhotoButton.setDisable(true);
            takePhotoButton.setText("Activated");
        } else {
            takePhotoButton.setOnAction(event -> showFaceSetupDialog());
        }
    }

    private void initializeBindings() {
        Dialog_UpdatePassword_User.setVisible(false);
        DialogConfirm_Delete.setVisible(false);
        faceSetupDialog.setVisible(false);

        captureButton.setOnAction(event -> captureFace());
        saveButton.setOnAction(event -> saveFace());
        closeFaceDialogButton.setOnAction(event -> closeFaceDialog());
    }

    @FXML
    private void showFaceSetupDialog() {
        capturedFaces.clear();
        faceSetupDialog.setVisible(true);
        startWebcamPreview();
    }

    @FXML
    private void captureFace() {
        if (!isCapturing) {
            isCapturing = true;
            statusLabel.setText("Capturing... (" + (capturedFaces.size() + 1) + "/" + REQUIRED_PHOTOS + ")");

            Mat frame = new Mat();
            if (camera.read(frame)) {
                Mat face = faceDetector.detectAndExtractFace(frame);
                if (face != null && !face.empty()) {
                    capturedFaces.add(face);
                    statusLabel.setText("Captured " + capturedFaces.size() + "/" + REQUIRED_PHOTOS);

                    if (capturedFaces.size() >= REQUIRED_PHOTOS) {
                        captureButton.setDisable(true);
                        saveButton.setDisable(false);
                        statusLabel.setText("Capture complete. Click Save to finish.");
                    }
                } else {
                    statusLabel.setText("No face detected. Try again.");
                }
            }
            isCapturing = false;
        }
    }

    @FXML
    private void saveFace() {
        String userDirPath = FACES_DIR + userId.toString();
        File userDir = new File(userDirPath);
        if (!userDir.exists()) {
            userDir.mkdirs();
        }

        try {
            for (int i = 0; i < capturedFaces.size(); i++) {
                String photoPath = userDirPath + "/photo_" + System.currentTimeMillis() + "_" + i + ".jpg";
                opencv_imgcodecs.imwrite(photoPath, capturedFaces.get(i));
            }

            Thread trainThread = new Thread(() -> {
                faceRecognitionRepository.trainModel();
                Platform.runLater(() -> {
                    showAlert("Face recognition setup completed successfully!");
                    takePhotoButton.setDisable(true);
                    takePhotoButton.setText("Face Recognition Set Up");
                    closeFaceDialog();
                });
            });
            trainThread.start();
        } catch (Exception e) {
            showAlert("Error saving face data: " + e.getMessage());
        }
    }

    @FXML
    private void closeFaceDialog() {
        stopWebcamPreview();
        faceSetupDialog.setVisible(false);
        capturedFaces.clear();
        captureButton.setDisable(false);
        saveButton.setDisable(true);
        statusLabel.setText("Position your face in front of the camera");
    }

    private void startWebcamPreview() {
        if (!camera.isOpened()) {
            initializeCamera();
        }

        Thread previewThread = new Thread(() -> {
            while (faceSetupDialog.isVisible()) {
                Mat frame = new Mat();
                if (camera.read(frame)) {
                    // Convert Mat to JavaFX Image (you'll need a utility method for this)
                    Image fxImage = matToJavaFXImage(frame); // Implement this conversion
                    Platform.runLater(() -> webcamPreview.setImage(fxImage));
                }
                try {
                    Thread.sleep(33); // ~30 FPS
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        previewThread.setDaemon(true);
        previewThread.start();
    }


    private Image matToJavaFXImage(Mat mat) {
        try {
            // Convert Mat to BufferedImage
            Mat convertedMat = new Mat();

            // If the Mat is not in BGR format, convert it
            if (mat.channels() == 1) {
                opencv_imgproc.cvtColor(mat, convertedMat, opencv_imgproc.COLOR_GRAY2BGR);
            } else if (mat.channels() == 3) {
                opencv_imgproc.cvtColor(mat, convertedMat, opencv_imgproc.COLOR_BGR2RGB);
            } else {
                convertedMat = mat; // Assume it's already in correct format
            }

            // Get the image data
            int width = convertedMat.cols();
            int height = convertedMat.rows();
            byte[] data = new byte[width * height * 3]; // 3 bytes per pixel (RGB)
            convertedMat.data().get(data);

            // Create BufferedImage
            BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
            byte[] targetPixels = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
            System.arraycopy(data, 0, targetPixels, 0, data.length);

            // Convert BufferedImage to JavaFX Image
            return SwingFXUtils.toFXImage(bufferedImage, null);

        } catch (Exception e) {
            System.err.println("Error converting Mat to JavaFX Image: " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            // Clean up
            if (mat != null && !mat.isNull()) {
                mat.release();
            }
        }
    }

    private void stopWebcamPreview() {
        releaseCamera();
    }

    private void initializeCamera() {
        camera = new VideoCapture(0);
        if (!camera.isOpened()) {
            showAlert("Cannot access webcam.");
        }
    }

    private void releaseCamera() {
        if (camera != null && camera.isOpened()) {
            camera.release();
        }
    }

    private void showAlert(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
            alert.showAndWait();
        });
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

            a.setEmail(email);
            a.setTelephone(Integer.parseInt(numero));

            if (selectedImageFile != null) {
                a.setPhotoUrl(selectedImageFile.getAbsolutePath());
            }

            ur.updateEmailPhoneAndImage(a);
            Modals.displaySuccess("Profile updated successfully", "Update");
            afficherdetails();
        }
    }


    @FXML
    private void changeImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Profile Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        File file = fileChooser.showOpenDialog(ChangeImageBtn.getScene().getWindow());
        if (file != null) {
            selectedImageFile = file;
            Image image = new Image(file.toURI().toString());
            ImageviewUser.setImage(image);
        }
    }


    public void afficherUpdate(ActionEvent actionEvent) {
        Platform.runLater(() -> {

            BoxBlur boxBlur = new BoxBlur(5, 5, 3);
            general_pane.setEffect(boxBlur);


            Dialog_UpdatePassword_User.setOpacity(0);
            Dialog_UpdatePassword_User.setVisible(true);

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
            Parent root = FXMLLoader.load(com.example.ewaste.Main.class.getResource("views/mainLoginSignUp.fxml"));
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
                Parent root = FXMLLoader.load(com.example.ewaste.Main.class.getResource("views/mainLoginSignUp.fxml"));
                Stage stage = new Stage();
                Scene scene = new Scene(root);
                root.setOnMousePressed((MouseEvent event) -> {
                    xOffset = event.getSceneX();
                    yOffset = event.getSceneY();
                });
                root.setOnMouseDragged((MouseEvent event) -> {
                    stage.setX(event.getScreenX() - xOffset);
                    stage.setY(event.getScreenY() - yOffset);
                    stage.setOpacity(.6);
                });

                root.setOnMouseReleased((MouseEvent event) -> {
                    stage.setOpacity(1);
                });
                stage.initStyle(StageStyle.TRANSPARENT);
                stage.setScene(scene);
                stage.show();
            }else return;

        }catch (Exception e) {e.printStackTrace();}
    }


}


