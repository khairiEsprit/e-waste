package com.example.ewaste;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * A simplified version of the E-Waste Management System
 * This version doesn't rely on external dependencies and should work with basic JavaFX
 */
public class SimplifiedMain extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Create the main container
            BorderPane root = new BorderPane();
            root.setPadding(new Insets(20));
            root.setStyle("-fx-background-color: #f5f5f5;");

            // Create header
            Label headerLabel = new Label("E-Waste Management System");
            headerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
            headerLabel.setTextFill(Color.web("#0C162C"));
            
            HBox header = new HBox(headerLabel);
            header.setAlignment(Pos.CENTER);
            header.setPadding(new Insets(0, 0, 20, 0));
            root.setTop(header);

            // Create tabs
            TabPane tabPane = new TabPane();
            tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
            
            // Events tab
            Tab eventsTab = new Tab("Events");
            eventsTab.setContent(createEventsTab());
            
            // Reviews tab
            Tab reviewsTab = new Tab("Reviews");
            reviewsTab.setContent(createReviewsTab());
            
            // Waste Bins tab
            Tab binsTab = new Tab("Waste Bins");
            binsTab.setContent(createBinsTab());
            
            tabPane.getTabs().addAll(eventsTab, reviewsTab, binsTab);
            root.setCenter(tabPane);

            // Create footer
            Label footerLabel = new Label("© 2025 E-Waste Management System");
            footerLabel.setTextFill(Color.web("#666666"));
            
            HBox footer = new HBox(footerLabel);
            footer.setAlignment(Pos.CENTER);
            footer.setPadding(new Insets(20, 0, 0, 0));
            root.setBottom(footer);

            // Create scene and show stage
            Scene scene = new Scene(root, 800, 600);
            primaryStage.setTitle("E-Waste Management System");
            primaryStage.setScene(scene);
            primaryStage.show();
            
        } catch (Exception e) {
            showErrorDialog(e);
        }
    }

    private VBox createEventsTab() {
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        
        Label titleLabel = new Label("Upcoming Events");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        
        // Create a sample event card
        VBox eventCard = createEventCard(
            "Community Cleanup Day", 
            "Join us for a day of cleaning up the local park and recycling waste properly.",
            "June 15, 2025",
            "Central Park"
        );
        
        // Create another sample event card
        VBox eventCard2 = createEventCard(
            "E-Waste Collection Drive", 
            "Bring your old electronics for proper recycling and disposal.",
            "July 10, 2025",
            "City Hall"
        );
        
        Button addEventButton = new Button("Add New Event");
        addEventButton.setOnAction(e -> showInfoDialog("Add Event", "This feature would allow adding new events."));
        
        content.getChildren().addAll(titleLabel, eventCard, eventCard2, addEventButton);
        return content;
    }
    
    private VBox createReviewsTab() {
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        
        Label titleLabel = new Label("Community Reviews");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        
        // Create sample reviews
        VBox review1 = createReviewCard("John Doe", "Great initiative! The collection drive was well organized.", 5);
        VBox review2 = createReviewCard("Jane Smith", "The app is easy to use but could use more features.", 4);
        
        // Create review form
        GridPane reviewForm = new GridPane();
        reviewForm.setHgap(10);
        reviewForm.setVgap(10);
        reviewForm.setPadding(new Insets(20));
        reviewForm.setStyle("-fx-background-color: #e8e8e8; -fx-background-radius: 5;");
        
        reviewForm.add(new Label("Your Name:"), 0, 0);
        reviewForm.add(new TextField(), 1, 0);
        
        reviewForm.add(new Label("Your Review:"), 0, 1);
        TextArea reviewText = new TextArea();
        reviewText.setPrefRowCount(3);
        reviewForm.add(reviewText, 1, 1);
        
        reviewForm.add(new Label("Rating:"), 0, 2);
        ComboBox<Integer> ratingCombo = new ComboBox<>();
        ratingCombo.getItems().addAll(1, 2, 3, 4, 5);
        ratingCombo.setValue(5);
        reviewForm.add(ratingCombo, 1, 2);
        
        Button submitButton = new Button("Submit Review");
        submitButton.setOnAction(e -> showInfoDialog("Submit Review", "This would submit your review to the system."));
        reviewForm.add(submitButton, 1, 3);
        
        content.getChildren().addAll(titleLabel, review1, review2, new Separator(), reviewForm);
        return content;
    }
    
    private VBox createBinsTab() {
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        
        Label titleLabel = new Label("Waste Bin Status");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        
        // Create a table for waste bins
        TableView<WasteBin> table = new TableView<>();
        
        TableColumn<WasteBin, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty());
        
        TableColumn<WasteBin, String> locationColumn = new TableColumn<>("Location");
        locationColumn.setCellValueFactory(cellData -> cellData.getValue().locationProperty());
        
        TableColumn<WasteBin, String> fillLevelColumn = new TableColumn<>("Fill Level");
        fillLevelColumn.setCellValueFactory(cellData -> cellData.getValue().fillLevelProperty());
        
        TableColumn<WasteBin, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
        
        table.getColumns().addAll(idColumn, locationColumn, fillLevelColumn, statusColumn);
        
        // Add sample data
        table.getItems().add(new WasteBin("1", "Ariana, Tunisia", "75%", "Active"));
        table.getItems().add(new WasteBin("2", "Tunis, Tunisia", "45%", "Active"));
        table.getItems().add(new WasteBin("3", "Sousse, Tunisia", "90%", "Full"));
        
        Button refreshButton = new Button("Refresh Data");
        refreshButton.setOnAction(e -> showInfoDialog("Refresh", "This would refresh the waste bin data."));
        
        content.getChildren().addAll(titleLabel, table, refreshButton);
        return content;
    }
    
    private VBox createEventCard(String title, String description, String date, String location) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: white; -fx-border-color: #dddddd; -fx-border-radius: 5; -fx-background-radius: 5;");
        
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        
        Label descLabel = new Label(description);
        descLabel.setWrapText(true);
        
        Label dateLabel = new Label("Date: " + date);
        Label locationLabel = new Label("Location: " + location);
        
        Button participateButton = new Button("Participate");
        participateButton.setOnAction(e -> showInfoDialog("Participate", "You would register for this event: " + title));
        
        card.getChildren().addAll(titleLabel, descLabel, dateLabel, locationLabel, participateButton);
        return card;
    }
    
    private VBox createReviewCard(String name, String comment, int rating) {
        VBox card = new VBox(5);
        card.setPadding(new Insets(10));
        card.setStyle("-fx-background-color: white; -fx-border-color: #dddddd; -fx-border-radius: 5; -fx-background-radius: 5;");
        
        Label nameLabel = new Label(name);
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        
        Label commentLabel = new Label(comment);
        commentLabel.setWrapText(true);
        
        // Create star rating
        HBox stars = new HBox(2);
        for (int i = 0; i < 5; i++) {
            Label star = new Label("★");
            star.setTextFill(i < rating ? Color.GOLD : Color.LIGHTGRAY);
            stars.getChildren().add(star);
        }
        
        card.getChildren().addAll(nameLabel, stars, commentLabel);
        return card;
    }
    
    private void showErrorDialog(Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("An error occurred");
        alert.setContentText(e.getMessage());
        
        TextArea textArea = new TextArea();
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : e.getStackTrace()) {
            sb.append(element.toString()).append("\n");
        }
        textArea.setText(sb.toString());
        
        alert.getDialogPane().setExpandableContent(textArea);
        alert.showAndWait();
    }
    
    private void showInfoDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    // Simple class to represent a waste bin for the table
    public static class WasteBin {
        private final javafx.beans.property.SimpleStringProperty id;
        private final javafx.beans.property.SimpleStringProperty location;
        private final javafx.beans.property.SimpleStringProperty fillLevel;
        private final javafx.beans.property.SimpleStringProperty status;
        
        public WasteBin(String id, String location, String fillLevel, String status) {
            this.id = new javafx.beans.property.SimpleStringProperty(id);
            this.location = new javafx.beans.property.SimpleStringProperty(location);
            this.fillLevel = new javafx.beans.property.SimpleStringProperty(fillLevel);
            this.status = new javafx.beans.property.SimpleStringProperty(status);
        }
        
        public javafx.beans.property.SimpleStringProperty idProperty() { return id; }
        public javafx.beans.property.SimpleStringProperty locationProperty() { return location; }
        public javafx.beans.property.SimpleStringProperty fillLevelProperty() { return fillLevel; }
        public javafx.beans.property.SimpleStringProperty statusProperty() { return status; }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
