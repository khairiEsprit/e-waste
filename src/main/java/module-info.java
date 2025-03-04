module com.example.ewaste {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;
    //requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires com.google.gson;
    requires java.net.http;
    requires java.sql;
    requires com.google.zxing;
    requires javafx.swing;
    requires twilio;
    opens com.example.ewaste.Controllers to javafx.fxml;
    exports com.example.ewaste.Controllers;

    opens com.example.ewaste to javafx.fxml;
    opens com.example.ewaste.Entities to javafx.base;
    opens com.example.ewaste.Repository to javafx.fxml;
    exports com.example.ewaste;

}