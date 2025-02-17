package com.example.ewaste.controllers;

import com.example.ewaste.Main;
import com.example.ewaste.entities.GetData;
import com.example.ewaste.utils.DataBase;
import com.example.ewaste.entities.User;
import com.example.ewaste.repository.AuthRepository;
import com.example.ewaste.repository.CitoyenRepository;
import com.example.ewaste.repository.EmployeeRepository;
import com.example.ewaste.utils.OpenAiApi;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;



public class DashboardController implements Initializable {


    public TextField addEmployee_telephone;
    public PasswordField addEmployee_password;
    @FXML
    private Button addEmployee_addBtn;

    @FXML
    private DatePicker addEmployee_birthDate;

    @FXML
    private Button addEmployee_btn;

    @FXML
    private Button addEmployee_clearBtn;



    @FXML
    private TableColumn<User, Integer> addEmployee_col_telephone;
    @FXML
    private TableColumn<User, String> addEmployee_col_photo;

    @FXML
    private TableColumn<User, String> addEmployee_col_firstName;

    @FXML
    private TableColumn<User, String> addEmployee_col_email;

    @FXML
    private TableColumn<User, String> addEmployee_col_lastName;

    @FXML
    private TableColumn<User, String> addEmployee_col_status;

    @FXML
    private TableColumn<User, String> addEmployee_col_employeeNum;

    @FXML
    private TableColumn<User, String> addEmployee_col_birthDate;

//    @FXML
//    private ComboBox<?> addStudent_course;

    @FXML
    private Button addEmployee_deleteBtn;

    @FXML
    private TextField addEmployee_firstName;

    @FXML
    private TextField addEmployee_email;

    @FXML
    private AnchorPane addEmployee_form;
//
//    @FXML
//    private ComboBox<?> addStudent_gender;

    @FXML
    private ImageView addEmployee_imageView;

    @FXML
    private Button addEmployee_insertBtn;

    @FXML
    private TextField addEmployee_lastName;

    @FXML
    private TextField addEmployee_search;

    @FXML
    private ComboBox<?> addEmployee_status;

    @FXML
    private TextField addEmployee_EmployeeNum;

    @FXML
    private TableView<User> addEmployee_tableView;

    @FXML
    private Button addEmployee_updateBtn;

//    @FXML
//    private ComboBox<?> addStudent_year;

    @FXML
    private Button availableCourse_addBtn;

    @FXML
    private Button availableCourse_clearBtn;

    @FXML
    private TableColumn<User, String> availableCourse_col_course;

    @FXML
    private TableColumn<User, String> availableCourse_col_degree;

    @FXML
    private TableColumn<User, String> availableCourse_col_description;

    @FXML
    private TextField availableCourse_course;

    @FXML
    private TextField availableCourse_degree;

    @FXML
    private Button availableCourse_deleteBtn;

    @FXML
    private TextField availableCourse_description;

    @FXML
    private AnchorPane availableCourse_form;

    @FXML
    private TableView<User> availableCourse_tableView;

    @FXML
    private Button availableCourse_updateBtn;

    @FXML
    private Button availableCourses_btn;

    @FXML
    private Button close_btn;

    @FXML
    private Button home_btn;

    @FXML
    private AnchorPane home_form;

    @FXML
    private Label home_totalEmployee;

    @FXML
    private BarChart<?, ?> home_totalEnrolledChart;

    @FXML
    private Label home_totalCitoyen;

    @FXML
    private LineChart<String, Number> home_totalEmployeeChart;

    @FXML
    private Label home_totalMale;

    @FXML
    private LineChart<String, Number> home_totalCitoyenChart;

    @FXML
    private Button logout_btn;

    @FXML
    private AnchorPane main_form;

    @FXML
    private Button minimize_btn;

    @FXML
    private Button studentGrade_btn;

    @FXML
    private Button generate_rapport;





    @FXML
    private Button generateRapport_btn;


    @FXML
    private Label rapportTitle;

    @FXML
    private TextArea rapportContent;

    @FXML
    private Button studentGrade_clearBtn;
    @FXML
    private VBox rapportContentContainer;

    @FXML
    private TableColumn<User, String> studentGrade_col_course;

    @FXML
    private TableColumn<User, String> studentGrade_col_final;

    @FXML
    private TableColumn<User, String> studentGrade_col_firstSem;

    @FXML
    private TableColumn<User, String> studentGrade_col_secondSem;

    @FXML
    private TableColumn<User, String> studentGrade_col_studentNum;

    @FXML
    private TableColumn<User, String> studentGrade_col_year;

    @FXML
    private Label studentGrade_course;

    @FXML
    private TextField studentGrade_firstSem;

    @FXML
    private AnchorPane studentGrade_form;

    @FXML
    private AnchorPane rapportDisplay;
    @FXML
    private TextField studentGrade_search;

    @FXML
    private TextField studentGrade_secondSem;

    @FXML
    private TextField studentGrade_studentNum;

    @FXML
    private TableView<User> studentGrade_tableView;

    @FXML
    private Button studentGrade_updateBtn;

    @FXML
    private Label studentGrade_year;

    @FXML
    private Label username;


    @FXML
    private StackPane loadingOverlay;

    @FXML
    private ImageView loadingGif;

    private double xOffset = 0;
    private double yOffset = 0;

    private String[] statusList = {"Disponible", "Non Disponible"};

