module com.example.ewaste {
    requires javafx.controls; // Ajoutez ce module si vous utilisez des contrôles JavaFX
    requires javafx.fxml;     // Module pour FXML
    requires javafx.web;      // Module pour JavaFX Web
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.sql;        // Module pour JDBC
    requires jdk.jfr;         // Module pour Java Flight Recorder
    requires MaterialFX;      // Module pour MaterialFX

    // Ouvrir les packages nécessaires à JavaFX
    opens com.example.ewaste to javafx.fxml;
    opens com.example.ewaste.Controllers to javafx.fxml;
    opens com.example.ewaste.Entities to javafx.base, javafx.fxml; // Ajoutez cette ligne

    // Exporter les packages nécessaires
    exports com.example.ewaste;
    exports com.example.ewaste.Controllers;
    exports com.example.ewaste.Entities; // Exportez le package Entities si nécessaire
}