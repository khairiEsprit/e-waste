module com.example.ewaste {
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;
//    requires eu.hansolo.tilesfx;
    requires java.net.http;
    requires com.google.gson;
    requires io.github.cdimascio.dotenv.java;
    requires MaterialFX;
    requires java.mail;
    requires com.gluonhq.maps;
    requires jxbrowser;
    requires jxbrowser.javafx;
    requires org.json;
    requires jdk.compiler;
    requires com.fasterxml.jackson.databind;
    requires java.sql;
    requires nanohttpd;
    requires de.jensd.fx.glyphs.fontawesome;
    opens com.example.ewaste.Controllers to javafx.fxml;
    exports com.example.ewaste.Controllers;
    opens com.example.ewaste.Entities to javafx.base;
    exports com.example.ewaste.Entities;
    opens com.example.ewaste to javafx.fxml;
    exports com.example.ewaste;

}