    CitoyenRepository cr = new CitoyenRepository();
    EmployeeRepository er = new EmployeeRepository();
    //    START CODE FOR MINIMIZE BUTTON
    public void close_btn_OnAction() {
        System.exit(0);
    }
//    END CODE FOR MINIMIZE BUTTON

    //    START CODE FOR MINIMIZE BUTTON
    public void minimize_btn_onAction() {
        Stage stage = (Stage) main_form.getScene().getWindow();
        stage.setIconified(true);
    }
//    END CODE FOR MINIMIZE BUTTON

//    public void displayUsername () {
//        username.setText(GetData.username);
//    }
    public void defaultNav () {
        home_btn.setStyle("-fx-background-color: linear-gradient(to bottom right, #3f82ae, #26bf7d);");
    }



    @FXML
    private void generateRapport() throws IOException, InterruptedException {
        // Show the loading overlay immediately when the button is clicked
        loadingOverlay.setVisible(true);

        // Disable the button and change its text to indicate a loading state
        generateRapport_btn.setDisable(true);
        generateRapport_btn.setText("Generating...");

        // Run the API call in a separate thread to avoid freezing the UI
        new Thread(() -> {
            try {
                String prompt = "Données pour le rapport mensuel - Novembre 2024 :" +
                        "- Tâches : 150 (130 terminées, taux de réussite : 87%)." +
                        "- Réclamations : 45 (40 résolues, taux de résolution : 89%)." +
                        "- Avis des citoyens : Note moyenne de 4.2/5. " +
                        "Commentaires fréquents : Collectes ponctuelles mais quelques retards en fin de mois. " +
                        "- Zones problématiques : Quartier X (retards fréquents). " +
                        "Analyse ces données et rédige un rapport professionnel en français. " +
                        "Le rapport doit comporter une date de rédaction valide choisie de manière aléatoire, " +
                        "ainsi qu'une signature finale comprenant un nom et un titre aléatoires (par exemple, " +
                        "'Jean Dupont, Directeur des opérations'). " +
                        "Points clés : efficacité des collectes, satisfaction citoyenne, problèmes récurrents.";

                String rapport = callOpenAIFunction(prompt);

                // Update the UI on the JavaFX Application Thread
                Platform.runLater(() -> {
                    // Clear previous content
                    rapportContentContainer.getChildren().clear();

                    // Parse the full report into sections based on Markdown-style titles
                    List<Section> sections = parseSections(rapport);

                    // For each section, create a styled block that takes the full width of the container
                    for (Section section : sections) {
                        VBox sectionBox = new VBox();
                        sectionBox.getStyleClass().add("rapport-section");
                        sectionBox.setSpacing(5);
                        sectionBox.setMaxWidth(Double.MAX_VALUE); // Fill available width

                        Label sectionTitle = new Label(section.title);
                        sectionTitle.getStyleClass().add("rapport-section-title");

                        Label sectionContent = new Label(section.content);
                        sectionContent.setWrapText(true);

                        sectionBox.getChildren().addAll(sectionTitle, sectionContent);
                        rapportContentContainer.getChildren().add(sectionBox);
                    }

                    // Hide the loading overlay and re-enable the button
                    loadingOverlay.setVisible(false);
                    generateRapport_btn.setDisable(false);
                    generateRapport_btn.setText("Generate Rapport");
                });
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    // Hide the loading overlay and re-enable the button in case of error
                    loadingOverlay.setVisible(false);
                    generateRapport_btn.setDisable(false);
                    generateRapport_btn.setText("Generate Rapport");

                    // Display a styled error block
                    VBox errorBox = new VBox();
                    errorBox.getStyleClass().add("rapport-section");
                    errorBox.setSpacing(5);
                    errorBox.setMaxWidth(Double.MAX_VALUE);

                    Label errorTitle = new Label("Error");
                    errorTitle.getStyleClass().add("rapport-section-title");

                    Label errorContent = new Label("Error generating the report. Please try again.");
                    errorContent.setWrapText(true);

                    errorBox.getChildren().addAll(errorTitle, errorContent);
                    rapportContentContainer.getChildren().clear();
                    rapportContentContainer.getChildren().add(errorBox);
                });
            }
        }).start();
    }

    private String callOpenAIFunction(String prompt) throws IOException, InterruptedException {
        OpenAiApi api = new OpenAiApi();
        return api.genererRapport(prompt);
    }

    // Helper method to parse the OpenAI response into sections based on Markdown-style titles
    private List<Section> parseSections(String text) {
        List<Section> sections = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\*\\*(.*?)\\*\\*\\s*(.*?)(?=\\n\\*\\*|\\z)", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            String title = matcher.group(1).trim();
            String content = matcher.group(2).trim();
            sections.add(new Section(title, content));
        }
        return sections;
    }

    // Inner class representing a report section
    private static class Section {
        String title;
        String content;
        public Section(String title, String content) {
            this.title = title;
            this.content = content;
        }
    }


    //    START CODE FOR FORM SWITCHING
    private void showSection(AnchorPane sectionToShow, Button activeButton) {
        // Hide all sections
        home_form.setVisible(false);
        addEmployee_form.setVisible(false);
        availableCourse_form.setVisible(false);
        studentGrade_form.setVisible(false);
        rapportDisplay.setVisible(false);

        // Show the selected section
        sectionToShow.setVisible(true);

        // Reset button styles
        home_btn.setStyle("-fx-background-color: transparent");
        addEmployee_btn.setStyle("-fx-background-color: transparent");
        availableCourses_btn.setStyle("-fx-background-color: transparent");
        studentGrade_btn.setStyle("-fx-background-color: transparent");
        generate_rapport.setStyle("-fx-background-color: transparent");

        // Highlight the active button
        activeButton.setStyle("-fx-background-color: linear-gradient(to bottom right, #29AB87, #ACE1AF);");
    }

    public void switchFormOnAction(ActionEvent event) throws IOException, InterruptedException, SQLException {
        if (event.getSource() == home_btn) {
            showSection(home_form, home_btn);
            homeDisplayTotalCitoyen();
            homeDisplayTotalEmployee();
            homeDisplayCitoyenChart();
            homeDisplayEmployeeChart();



        } else if (event.getSource() == addEmployee_btn) {
            showSection(addEmployee_form, addEmployee_btn);
            addEmployeeShowListData();
//            addStudentShowListData();
//            addStudent_yearList();
//            addStudent_genderList();
//            addStudent_statusList();
//            setAddStudent_courseList();
            addEmployee_search_onKeyTyped();
        } else if (event.getSource() == availableCourses_btn) {
            showSection(availableCourse_form, availableCourses_btn);
//            availableCourseShowListData();
        } else if (event.getSource() == studentGrade_btn) {
            showSection(studentGrade_form, studentGrade_btn);
//            studentGradesShowListData();
//            studentGradeSearch_onAction();
        } else if (event.getSource() == generate_rapport) {
            showSection(rapportDisplay, generate_rapport);
            generateRapport_btn.setStyle("-fx-background-color: linear-gradient(to bottom right, #29AB87, #ACE1AF);");
        }
    }

