package com.example.ewaste.Controllers;


import com.example.ewaste.Entities.ApplicationContext;
import com.example.ewaste.Entities.User;
import com.example.ewaste.Entities.UserSession;
import com.example.ewaste.Repository.AuthRepository;
import com.example.ewaste.Repository.FaceRecognitionRepository;
import com.example.ewaste.Repository.UserRepository;
import com.example.ewaste.Utils.FaceDetector;
import com.example.ewaste.Utils.Modals;
import com.example.ewaste.Utils.Navigate;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_videoio.VideoCapture;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static com.example.ewaste.Utils.RoleNavigation.navigateUser;


public class LoginController implements Initializable {

    public Button forgot_password;
    @FXML
    private TextField ck_emailField;

    @FXML
    private TextField ck_passwordField;
  @FXML
    private Button signIn_btn;

    @FXML
    private Button recognizeButton;
    @FXML
    private Pane overlay;

    @FXML
    private ProgressIndicator progressIndicator;

    @FXML
    private Label statusLabel;
    private final FaceDetector faceDetector;
    private final FaceRecognitionRepository faceRecognitionRepository;
    private VideoCapture camera;



    public LoginController() {
        URL cascadeUrl = getClass().getResource("/com/example/ewaste/haarcascade_frontalface_default.xml");
        if (cascadeUrl == null) {
            throw new RuntimeException("Haar cascade file not found at /com/example/ewaste/haarcascade_frontalface_default.xml");
        }
        String cascadePath = cascadeUrl.getPath().replaceFirst("^/", "");
        System.out.println("Loading cascade from: " + cascadePath);
        this.faceDetector = new FaceDetector(cascadePath);
        this.faceRecognitionRepository = new FaceRecognitionRepository();
        initializeCamera();
    }

    private void showAlert(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
            alert.showAndWait();
        });
    }



    AuthRepository ar = new AuthRepository();
    UserRepository ur = new UserRepository();

    private void loginWithFace() {
        // Show the overlay and set initial status
        Platform.runLater(() -> {
            overlay.setVisible(true);
            overlay.setManaged(true); // Make it take space when visible
            statusLabel.setText("Detecting face...");
        });

        // Run face recognition in a background thread
        Task<Void> recognitionTask = new Task<>() {
            @Override
            protected Void call() {
                try {
                    System.out.println("Starting face login process...");
                    if (!camera.isOpened()) {
                        System.out.println("Camera not open, reinitializing...");
                        initializeCamera();
                    }

                    Mat frame = new Mat();
                    boolean frameCaptured = camera.read(frame);
                    System.out.println("Frame captured: " + frameCaptured);
                    System.out.println("Frame empty: " + frame.empty());
                    System.out.println("Frame size: " + frame.rows() + "x" + frame.cols());

                    Platform.runLater(() -> statusLabel.setText("Analyzing frame..."));
                    Mat face = faceDetector.detectAndExtractFace(frame);
                    if (face == null || face.empty()) {
                        System.out.println("Face detection failed: " + (face == null ? "null" : "empty"));
                        showAlert("No face detected. Please try again.");
                        return null;
                    }
                    System.out.println("Face detected - size: " + face.rows() + "x" + face.cols());

                    Platform.runLater(() -> statusLabel.setText("Recognizing face..."));
                    Integer recognizedUserId = faceRecognitionRepository.recognizeFace(face);
                    System.out.println("Recognized User ID: " + recognizedUserId);
                    if (recognizedUserId != null) {
                        System.out.println("Fetching user with ID: " + recognizedUserId);
                        User user = ur.getUserById(recognizedUserId);
                        if (user != null) {
                            System.out.println("User found: ID=" + user.getId() + ", Name=" + user.getNom());
                            UserSession userSession = new UserSession(user.getId(), user.getNom(), user.getPrenom(), user.getRole());
                            ApplicationContext.getInstance().setUserSession(userSession);
                            System.out.println("User session set. Role: " + userSession.getRole());
                            Platform.runLater(() -> {
                                statusLabel.setText("Login successful!");
                                Stage window = (Stage) recognizeButton.getScene().getWindow();
                                try {
                                    navigateUser(window, userSession.getRole());
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            });
                        } else {
                            System.out.println("No user found in database for ID: " + recognizedUserId);
                            showAlert("Recognized user not found in database.");
                        }
                    } else {
                        System.out.println("Face recognition failed or no training data available");
                        showAlert("Face recognition failed. Please ensure you have set up face login.");
                    }
                } catch (Exception e) {
                    System.err.println("Exception during face recognition: " + e.getMessage());
                    e.printStackTrace();
                    showAlert("Error during face recognition: " + e.getMessage());
                } finally {
                    releaseCamera();
                }
                return null;
            }
        };

        // Update UI when task completes or fails
        recognitionTask.setOnSucceeded(event -> Platform.runLater(() -> {
            new Thread(() -> {
                try {
                    Thread.sleep(1000); // Show success message for 1 second
                    Platform.runLater(() -> {
                        overlay.setVisible(false);
                        overlay.setManaged(false); // Collapse space again
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }));
        recognitionTask.setOnFailed(event -> Platform.runLater(() -> {
            statusLabel.setText("Recognition failed");
            new Thread(() -> {
                try {
                    Thread.sleep(1000); // Show failure message briefly
                    Platform.runLater(() -> {
                        overlay.setVisible(false);
                        overlay.setManaged(false); // Collapse space again
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }));

        // Start the task
        new Thread(recognitionTask).start();
    }
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

    private void initializeCamera() {
        camera = new VideoCapture(0);
        if (!camera.isOpened()) {
            System.err.println("Failed to open camera");
            showAlert("Cannot access webcam.");
        } else {
            System.out.println("Camera initialized successfully");
        }
    }

    private void releaseCamera() {
        if (camera != null && camera.isOpened()) {
            camera.release();
            System.out.println("Camera released");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        recognizeButton.setOnAction(event -> loginWithFace());

        // Ensure overlay is initially hidden and doesn’t take space
        overlay.setVisible(false);
        overlay.setManaged(false);
    }
}
