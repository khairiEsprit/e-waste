module com.example.ewaste {
    requires javafx.web;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.net.http;
    requires java.sql;
    requires jdk.jfr;
    requires com.google.gson;
    requires org.json;

    // Ouvrir les packages nécessaires à JavaFX
    opens com.example.ewaste to javafx.fxml;
    opens com.example.ewaste.Controllers to javafx.fxml;
    opens com.example.ewaste.Entities to javafx.base, javafx.fxml;

    // Exporter les packages nécessaires
    exports com.example.ewaste;
    exports com.example.ewaste.Controllers;
    exports com.example.ewaste.Entities;
    exports com.example.ewaste.Utils;
}