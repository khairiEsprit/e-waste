module org.example.ewaste.pihoussem {
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;

    requires java.sql;
    requires java.net.http;
    requires javafx.fxml;
    requires java.mail;

    opens com.example.ewaste.contollers to javafx.fxml;
    exports com.example.ewaste.contollers;
    opens com.example.ewaste.entities to javafx.base;
    exports com.example.ewaste.entities;
    opens com.example.ewaste to javafx.fxml;
    exports com.example.ewaste;
}