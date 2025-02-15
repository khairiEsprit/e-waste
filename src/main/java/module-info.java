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
    requires java.sql;
    requires java.management;
    opens com.example.ewaste.controllers to javafx.fxml;
    exports com.example.ewaste.controllers;

    opens com.example.ewaste to javafx.fxml;
    opens com.example.ewaste.entities to javafx.base;
    exports com.example.ewaste;
}