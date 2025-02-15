module com.example.ewaste {
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.sql;
    requires jdk.jfr;
    requires MaterialFX;

    opens com.example.ewaste to javafx.fxml;
    exports com.example.ewaste;
    exports com.example.ewaste.Controllers;
    opens com.example.ewaste.Controllers to javafx.fxml;
}