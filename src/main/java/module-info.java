module com.example.ewaste {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires jdk.jsobject;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;
    requires jakarta.mail;
    requires java.net.http;
    requires org.json;
    //requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.sql;
    requires java.management;
    opens com.example.ewaste.Controllers to javafx.fxml;
    exports com.example.ewaste.Controllers;

    opens com.example.ewaste to javafx.fxml;
    opens com.example.ewaste.Entities to javafx.base;
    exports com.example.ewaste;
}