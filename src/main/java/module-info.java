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
    requires javafx.swing;
    requires jdk.jsobject;
    requires org.json;
    requires libphonenumber;
    requires mysql.connector.j;
    requires jakarta.mail;
    requires itextpdf;
    opens com.example.ewaste.Controllers to javafx.fxml;
    exports com.example.ewaste.Controllers;
    opens com.example.ewaste.Entities to javafx.base;

    opens com.example.ewaste to javafx.fxml;
    exports com.example.ewaste;
    exports com.example.ewaste.Repository;
    opens com.example.ewaste.Repository to javafx.fxml;
}