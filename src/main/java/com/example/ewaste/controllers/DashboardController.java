package com.example.ewaste.controllers;

import com.example.ewaste.Main;
import com.example.ewaste.entities.GetData;
import com.example.ewaste.entities.Poubelle;
import com.example.ewaste.repository.PoubelleRepository;
import com.example.ewaste.utils.DataBase;
import com.example.ewaste.entities.User;
import com.example.ewaste.repository.AuthRepository;
import com.example.ewaste.repository.CitoyenRepository;
import com.example.ewaste.repository.EmployeeRepository;
import com.example.ewaste.utils.OpenAiApi;
import com.gluonhq.maps.MapLayer;
import com.gluonhq.maps.MapPoint;
import com.gluonhq.maps.MapView;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;
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
    private final MapPoint point = new MapPoint(48.85,2.29);




    CitoyenRepository cr = new CitoyenRepository();
    EmployeeRepository er = new EmployeeRepository();



    public void close_btn_OnAction() {
        System.exit(0);
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
        addEmployee_statusList();
//        setAddStudent_courseList();
        addEmployee_search_onKeyTyped();
        MapView map = CreateMapView();
        address.getChildren().add(map);
        VBox.setVgrow(map, Priority.ALWAYS);
        System.out.println("MapView added to address container: " + (map != null));

    }

    //  maps code :!!!!!!!

     private static final int CENTER_ID = 1; // Change dynamically if needed
     PoubelleRepository pr = new PoubelleRepository();

    private static final int FULL_THRESHOLD = 70;

    private MapView CreateMapView() {
        MapView map = new MapView();
        map.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        map.setZoom(18);

        // Set your center point (e.g., collection center)
        MapPoint centerLocation = getCenterLocation(CENTER_ID);
        map.flyTo(0, centerLocation, 0.1);

        // Add a marker for the center (without a route number)
//        map.addLayer(new CustomMapLayer(centerLocation, "Collection Center", Color.BLUE, null));
        map.addLayer(new CenterMapLayer(centerLocation, "Center of Ariana"));

        // Fetch poubelle bins for this center
        List<Poubelle> poubelleBins = pr.getPoubellesByCenter(CENTER_ID);

        // First, compute the route ordering:
        // We want: center -> first full trash bin -> then all low filled bins
        Map<Poubelle, Integer> routePriority = new HashMap<>();
        Poubelle firstFullBin = null;
        List<Poubelle> lowBins = new ArrayList<>();

        for (Poubelle poubelle : poubelleBins) {
            if (poubelle.getFillLevel() >= FULL_THRESHOLD) {
                // Pick the first bin that is full
                if (firstFullBin == null) {
                    firstFullBin = poubelle;
                }
            } else {
                lowBins.add(poubelle);
            }
        }

        int prio = 1;
        if (firstFullBin != null) {
            routePriority.put(firstFullBin, prio++);
        }
        for (Poubelle bin : lowBins) {
            routePriority.put(bin, prio++);
        }

        // Add markers for trash bins.
        // If the trash bin is part of the route, pass its route priority so that it appears inside the circle.
        for (Poubelle poubelle : poubelleBins) {
            MapPoint poubelleLocation = new MapPoint(poubelle.getLatitude(), poubelle.getLongitude());
            Color fillColor = getFillColor(poubelle.getFillLevel());
            String tooltipText = "Trash Bin\nFill Level: " + poubelle.getFillLevel() + "%\nStatus: "
                    + (poubelle.isWorking() ? "Working" : "Broken");

            // Get the route priority if available; otherwise, null.
            Integer routeNum = routePriority.get(poubelle);
            map.addLayer(new CustomMapLayer(poubelleLocation, tooltipText, fillColor, routeNum));
        }

        // Build the route polyline:
        List<MapPoint> routePoints = new ArrayList<>();
        routePoints.add(centerLocation);
        if (firstFullBin != null) {
            routePoints.add(new MapPoint(firstFullBin.getLatitude(), firstFullBin.getLongitude()));
        }
        for (Poubelle bin : lowBins) {
            routePoints.add(new MapPoint(bin.getLatitude(), bin.getLongitude()));
        }

        // Add the polyline route to the map.
        map.addLayer(new CustomPolylineLayer(routePoints, Color.RED));

        return map;
    }

    private class CustomMapLayer extends MapLayer {
        private final MapPoint location;
        private final Node marker;

        /**
         * @param location    Geographic location of the marker.
         * @param tooltipText Text for the tooltip.
         * @param markerColor Color of the circle.
         * @param priority    Optional route priority number to display inside the circle (or null).
         */
        public CustomMapLayer(MapPoint location, String tooltipText, Color markerColor, Integer priority) {
            this.location = location;
            // Create the circle for the marker.
            Circle circle = new Circle(10, markerColor);

            // Use a StackPane so that we can layer the circle and an optional number.
            StackPane markerPane = new StackPane();
            markerPane.getChildren().add(circle);

            // If a priority number is provided, add it as a label.
            if (priority != null) {
                Label label = new Label(priority.toString());
                label.setTextFill(Color.WHITE);
                // Optionally, style the label font size or weight:
                label.setStyle("-fx-font-weight: bold;");
                markerPane.getChildren().add(label);
            }

            // Install a tooltip on the marker.
            Tooltip.install(markerPane, new Tooltip(tooltipText));
            marker = markerPane;
            getChildren().add(marker);
        }

        @Override
        protected void layoutLayer() {
            // Convert geographic coordinates to screen coordinates.
            Point2D screenPoint = getMapPoint(location.getLatitude(), location.getLongitude());
            marker.setTranslateX(screenPoint.getX());
            marker.setTranslateY(screenPoint.getY());
        }
    }


    // Custom layer for the center marker with a different shape and a fixed label.
    private class CenterMapLayer extends MapLayer {
        private final MapPoint location;
        private final Node marker;

        /**
         * @param location  The geographic location for the center.
         * @param labelText The text to display (e.g., "Center of Ariana").
         */
        public CenterMapLayer(MapPoint location, String labelText) {
            this.location = location;

            // Create a rectangle as the marker shape.
            Rectangle rectangle = new Rectangle(120, 40);
            rectangle.setFill(Color.DARKBLUE);
            rectangle.setArcWidth(10);
            rectangle.setArcHeight(10);

            // Create a label to display on the marker.
            Label label = new Label(labelText);
            label.setTextFill(Color.WHITE);
            label.setStyle("-fx-font-weight: bold; -fx-font-size: 10;");
            label.setWrapText(true);
            label.setAlignment(Pos.CENTER);

            // Use a StackPane to overlay the label on the rectangle.
            StackPane markerPane = new StackPane();
            markerPane.getChildren().addAll(rectangle, label);

            marker = markerPane;
            getChildren().add(marker);
        }

        @Override
        protected void layoutLayer() {
            // Convert geographic coordinates to screen coordinates.
            Point2D screenPoint = getMapPoint(location.getLatitude(), location.getLongitude());
            marker.setTranslateX(screenPoint.getX());
            marker.setTranslateY(screenPoint.getY());
        }
    }




    private class CustomPolylineLayer extends MapLayer {
        private final List<MapPoint> points;
        private final Polyline polyline;

        public CustomPolylineLayer(List<MapPoint> points, Color lineColor) {
            this.points = points;
            polyline = new Polyline();
            polyline.setStroke(lineColor);
            polyline.setStrokeWidth(2);
            getChildren().add(polyline);
        }

        @Override
        protected void layoutLayer() {
            ObservableList<Double> coords = polyline.getPoints();
            coords.clear();
            for (MapPoint mp : points) {
                Point2D pt = getMapPoint(mp.getLatitude(), mp.getLongitude());
                coords.add(pt.getX());
                coords.add(pt.getY());
            }
        }
    }

    private MapPoint getCenterLocation(int centerId) {
        // Hardcoded for now, but you can fetch from DB
        return new MapPoint(36.8022, 10.1811);
    }



    private Color getFillColor(int fillLevel) {
        if (fillLevel < 30) return Color.GREEN;
        if (fillLevel < 70) return Color.ORANGE;
        return Color.RED;
    }

