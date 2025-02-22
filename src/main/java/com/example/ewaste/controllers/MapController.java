package com.example.ewaste.controllers;

import javafx.application.Platform;
import javafx.scene.control.TextField;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.concurrent.Worker;
import netscape.javascript.JSObject;

public class MapController {

    private WebView mapView;
    private WebEngine webEngine;
    private TextField latitudeField;
    private TextField longitudeField;

    public MapController(WebView mapView, TextField latitudeField, TextField longitudeField) {
        this.mapView = mapView;
        this.latitudeField = latitudeField;
        this.longitudeField = longitudeField;
        this.webEngine = mapView.getEngine();

        loadMap();
    }

    private void loadMap() {
        String mapHtml = """
            <html>
            <head>
                <meta charset='utf-8' />
                <meta name='viewport' content='width=device-width, initial-scale=1.0'>
                <link rel='stylesheet' href='https://unpkg.com/leaflet/dist/leaflet.css' />
                <script src='https://unpkg.com/leaflet/dist/leaflet.js'></script>
            </head>
            <body>
                <div id='map' style='width: 100%; height: 100vh;'></div>
                <script>
                    var map = L.map('map').setView([36.7472, 10.1205], 13);
                    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                        attribution: '© OpenStreetMap contributors'
                    }).addTo(map);

                    var marker = L.marker([36.7472, 10.1205]).addTo(map);

                    map.on('click', function(e) {
                        var lat = e.latlng.lat.toFixed(6);
                        var lng = e.latlng.lng.toFixed(6);
                        marker.setLatLng([lat, lng]);

                        // Envoyer les coordonnées à JavaFX
                        window.javaApp.setCoordinates(lat, lng);
                    });

                    function updateLocation(lat, lng) {
                        map.setView([lat, lng], 13);
                        marker.setLatLng([lat, lng]);
                    }
                </script>
            </body>
            </html>
        """;

        webEngine.loadContent(mapHtml);

        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                JSObject window = (JSObject) webEngine.executeScript("window");
                window.setMember("javaApp", this);
            }
        });
    }

    public void setCoordinates(double lat, double lng) {
        Platform.runLater(() -> {
            latitudeField.setText(String.valueOf(lat));
            longitudeField.setText(String.valueOf(lng));
        });
    }

    public void setLocation(double lat, double lng) {
        if (webEngine != null) {
            String script = "updateLocation(" + lat + ", " + lng + ")";
            webEngine.executeScript(script);
        }
    }
}
