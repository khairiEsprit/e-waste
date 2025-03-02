package com.example.ewaste.Controllers;

import com.example.ewaste.Main;
import com.example.ewaste.Entities.GetData;
import com.example.ewaste.Entities.Poubelle;
import com.example.ewaste.Repository.*;
import com.example.ewaste.Utils.DataBase;
import com.example.ewaste.Entities.User;
import com.example.ewaste.Utils.FloatingChatbotButton;
import com.example.ewaste.Utils.OpenAiApi;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
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

    @FXML
    public TextField addEmployee_telephone;
    @FXML
    public PasswordField addEmployee_password;
    @FXML
    public AnchorPane rapportDisplay;
    @FXML
    public Button Map_view;
    @FXML
    public AnchorPane MapsDisplay;
    public AnchorPane mapContainer;
    public AnchorPane employeeCard;
    public AnchorPane citizenCard;
    public Label tableTitle;
    public AnchorPane citoyenCard;
    public AnchorPane contentArea;
    public ScrollPane infoScrollPane;
    public VBox infoContent;
    public AnchorPane centerContent;
    public AnchorPane sidebar;
    public Button CentrePage;
    public Button AvisPage;
    public Button backButton;

    @FXML
    private Button addEmployee_addBtn;

    @FXML
    private VBox address;


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



    @FXML
    private Button addEmployee_deleteBtn;

    @FXML
    private TextField addEmployee_firstName;

    @FXML
    private TextField addEmployee_email;

    @FXML
    private AnchorPane addEmployee_form;


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








    @FXML
    private Button close_btn;

    @FXML
    private Button home_btn;

    @FXML
    private AnchorPane home_form;

    @FXML
    private Label home_totalEmployee;



    @FXML
    private Label home_totalCitoyen;

    @FXML
    private LineChart<String, Number> home_totalEmployeeChart;



    @FXML
    private LineChart<String, Number> home_totalCitoyenChart;

    @FXML
    private Button logout_btn;

    @FXML
    private AnchorPane main_form;

    @FXML
    private Button minimize_btn;



    @FXML
    private Button generate_rapport;

    private MapBox mapboxMap;



    @FXML
    private Button generateRapport_btn;


    @FXML
    private Label rapportTitle;

    @FXML
    private TextArea rapportContent;


    @FXML
    private VBox rapportContentContainer;




    @FXML
    private Label username;


    @FXML
    private StackPane loadingOverlay;

    @FXML
    private ImageView loadingGif;

    private double xOffset = 0;
    private double yOffset = 0;

    private String[] statusList = {"Disponible", "Non Disponible"};
//    private final MapPoint point = new MapPoint(48.85,2.29);
    private List<AnchorPane> allSections;
    private List<Button> sidebarButtons;
    private List<Button> navbarButtons;
    private Map<String, Pane> friendPages = new HashMap<>();

    ChatBotInterface chat = new ChatBotInterface();

    CitoyenRepository cr = new CitoyenRepository();
    EmployeeRepository er = new EmployeeRepository();
    PoubelleRepository pr = new PoubelleRepository();
    CentreRepository ctr = new CentreRepository();
    private static final int CENTER_ID = 1;