//    END CODE FOR FORM SWITCHING

    //    START CODE FOR LOGOUT BUTTON
    public void logout_btn_onAction() {
        try {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Message");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to logout?");
            Optional<ButtonType> option = alert.showAndWait();
            if (option.get().equals(ButtonType.OK)) {
                logout_btn.getScene().getWindow().hide();
                Parent root = FXMLLoader.load(Main.class.getResource("/views/mainLoginSignUp.fxml"));
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
//    END CODE FOR LOGOUT BUTTON

    //    START CODE FOR HOME FORM
    public void homeDisplayTotalEmployee () throws SQLException {
        int countEnrolled =  er.getEmployeeCount();

        home_totalEmployee.setText(String.valueOf(countEnrolled));

    }

    public void homeDisplayTotalCitoyen () throws SQLException {
        int countCitoyen = cr.getCitoyenCount();
        home_totalCitoyen.setText(String.valueOf(countCitoyen));


    }
    public void homeDisplayMaleEnrolled () {
//        String sql = "SELECT COUNT(id) FROM student WHERE gender = 'Male' AND status = 'Enrolled'";
//        connect = DatabaseConnection.connectDb();
//        try {
//            int countMale = 0;
//            prepare = connect.prepareStatement(sql);
//            result = prepare.executeQuery();
//            if (result.next()){
//                countMale = result.getInt("COUNT(id)");
//            }
//            home_totalMale.setText(String.valueOf(countMale));
//        } catch (Exception e){e.printStackTrace();}
    }


    public void homeDisplayCitoyenChart() {
        final Connection conn = DataBase.getInstance().getConnection();

        home_totalCitoyenChart.getData().clear();

        String sql = "SELECT COUNT(id) FROM utilisateur WHERE role = 'CITOYEN'";
        try (PreparedStatement prepare = conn.prepareStatement(sql);
             ResultSet result = prepare.executeQuery()) {

            XYChart.Series<String, Number> chart = new XYChart.Series<>();
            chart.setName("Total Citoyens");

            if (result.next()) {
                int count = result.getInt(1);
                chart.getData().add(new XYChart.Data<>("Citoyens", count));
            }

            home_totalCitoyenChart.getData().add(chart);

        } catch (SQLException e) {
            e.printStackTrace(); // Consider logging this instead
        }
    }

    public void homeDisplayEmployeeChart() {

        home_totalEmployeeChart.getData().clear();

        String sql = "SELECT COUNT(id) FROM utilisateur WHERE role = 'EMPLOYEE'";
        try (PreparedStatement prepare = DataBase.getInstance().getConnection().prepareStatement(sql);
             ResultSet result = prepare.executeQuery()) {

            XYChart.Series<String, Number> chart = new XYChart.Series<>();
            chart.setName("Total Employees");

            if (result.next()) {
                int count = result.getInt(1);
                chart.getData().add(new XYChart.Data<>("Employees", count));
            }

            home_totalEmployeeChart.getData().add(chart);

        } catch (SQLException e) {
            e.printStackTrace(); // Consider logging this instead
        }
    }


    public void homeDisplayMaleEnrolledChart () {
//        home_totalMaleChart.getData().clear();
//        String sql = "SELECT date, COUNT(id) FROM student WHERE gender = 'Male' AND status = 'Enrolled' GROUP BY date ORDER BY TIMESTAMP(date) ASC LIMIT 5";
//        connect = DatabaseConnection.connectDb();
//        try {
//            XYChart.Series chart = new XYChart.Series();
//            prepare = connect.prepareStatement(sql);
//            result = prepare.executeQuery();
//            while (result.next()){
//                chart.getData().add(new XYChart.Data(result.getString(1), result.getInt(2)));
//            }
//            home_totalMaleChart.getData().add(chart);
//        } catch (Exception e) {e.printStackTrace();}
    }
//    END CODE FOR HOME FORM
    private  ObservableList<User> addEmployeeListD;
    @FXML

    public void addEmployeeShowListData() throws SQLException {
        addEmployeeListD = er.getEmployeList();
        addEmployee_col_employeeNum.setCellValueFactory(new PropertyValueFactory<>("id"));
        addEmployee_col_firstName.setCellValueFactory(new PropertyValueFactory<>("nom"));
        addEmployee_col_lastName.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        addEmployee_col_email.setCellValueFactory(new PropertyValueFactory<>("email"));
        addEmployee_col_birthDate.setCellValueFactory(new PropertyValueFactory<>("DateNss"));
        addEmployee_col_status.setCellValueFactory(new PropertyValueFactory<>("status"));
        addEmployee_col_telephone.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        addEmployee_col_photo.setCellValueFactory(new PropertyValueFactory<>("photoUrl"));

        addEmployee_tableView.setItems(addEmployeeListD);
    }

    private Image image;

    @FXML
    public void addEmployeeSelect() {
        User employeeD = addEmployee_tableView.getSelectionModel().getSelectedItem();

        if (employeeD == null) {
            return; // No selected item, exit early
        }

        addEmployee_EmployeeNum.setText(String.valueOf(employeeD.getId()));
        addEmployee_firstName.setText(employeeD.getNom());
        addEmployee_lastName.setText(employeeD.getPrenom());
        addEmployee_email.setText(employeeD.getEmail());
//        addEmployee_password.setText(employeeD.getPassword());
        addEmployee_telephone.setText(String.valueOf(employeeD.getTelephone()));

        // Convert Date to LocalDate
        Date date = employeeD.getDateNss();
        if (date != null) {
            LocalDate localDate = Instant.ofEpochMilli(date.getTime())
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            addEmployee_birthDate.setValue(localDate);
        } else {
            addEmployee_birthDate.setValue(null);
        }
        String photoUrl = employeeD.getPhotoUrl();
        String uri;
        if (photoUrl.startsWith("http://") || photoUrl.startsWith("https://")) {
            uri = photoUrl;
        } else {
            uri = "file:" + photoUrl;
        }
        System.out.println("URI: " + uri);
        Image image = new Image(uri, 120, 170, false, true);
        addEmployee_imageView.setImage(image);
        GetData.path = employeeD.getPhotoUrl();
        System.out.println("Stored path: " + GetData.path);
    }

//    @FXML
//    public void addStudent_yearList() {
//        List <String> yearL = new ArrayList<>();
//        for (String data: yearList) {
//            yearL.add(data);
//        }
//        ObservableList ObList = FXCollections.observableArrayList(yearL);
//        addStudent_year.setItems(ObList);
//    }
//    @FXML
//    public void addStudent_genderList(){
//        List<String> genderL = new ArrayList<>();
//        for (String data: genderList) {
//            genderL.add(data);
//        }
//        ObservableList ObList = FXCollections.observableArrayList(genderL);
//        addStudent_gender.setItems(ObList);
//    }
    @FXML
    public void addEmployee_statusList(){
        List <String> statusL = new ArrayList<>();
        for (String data: statusList) {
            statusL.add(data);
        }
        ObservableList ObList = FXCollections.observableArrayList(statusL);
        addEmployee_status.setItems(ObList);
    }
    @FXML
    public void setAddEmployee_courseList () {
        String listCourse = "SELECT * FROM course";
//        connect = DatabaseConnection.connectDb();
//        try {
//            ObservableList listC = FXCollections.observableArrayList();
//            prepare = connect.prepareStatement(listCourse);
//            result = prepare.executeQuery();
//            while (result.next()){
//                listC.add(result.getString("course"));
//            }
//            addStudent_course.setItems(listC);
//        } catch (Exception e) {e.printStackTrace();}
    }
@FXML
    public void addEmployee_insertBtn_onAction(){
        FileChooser open = new FileChooser();
        open.setTitle("Select Image File");
        open.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image File", "*jpg", "*png"));
        File file = open.showOpenDialog(main_form.getScene().getWindow());
        if (file != null) {
            Image image = new Image(file.toURI().toString(), 120, 170, false, true);
            addEmployee_imageView.setImage(image);
            GetData.path = file.getAbsolutePath();
        }
    }
    @FXML
    public void addEmployee_addBtn_onAction() {

        String insertData = "INSERT INTO utilisateur (nom, prenom, email, DateNss, telephone, status, role, photo,mdp) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?,?)";

        String checkData = "SELECT email FROM utilisateur WHERE email = ?";

        try {
            Alert alert;

            // Validate required fields
            if (addEmployee_firstName.getText().isEmpty()
                    || addEmployee_lastName.getText().isEmpty()
                    || addEmployee_email.getText().isEmpty()
                    || addEmployee_birthDate.getValue() == null
                    || addEmployee_telephone.getText().isEmpty()
                    || addEmployee_status.getSelectionModel().getSelectedItem() == null
                    || addEmployee_password.getText().isEmpty()
                    || GetData.path == null || GetData.path.isEmpty()) {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Message");
                alert.setHeaderText(null);
                alert.setContentText("Fields cannot be empty!");
                alert.showAndWait();
                return;
            }

            // Check if employee already exists (by email)
            try (PreparedStatement checkStmt = DataBase.getInstance().getConnection().prepareStatement(checkData)) {
                checkStmt.setString(1, addEmployee_email.getText());
                ResultSet result = checkStmt.executeQuery();
                if (result.next()) {
                    alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error Message");
                    alert.setHeaderText(null);
                    alert.setContentText("Employee with email " + addEmployee_email.getText() + " already exists!");
                    alert.showAndWait();
                    return;
                }
            }

            // Convert JavaFX DatePicker to SQL Date
            java.sql.Date sqlBirthDate = java.sql.Date.valueOf(addEmployee_birthDate.getValue());

            // Insert new employee
            try (PreparedStatement insertStmt = DataBase.getInstance().getConnection().prepareStatement(insertData)) {
                insertStmt.setString(1, addEmployee_firstName.getText());
                insertStmt.setString(2, addEmployee_lastName.getText());
                insertStmt.setString(3, addEmployee_email.getText());
                insertStmt.setDate(4, sqlBirthDate);
                insertStmt.setInt(5, Integer.parseInt(addEmployee_telephone.getText()));
                insertStmt.setString(6, addEmployee_status.getSelectionModel().getSelectedItem().toString());
                insertStmt.setString(7, "EMPLOYEE"); // Fixed role as "employe"


                // Format photo URL
                String uri = GetData.path.replace("\\", "\\\\");
                insertStmt.setString(8, uri);
                insertStmt.setString(9, AuthRepository.hashPassword2(addEmployee_password.getText()));

                insertStmt.executeUpdate();
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }

            // Show success alert
            alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information Message");
            alert.setHeaderText(null);
            alert.setContentText("Employee Successfully Added!");
            alert.showAndWait();

            // Reload data & clear fields
            addEmployeeShowListData();
            addEmployee_clearBtn_onAction();

        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText(null);
            alert.setContentText("Error while adding employee: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    public void addEmployee_updateBtn_onAction() {

        // Prepare SQL query
        String updateData = "UPDATE utilisateur SET "
                + "nom = ?, "
                + "prenom = ?, "
                + "email = ?, "
                + "DateNss = ?, "
                + "telephone = ?, "
                + "status = ?, "
                + "role = ?, "
                + "photo = ? "
                + "WHERE email = ?";

        // Validate required fields
        if (addEmployee_firstName.getText().isEmpty()
                || addEmployee_lastName.getText().isEmpty()
                || addEmployee_email.getText().isEmpty()
                || addEmployee_birthDate.getValue() == null
                || addEmployee_telephone.getText().isEmpty()
                || addEmployee_status.getSelectionModel().getSelectedItem() == null
//                || addEmployee_password.getText().isEmpty()
                || GetData.path == null || GetData.path.isEmpty()) {

            showAlert(Alert.AlertType.ERROR, "Error Message", "Fields cannot be empty!");
            return;
        }

        // Confirm update action
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Message");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to update the employee with email: " + addEmployee_email.getText() + "?");

        Optional<ButtonType> option = alert.showAndWait();
        if (option.isEmpty() || !option.get().equals(ButtonType.OK)) {
            return;
        }

        try (Connection connect = DataBase.getInstance().getConnection();
             PreparedStatement statement = connect.prepareStatement(updateData)) {

            // Prepare data for updating
            String uri = GetData.path.replace("\\", "\\\\");

            statement.setString(1, addEmployee_firstName.getText());
            statement.setString(2, addEmployee_lastName.getText());
            statement.setString(3, addEmployee_email.getText());
            statement.setDate(4, java.sql.Date.valueOf(addEmployee_birthDate.getValue()));
            statement.setInt(5, Integer.parseInt(addEmployee_telephone.getText()));
            statement.setString(6, addEmployee_status.getSelectionModel().getSelectedItem().toString());
            statement.setString(7, "EMPLOYEE"); // Role fixed as "EMPLOYEE"
            statement.setString(8, uri);
            statement.setString(9, addEmployee_email.getText()); // Update by email

            // Execute update
            statement.executeUpdate();

            // Success message
            showAlert(Alert.AlertType.INFORMATION, "Information Message", "Successfully Updated!");

            // Reload the updated list
            addEmployeeShowListData();

            // Clear the fields
            addEmployee_clearBtn_onAction();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Error while updating employee: " + e.getMessage());
        }
    }

    // Utility method for showing alerts
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }



    @FXML
    public void addEmployee_deleteBtn_onAction() {


        String deleteData = "DELETE FROM utilisateur WHERE email = ?"; // Deleting by email

        // Validate if email is provided
        if (addEmployee_email.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error Message", "Email cannot be empty!");
            return;
        }

        // Confirm delete action
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Message");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to delete the employee with email: " + addEmployee_email.getText() + "?");

        Optional<ButtonType> option = alert.showAndWait();
        if (option.isEmpty() || !option.get().equals(ButtonType.OK)) {
            return;
        }

        try (Connection connect =DataBase.getInstance().getConnection();
             PreparedStatement deleteStmt = connect.prepareStatement(deleteData)) {

            // Delete employee by email
            deleteStmt.setString(1, addEmployee_email.getText());
            deleteStmt.executeUpdate();

            // Success message
            showAlert(Alert.AlertType.INFORMATION, "Information Message", "Successfully Deleted!");

            // Reload the updated list
            addEmployeeShowListData();

            // Clear the fields
            addEmployee_clearBtn_onAction();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Error while deleting employee: " + e.getMessage());
        }
    }
    @FXML
    public void addEmployee_clearBtn_onAction() {
        addEmployee_EmployeeNum.setText("");
        addEmployee_firstName.setText("");
        addEmployee_lastName.setText("");
        addEmployee_email.setText("");
        addEmployee_birthDate.setValue(null);
        addEmployee_telephone.setText("");
        addEmployee_status.getSelectionModel().clearSelection();
        addEmployee_imageView.setImage(null);
        addEmployee_password.setText("");
        addEmployee_telephone.setText("");
        GetData.path = "";
    }

    @FXML
    public void addEmployee_search_onKeyTyped() {
        // Assuming addEmployeeListD is a properly populated ObservableList<User>
        FilteredList<User> filter = new FilteredList<>(addEmployeeListD, e -> true);

        // Assuming addEmployee_search is your TextField for searching
        addEmployee_search.textProperty().addListener((observable, oldValue, newValue) -> {
            filter.setPredicate(predicateUser -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String searchKey = newValue.toLowerCase();

                return Stream.of(
                                String.valueOf(predicateUser.getId()),
                                String.valueOf(predicateUser.getTelephone()),
                                predicateUser.getNom() != null ? predicateUser.getNom() : "",
                                predicateUser.getPrenom() != null ? predicateUser.getPrenom() : "",
                                predicateUser.getEmail() != null ? predicateUser.getEmail() : "",
                                predicateUser.getDateNss() != null ? String.valueOf(predicateUser.getDateNss()) : "",
                                predicateUser.getStatus() != null ? predicateUser.getStatus() : ""
                        )
                        .anyMatch(data -> data.toLowerCase().contains(searchKey));// Use toLowerCase() for case-insensitive search
            });
        });

        SortedList<User> sortList = new SortedList<>(filter);

        // Assuming addEmployee_tableView is your TableView
        sortList.comparatorProperty().bind(addEmployee_tableView.comparatorProperty());
        addEmployee_tableView.setItems(sortList);
    }

