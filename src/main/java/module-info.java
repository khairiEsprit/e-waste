module com.example.ewaste {
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.sql;
    requires java.net.http;
    requires com.google.gson;
    requires io.github.cdimascio.dotenv.java;
    requires MaterialFX;
    opens com.example.ewaste.controllers to javafx.fxml;
    exports com.example.ewaste.controllers;

    opens com.example.ewaste to javafx.fxml;
    exports com.example.ewaste;
}