private final  MapBox map = new MapBox();
    public void close_btn_OnAction() {
        System.exit(0);
    }


    @Override
    public void initialize (URL url, ResourceBundle resourceBundle) {


        FloatingChatbotButton chatbotButton = new FloatingChatbotButton(chat::openChatbot);
        main_form.getChildren().add(chatbotButton.getButton());

        // Positioning the button at the bottom-right corner
        AnchorPane.setBottomAnchor(chatbotButton.getButton(), 20.0);
        AnchorPane.setRightAnchor(chatbotButton.getButton(), 20.0);



        homeDisplayCitoyenChart();
        homeDisplayEmployeeChart();
        try {
            addEmployeeShowListData();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        sidebarButtons = Arrays.asList(home_btn, addEmployee_btn, Map_view, generate_rapport);

        // Initialize navbar (friend) buttons
        navbarButtons = Arrays.asList(CentrePage, AvisPage);

        // Initialize all sections
        allSections = new ArrayList<>();
        allSections.add(home_form);
        allSections.add(addEmployee_form);
        allSections.add(MapsDisplay);
        allSections.add(rapportDisplay);

        // Show home section by default
        showSection(home_form, home_btn);
        try {
            homeDisplayTotalCitoyen();
            homeDisplayTotalEmployee();
            // Add other initialization methods as needed
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Image image = new Image(Main.class.getResourceAsStream("/com/example/ewaste/assets/iconLoadingGIF.gif"));
        loadingGif.setImage(image);
        addEmployee_statusList();
//        setAddStudent_courseList();
        addEmployee_search_onKeyTyped();
        // code for gluon map
//        MapView map = CreateMapView();
//        address.getChildren().add(map);
//        VBox.setVgrow(map, Priority.ALWAYS);
//        System.out.println("MapView added to address container: " + (map != null));
        // code for mapbox

        Node mapView = map.getView();

        // Add the map view to the AnchorPane container.
        mapContainer.getChildren().add(mapView);

        // Set anchor constraints so that the map fills the container.
        AnchorPane.setTopAnchor(mapView, 0.0);
        AnchorPane.setBottomAnchor(mapView, 0.0);
        AnchorPane.setLeftAnchor(mapView, 0.0);
        AnchorPane.setRightAnchor(mapView, 0.0);

//        map.flyToLocation(36.8065, 10.1815, 12);
//
        displayPoubellesAndRoute(CENTER_ID);



    }


    public void displayPoubellesAndRoute(int CENTER_ID) {
        // Fetch center location and poubelles
        List<Poubelle> poubelleBins = pr.getPoubellesByCenter(CENTER_ID);
        float[] centerLocation;
        try {
            centerLocation = ctr.getLatitudeLongitude(CENTER_ID);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // Update Map: Mark the center and each poubelle on the map (as already implemented)
        map.addCustomMarker(centerLocation[0], centerLocation[1], "black", true, "Center of Ariana");

        // Sort poubelles by fill level (highest first)
        poubelleBins.sort((p1, p2) -> Float.compare(p2.getFillLevel(), p1.getFillLevel()));

        List<String> waypoints = new ArrayList<>();
        waypoints.add(centerLocation[1] + "," + centerLocation[0]); // Start from center

        for (Poubelle bin : poubelleBins) {
            String color;
            float fillLevel = bin.getFillLevel();
            if (fillLevel > 80) {
                color = "red";
            } else if (fillLevel > 60) {
                color = "orange";
            } else if (fillLevel > 40) {
                color = "yellow";
            } else {
                color = "green";
            }
            map.addCustomMarker(bin.getLatitude(), bin.getLongitude(), color, false, "Fill: " + fillLevel + "%");
            waypoints.add(bin.getLongitude() + "," + bin.getLatitude());
        }
        map.drawRoute(waypoints);

        // Update Info Panel:
        infoContent.getChildren().clear();

        // **Static Explanation for the Admin**
        Label explanation = new Label(
                "This map displays the optimized collection route for waste bins.\n" +
                        " - The black marker represents the center.\n" +
                        " - Colored markers represent waste bins:\n" +
                        "     - Red: Over 80% full (high priority)\n" +
                        "     - Orange: 60-80% full\n" +
                        "     - Yellow: 40-60% full\n" +
                        "     - Green: Less than 40% full (low priority)\n" +
                        "The route starts at the center and prioritizes bins based on their fill level, \n" +
                        "ensuring the most filled bins are collected first."
        );
        explanation.setWrapText(true);
        explanation.getStyleClass().add("admin-explanation");  // Custom style if needed
        infoContent.getChildren().add(explanation);

        // Separator
        Separator separator1 = new Separator();
        infoContent.getChildren().add(separator1);

        // Display center information
        Label centerInfo = new Label("Center: Ariana\nCoordinates: " + centerLocation[0] + ", " + centerLocation[1]);
        centerInfo.getStyleClass().add("center-info");
        infoContent.getChildren().add(centerInfo);

        // Separator
        Separator separator2 = new Separator();
        infoContent.getChildren().add(separator2);

        // Display each poubelle information
        for (Poubelle bin : poubelleBins) {
            String infoText = "Bin at (" + bin.getLatitude() + ", " + bin.getLongitude() + ")\nFill: " + bin.getFillLevel() + "%";
            Label binLabel = new Label(infoText);
            binLabel.getStyleClass().add("bin-info");
            infoContent.getChildren().add(binLabel);
        }
    }







    public void minimize_btn_onAction() {
        Stage stage = (Stage) main_form.getScene().getWindow();
        stage.setIconified(true);
    }

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


    private void hideAllSections() {
        for (AnchorPane section : allSections) {
            section.setVisible(false);
        }
    }

    // Reset styles of all buttons
    private void resetAllButtons() {
        for (Button btn : sidebarButtons) {
            btn.setStyle("-fx-background-color: transparent");
        }
        for (Button btn : navbarButtons) {
            btn.setStyle("-fx-background-color: transparent");
        }
    }

    // Show a built-in section and highlight its button
    private void showSection(AnchorPane sectionToShow, Button activeButton) {
        hideAllSections();
        resetAllButtons();
        sectionToShow.setVisible(true);
        backButton.setVisible(false);
        activeButton.setStyle("-fx-background-color: linear-gradient(to bottom right, #29AB87, #ACE1AF);");
        sidebar.setVisible(true); // Show sidebar for non-friend sections
    }

    @FXML
    private void backToDashboard(ActionEvent event) {
        showSection(home_form, home_btn); // Replace with your home section and button
        backButton.setVisible(false); // Hide the back button
    }

    // Show a friend page and highlight its button
    private void showFriendPage(Button button, String fxmlFile) {
        Pane page = friendPages.get(button); // Use Pane instead of AnchorPane
        if (page == null) {
            try {
                FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/" + fxmlFile));
                page = loader.load();
                // Add to centerContent and anchor it to fill the space
                centerContent.getChildren().add(page);
                AnchorPane.setTopAnchor(page, 0.0);
                AnchorPane.setBottomAnchor(page, 0.0);
                AnchorPane.setLeftAnchor(page, 0.0);
                AnchorPane.setRightAnchor(page, 0.0);
                friendPages.put(String.valueOf(button), page); // Store the loaded page
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
        hideAllSections();
        resetAllButtons();
        page.setVisible(true);
        backButton.setVisible(true);
        button.setStyle("-fx-background-color: linear-gradient(to bottom right, #29AB87, #ACE1AF);");
        sidebar.setVisible(false); // Hide sidebar for friend pages
    }

    // Handle button clicks for sidebar and friend buttons
    @FXML
    public void switchFormOnAction(ActionEvent event) throws IOException, SQLException {
        Object source = event.getSource();

        // Sidebar buttons
        if (source == home_btn) {
            showSection(home_form, home_btn);
            homeDisplayTotalCitoyen();
            homeDisplayTotalEmployee();
        } else if (source == addEmployee_btn) {
            showSection(addEmployee_form, addEmployee_btn);
            addEmployeeShowListData();
        } else if (source == generate_rapport) {
            showSection(rapportDisplay, generate_rapport);
        } else if (source == Map_view) {
            showSection(MapsDisplay, Map_view);
            // Add your map.flyToLocation() call here if applicable
        }
        // Friend buttons
        else if (source == CentrePage) {
            showFriendPage(CentrePage, "Afficher_Centre.fxml");
        } else if (source == AvisPage) {
            showFriendPage(AvisPage, "friend2.fxml");
        }
    }

    public void logout_btn_onAction() {
        try {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Message");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to logout?");
            Optional<ButtonType> option = alert.showAndWait();
            if (option.get().equals(ButtonType.OK)) {
                logout_btn.getScene().getWindow().hide();
                Parent root = FXMLLoader.load(Main.class.getResource("views/mainLoginSignUp.fxml"));
                Stage stage = new Stage();
                Scene scene = new Scene(root);
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

    public void homeDisplayTotalEmployee () throws SQLException {
        int countEnrolled =  er.getEmployeeCount();

        home_totalEmployee.setText(String.valueOf(countEnrolled));

    }

    public void homeDisplayTotalCitoyen () throws SQLException {
        int countCitoyen = cr.getCitoyenCount();
        home_totalCitoyen.setText(String.valueOf(countCitoyen));


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


    @FXML
    private void handleEmployeeCardClick(MouseEvent event) {
        // Load the detailed view for employees
        loadUserTableView("EMPLOYEE");
    }

    @FXML
    private void handleCitoyenCardClick(MouseEvent event) {
        // Load the detailed view for citoyens
        loadUserTableView("CITOYEN");
    }

    /**
     * Loads the detailed table view into the contentArea pane.
     *
     * @param role The role to filter users by ("EMPLOYEE" or "CITOYEN").
     */
    private void loadUserTableView(String role) {
        try {
            // Load the FXML file for the user table view.
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/UserTableView.fxml"));
            Parent root = loader.load();

            // Get the controller of the loaded FXML and pass the role to it.
            UserTableController controller = loader.getController();
            controller.loadData(role);

            // Create a new stage (separate window)
            Stage tableStage = new Stage();
            tableStage.setTitle("User Details - " + role);
            tableStage.setScene(new Scene(root));

            // Optionally, make the new stage modal so it blocks interaction with the dashboard
            tableStage.initModality(Modality.APPLICATION_MODAL);

            // Show the new stage
            tableStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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

//    private MapView CreateMapView() {
//        MapView map = new MapView();
////        map.setPrefSize(500,400);
//        map.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
//        map.addLayer(new customMapLayer());
//        map.setZoom(10);
//        map.flyTo(0,point,0.1);
//        return map;
//
//    }
//    private class customMapLayer extends MapLayer {
//        private Node marker;
//        public customMapLayer(){
//            marker = new Circle(5, Color.RED);
//            getChildren().add(marker);
//        }
//
//        @Override
//        protected  void layoutLayer(){
//            Point2D point1 = getMapPoint(point.getLatitude(),point.getLongitude());
//            marker.setTranslateX(point1.getX());
//            marker.setTranslateY(point1.getY());
//        }
//    }









}