//    END CODE FOR ADD STUDENT FORM

    //    START CODE FOR AVAILABLE COURSES FORM
//    public ObservableList <User> availableCourseListData(){
//        ObservableList <User> listCourses = FXCollections.observableArrayList();
//        String sql = "SELECT * FROM course";
//        try {
//            User courseD;
//            connect = DatabaseConnection.connectDb();
//            prepare = connect.prepareStatement(sql);
//            result = prepare.executeQuery();
//            while (result.next()){
//                courseD = new User(result.getString("course"),
//                        result.getString("description"),
//                        result.getString("degree"));
//                listCourses.add(courseD);
//            }
//        }catch (Exception e) {e.printStackTrace();}
//        return listCourses;
//    }
    private ObservableList <User> availableCourseList;
    public void availableCourseShowListData () {
//        availableCourseList = availableCourseListData();

        availableCourse_col_course.setCellValueFactory(new PropertyValueFactory<>("course"));
        availableCourse_col_description.setCellValueFactory(new PropertyValueFactory<>("description"));
        availableCourse_col_degree.setCellValueFactory(new PropertyValueFactory<>("degree"));

        availableCourse_tableView.setItems(availableCourseList);
    }
    public void availableCourseSelect () {
        User courseD = availableCourse_tableView.getSelectionModel().getSelectedItem();
//        int num = availableCourse_tableView.getSelectionModel().getSelectedIndex();
//        if ((num - 1 ) < - 1) {return;}
//        availableCourse_course.setText(courseD.getCourse());
//        availableCourse_description.setText(courseD.getDescription());
//        availableCourse_degree.setText(courseD.getDegree());
    }
    public void availableCourse_addBtn_onAction () {
        String insertData = "INSERT INTO course (course, description, degree) VALUES (?, ?, ?)";
//        connect = DatabaseConnection.connectDb();
//        try {
//            Alert alert;
//            if (availableCourse_course.getText().isEmpty() || availableCourse_description.getText().isEmpty() || availableCourse_degree.getText().isEmpty()) {
//                alert = new Alert(Alert.AlertType.ERROR);
//                alert.setTitle("Error Message");
//                alert.setHeaderText(null);
//                alert.setContentText("Fields cannot be empty");
//                alert.showAndWait();
//            } else {
//                String checkData = "SELECT course FROM course WHERE course = '" + availableCourse_course.getText() + "'";
//                statement = connect.createStatement();
//                result = statement.executeQuery(checkData);
//                if (result.next()) {
//                    alert = new Alert(Alert.AlertType.ERROR);
//                    alert.setTitle("Error Message");
//                    alert.setHeaderText(null);
//                    alert.setContentText("Course: " + availableCourse_course.getText() + " already exists!");
//                    alert.showAndWait();
//                } else {
//                    prepare = connect.prepareStatement(insertData);
//                    prepare.setString(1, availableCourse_course.getText());
//                    prepare.setString(2, availableCourse_description.getText());
//                    prepare.setString(3, availableCourse_degree.getText());
//                    prepare.executeUpdate();
//
//                    alert = new Alert(Alert.AlertType.INFORMATION);
//                    alert.setTitle("Information Message");
//                    alert.setHeaderText(null);
//                    alert.setContentText("Successfully Added!");
//                    alert.showAndWait();
//                    //  TO LOAD THE UPDATED TABLE AFTER OPERATION
//                    availableCourseShowListData();
//                    //  TO CLEAR THE TEXT FIELDS
//                    availableCourse_clearBtn_onAction();
//                }
//            }
//        }catch (Exception e) {e.printStackTrace();}
    }
    public void availableCourse_updateBtn_onAction () {
        String updateData = "UPDATE course SET description = '"
                + availableCourse_description.getText() + "', degree = '"
                + availableCourse_degree.getText() + "' WHERE course = '"
                + availableCourse_course.getText() + "'";
//        connect = DatabaseConnection.connectDb();
//        try {
//            Alert alert;
//            if (availableCourse_course.getText().isEmpty() || availableCourse_description.getText().isEmpty() || availableCourse_degree.getText().isEmpty()) {
//                alert = new Alert(Alert.AlertType.ERROR);
//                alert.setTitle("Error Message");
//                alert.setHeaderText(null);
//                alert.setContentText("Fields cannot be empty");
//                alert.showAndWait();
//            } else {
//                alert = new Alert(Alert.AlertType.CONFIRMATION);
//                alert.setTitle("Confirmation Message");
//                alert.setHeaderText(null);
//                alert.setContentText("Are you sure you want to update Course: " + availableCourse_course.getText() + "?");
//                Optional<ButtonType> option = alert.showAndWait();
//                if (option.get().equals(ButtonType.OK)){
//                    statement = connect.createStatement();
//                    statement.executeUpdate(updateData);
//                    alert = new Alert(Alert.AlertType.INFORMATION);
//                    alert.setTitle("Information Message");
//                    alert.setHeaderText(null);
//                    alert.setContentText("Successfully Updated!");
//                    alert.showAndWait();
//                    //  TO LOAD THE UPDATED TABLE AFTER OPERATION
//                    availableCourseShowListData();
//                    //  TO CLEAR THE TEXT FIELDS
//                    availableCourse_clearBtn_onAction();
//                } else return;
//            }
//        } catch (Exception e) {e.printStackTrace();}
    }
    public void availableCourse_clearBtn_onAction () {
        availableCourse_course.setText("");
        availableCourse_description.setText("");
        availableCourse_degree.setText("");
    }
    public void availableCourse_deleteBtn_onAction() {
        String deleteData = "DELETE FROM course WHERE course = '"
                + availableCourse_course.getText() +"'";
//        connect = DatabaseConnection.connectDb();
//        try {
//            Alert alert;
//            if (availableCourse_course.getText().isEmpty() || availableCourse_description.getText().isEmpty() || availableCourse_degree.getText().isEmpty()) {
//                alert = new Alert(Alert.AlertType.ERROR);
//                alert.setTitle("Error Message");
//                alert.setHeaderText(null);
//                alert.setContentText("Fields cannot be empty");
//                alert.showAndWait();
//            } else {
//                alert = new Alert(Alert.AlertType.CONFIRMATION);
//                alert.setTitle("Confirmation Message");
//                alert.setHeaderText(null);
//                alert.setContentText("Are you sure you want to delete Course: " + availableCourse_course.getText() + "?");
//                Optional<ButtonType> option = alert.showAndWait();
//                if (option.get().equals(ButtonType.OK)) {
//                    statement = connect.createStatement();
//                    statement.executeUpdate(deleteData);
//                    alert = new Alert(Alert.AlertType.INFORMATION);
//                    alert.setTitle("Information Message");
//                    alert.setHeaderText(null);
//                    alert.setContentText("Successfully Deleted!");
//                    alert.showAndWait();
//                    //  TO LOAD THE UPDATED TABLE AFTER OPERATION
//                    availableCourseShowListData();
//                    //  TO CLEAR THE TEXT FIELDS
//                    availableCourse_clearBtn_onAction();
//                } else return;;
//            }
//        } catch (Exception e) {e.printStackTrace();}
    }
