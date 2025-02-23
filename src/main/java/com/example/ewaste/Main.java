package com.example.ewaste;

import com.teamdev.jxbrowser.engine.Engine;
import com.teamdev.jxbrowser.engine.EngineOptions;
import com.teamdev.jxbrowser.view.javafx.BrowserView;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

import static com.teamdev.jxbrowser.engine.RenderingMode.HARDWARE_ACCELERATED;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
//
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("views/Dashboard.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}


//
//public class Main extends Application {
//
//    @Override
//    public void start(Stage stage) {
//        // Create a WebView and load the local HTML file
//        WebView webView = new WebView();
//        String url = getClass().getResource("views/maps.html").toExternalForm();
//        webView.getEngine().load(url);
//
//        // Create and set the scene
//        Scene scene = new Scene(webView, 800, 600);
//        stage.setScene(scene);
//        stage.setTitle("Mapbox in JavaFX");
//        stage.show();
//    }
//
//    public static void main(String[] args) {
//        launch(args);
//    }
//}

//public final class Main extends Application {
//
//    @Override
//    public void start(Stage primaryStage) {
//        // Initialize Chromium with hardware acceleration and add your license key here.
//        EngineOptions options = EngineOptions.newBuilder(HARDWARE_ACCELERATED)
//                .licenseKey("") // Replace with your actual license key.
//                .build();
//        Engine engine = Engine.newInstance(options);
//
//        // Create a Browser instance.
//        var browser = engine.newBrowser();
//
//        // The HTML content with your Mapbox GL JS map.
//        String htmlContent = "<!DOCTYPE html>\n" +
//                "<html>\n" +
//                "<head>\n" +
//                "    <meta charset=\"utf-8\">\n" +
//                "    <title>Mapbox GL JS map</title>\n" +
//                "    <meta name=\"viewport\" content=\"initial-scale=1,maximum-scale=1,user-scalable=no\">\n" +
//                "    <link href=\"https://api.mapbox.com/mapbox-gl-js/v3.10.0/mapbox-gl.css\" rel=\"stylesheet\">\n" +
//                "    <script src=\"https://api.mapbox.com/mapbox-gl-js/v3.10.0/mapbox-gl.js\"></script>\n" +
//                "    <style>\n" +
//                "        body { margin: 0; padding: 0; }\n" +
//                "        #map { position: absolute; top: 0; bottom: 0; width: 100%; }\n" +
//                "    </style>\n" +
//                "</head>\n" +
//                "<body>\n" +
//                "    <div id=\"map\"></div>\n" +
//                "    <script>\n" +
//                "        // TO MAKE THE MAP APPEAR YOU MUST\n" +
//                "        // ADD YOUR ACCESS TOKEN FROM\n" +
//                "        // https://account.mapbox.com\n" +
//                "        mapboxgl.accessToken = '';\n" +
//                "        const map = new mapboxgl.Map({\n" +
//                "            container: 'map', // container ID\n" +
//                "            center: [-74.5, 40], // starting position [lng, lat]\n" +
//                "            zoom: 9 // starting zoom\n" +
//                "        });\n" +
//                "    </script>\n" +
//                "</body>\n" +
//                "</html>";
//
//        // Load the HTML content into the main frame.
//        browser.mainFrame().ifPresent(frame -> frame.loadHtml(htmlContent));
//
//        // Create and embed the JavaFX BrowserView component.
//        var view = BrowserView.newInstance(browser);
//
//        var scene = new Scene(new BorderPane(view), 1280, 700);
//        primaryStage.setTitle("JxBrowser JavaFX - Mapbox");
//        primaryStage.setScene(scene);
//        primaryStage.show();
//
//        // Shutdown Chromium and release allocated resources when the stage is closed.
//        primaryStage.setOnCloseRequest(event -> engine.close());
//    }
//
//    public static void main(String[] args) {
//        launch(args);
//    }
//}