//    public class MapLayer extends Pane {
//        // Your custom functionality here
//    }

//    private MapLayer drawRoute(MapPoint start, MapPoint end) {
//        MapLayer routeLayer = new MapLayer();
//
//        Line route = new Line();
//        route.setStartX(start.getLatitude());
//        route.setStartY(start.getLongitude());
//        route.setEndX(end.getLatitude());
//        route.setEndY(end.getLongitude());
//        route.setStrokeWidth(2);
//        route.setStroke(Color.DARKGRAY);
//
//        routeLayer.getChildren().add(route);
//        return routeLayer;
//    }

    // maps code !!!!!










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


    //    START CODE FOR FORM SWITCHING
    private void showSection(AnchorPane sectionToShow, Button activeButton) {
        // Hide all sections
        home_form.setVisible(false);
        addEmployee_form.setVisible(false);
        MapsDisplay.setVisible(false);
        rapportDisplay.setVisible(false);


        // Show the selected section
        sectionToShow.setVisible(true);

        // Reset button styles
        home_btn.setStyle("-fx-background-color: transparent");
        addEmployee_btn.setStyle("-fx-background-color: transparent");
        Map_view.setStyle("-fx-background-color: transparent");
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

            addEmployee_search_onKeyTyped();

        } else if (event.getSource() == generate_rapport) {
            showSection(rapportDisplay, generate_rapport);
            generateRapport_btn.setStyle("-fx-background-color: linear-gradient(to bottom right, #29AB87, #ACE1AF);");
        }else  if (event.getSource() == Map_view){
            showSection(MapsDisplay,Map_view);

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