//    END CODE FOR AVAILABLE COURSES FORM

    //    START CODE FOR GRADES OF STUDENTS FORM
    public ObservableList <User>  studentGradesListData(){
        ObservableList <User> listData = FXCollections.observableArrayList();
        String sql = "SELECT * FROM student_grade";
//        connect = DatabaseConnection.connectDb();
//        try {
//            User studentD;
//            prepare = connect.prepareStatement(sql);
//            result = prepare.executeQuery();
//            while(result.next()){
//                studentD = new User(result.getInt("studentNum")
//                        , result.getString("year")
//                        , result.getString("course")
//                        , result.getDouble("first_sem")
//                        , result.getDouble("second_sem")
//                        , result.getDouble("final"));
//                listData.add(studentD);
//            }
//        } catch (Exception e) {e.printStackTrace();}
        return listData;
    }
    private ObservableList <User> studentGradesList;
    public void studentGradesShowListData () {
        studentGradesList = studentGradesListData();
        studentGrade_col_studentNum.setCellValueFactory(new PropertyValueFactory<>("studentNum"));
        studentGrade_col_year.setCellValueFactory(new PropertyValueFactory<>("year"));
        studentGrade_col_course.setCellValueFactory(new PropertyValueFactory<>("course"));
        studentGrade_col_firstSem.setCellValueFactory(new PropertyValueFactory<>("firstSem"));
        studentGrade_col_secondSem.setCellValueFactory(new PropertyValueFactory<>("secondSem"));
        studentGrade_col_final.setCellValueFactory(new PropertyValueFactory<>("finals"));
        studentGrade_tableView.setItems(studentGradesList);
    }
    public void studentGradesSelect () {
        User studentD = studentGrade_tableView.getSelectionModel().getSelectedItem();
        int num = studentGrade_tableView.getSelectionModel().getSelectedIndex();
        if ((num - 1) < - 1) {return;}
//        studentGrade_studentNum.setText(String.valueOf(studentD.getStudentNum()));
//        studentGrade_year.setText(studentD.getYear());
//        studentGrade_course.setText(studentD.getCourse());
//        studentGrade_firstSem.setText(String.valueOf(studentD.getFirstSem()));
//        studentGrade_secondSem.setText(String.valueOf(studentD.getSecondSem()));
    }
    public void studentGradeSearch_onAction() {
        FilteredList <User> filter = new FilteredList<>(studentGradesList, e-> true);
        studentGrade_search.textProperty().addListener((Observable, oldValue, newValue) ->{
//            filter.setPredicate(predictateUser ->{
//                if(newValue.isEmpty() || newValue == null) {
//                    return true;
//                }
//                String searchKey = newValue.toLowerCase();
//                if (predictateUser.getStudentNum().toString().contains(searchKey)){
//                    return true;
//                } else if (predictateUser.getYear().toLowerCase().contains(searchKey)){
//                    return true;
//                } else if (predictateUser.getCourse().toLowerCase().contains(searchKey)){
//                    return true;
//                } else if (predictateUser.getFirstSem().toString().contains(searchKey)){
//                    return true;
//                } else if (predictateUser.getSecondSem().toString().contains(searchKey)){
//                    return true;
//                } else if (predictateUser.getFinals().toString().contains(searchKey)){
//                    return true;
//                } else return false;
//            });
        });
//        SortedList <User> sortList = new SortedList<>(filter);
//        sortList.comparatorProperty().bind(studentGrade_tableView.comparatorProperty());
//        studentGrade_tableView.setItems(sortList);
    }
    public void studentGradesUpdateBtn_OnAction () {
        String checkData = "SELECT * FROM student_grade WHERE studentNum = '"
                + studentGrade_studentNum.getText() + "'";
//        connect = DatabaseConnection.connectDb();
//        double finalResult = 0;
//        try {
//            prepare = connect.prepareStatement(checkData);
//            result = prepare.executeQuery();
//            finalResult = (Double.parseDouble(studentGrade_firstSem.getText())
//                    + Double.parseDouble(studentGrade_secondSem.getText()))/2;
//
//            String updateData = "UPDATE student_grade SET "
//                    + "studentNum = '" + studentGrade_studentNum.getText()
//                    + "', year = '" + studentGrade_year.getText()
//                    + "', course = '" + studentGrade_course.getText()
//                    + "', first_sem = '" + studentGrade_firstSem.getText()
//                    + "', second_sem = '" + studentGrade_secondSem.getText()
//                    + "', final = '" + finalResult + "' WHERE studentNum = '"
//                    + studentGrade_studentNum.getText() + "'";
//            Alert alert;
//            if (studentGrade_studentNum.getText().isEmpty()
//                    || studentGrade_year.getText().isEmpty()
//                    || studentGrade_course.getText().isEmpty()){
//                alert = new Alert(Alert.AlertType.ERROR);
//                alert.setTitle("Error Message");
//                alert.setHeaderText(null);
//                alert.setContentText("Fields cannot be empty");
//                alert.showAndWait();
//            } else {
//                alert = new Alert(Alert.AlertType.CONFIRMATION);
//                alert.setTitle("Confirmation Message");
//                alert.setHeaderText(null);
//                alert.setContentText("Are you sure you want to update Student # " + studentGrade_studentNum.getText() + "?");
//                Optional <ButtonType> option = alert.showAndWait();
//                if (option.get().equals(ButtonType.OK)){
//                    statement = connect.createStatement();
//                    statement.executeUpdate(updateData);
//                    alert = new Alert(Alert.AlertType.INFORMATION);
//                    alert.setTitle("Information Message");
//                    alert.setHeaderText(null);
//                    alert.setContentText("Successfully Updated");
//                    alert.showAndWait();
//                    //  TO LOAD THE UPDATED TABLE AFTER OPERATION
//                    studentGradesShowListData();
//                    //  TO CLEAR THE TEXT FIELDS
//                    studentGradeClearBtn_onAction();
//                } else return;
//            }
//        } catch (Exception e) {e.printStackTrace();}
    }
    public void studentGradeClearBtn_onAction () {
        studentGrade_studentNum.setText("");
        studentGrade_year.setText("");
        studentGrade_course.setText("");
        studentGrade_firstSem.setText("");
        studentGrade_secondSem.setText("");
    }



    @Override
    public void initialize (URL url, ResourceBundle resourceBundle) {

        try {
            homeDisplayTotalCitoyen();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            homeDisplayTotalEmployee();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        homeDisplayCitoyenChart();
        homeDisplayEmployeeChart();
        try {
            addEmployeeShowListData();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        Image image = new Image(Main.class.getResourceAsStream("/com/example/ewaste/assets/iconLoadingGIF.gif"));
        loadingGif.setImage(image);
//        displayUsername();
//        defaultNav();
//        homeDisplayTotalEnrolledStudents();
//        homeDisplayMaleEnrolled();
//        homeDisplayFemaleEnrolled();
//
//        homeDisplayTotalEnrolledChart();
//        homeDisplayFemaleEnrolledChart();
//        homeDisplayMaleEnrolledChart();
//        //  TO SHOW IMMEDIATELY WHEN PROCEEDED TO DASHBOARD APPLICATION FORM
//        addStudentShowListData();
//        addStudent_yearList();
//        addStudent_genderList();
        addEmployee_statusList();
//        setAddStudent_courseList();
        addEmployee_search_onKeyTyped();
//
//        availableCourseShowListData();
//        studentGradesShowListData();
//        studentGradeSearch_onAction();
    }
//    END CODE FOR GRADES OF STUDENTS FORM


}