module org.example.ewaste.pihoussem {
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;

    requires java.net.http;
    requires javafx.fxml;
    requires java.mail;
    requires itextpdf;
    requires org.json;
    requires okhttp3;
    requires com.fasterxml.jackson.databind;
    requires java.sql;

    opens com.example.ewaste.Controllers to javafx.fxml;
    exports com.example.ewaste.Controllers;
    opens com.example.ewaste.Entities to javafx.base;
    exports com.example.ewaste.Entities;
    opens com.example.ewaste to javafx.fxml;
    exports com.example.ewaste;
}