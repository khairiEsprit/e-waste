package com.example.ewaste.Controllers;

import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.scene.Node;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * A MapBox implementation using JavaFX WebView instead of JxBrowser.
 * This is a free alternative that doesn't require a license.
 */
public class WebViewMapBox {
    private WebView webView;
    private WebEngine webEngine;
    private AtomicBoolean mapInitialized = new AtomicBoolean(false);
    private List<Runnable> pendingOperations = new java.util.ArrayList<>();

    // MapBox access token - you can use your own token here
    private final String mapBoxKey = "pk.eyJ1IjoiYWhtZWRrYWFuaWNoZSIsImEiOiJjbHRxcnRtcGUwMGJqMmtvNXJyZWJxZWJsIn0.Vc_XLz-j-4k_-FUoLO_Lrw";

    public WebViewMapBox() {
        // Create a WebView instance
        webView = new WebView();
        webEngine = webView.getEngine();

        // Use a simpler map implementation with Leaflet instead of MapBox GL JS
        // Leaflet doesn't require WebGL and works better in WebView
        String htmlContent = createLeafletHtml();
        webEngine.loadContent(htmlContent);

        // Wait for the page to load before executing JavaScript
        webEngine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Worker.State.SUCCEEDED) {
                try {
                    // Make the Java object available to JavaScript
                    JSObject window = (JSObject) webEngine.executeScript("window");
                    window.setMember("javaConnector", this);

                    // Mark the map as initialized
                    mapInitialized.set(true);

                    // Execute any pending operations
                    for (Runnable operation : pendingOperations) {
                        operation.run();
                    }
                    pendingOperations.clear();
                } catch (Exception e) {
                    System.err.println("Error initializing map: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Creates the HTML content with Leaflet map (more compatible with WebView than MapBox GL JS)
     */
    private String createLeafletHtml() {
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta charset=\"utf-8\">\n" +
                "    <title>Leaflet Map</title>\n" +
                "    <meta name=\"viewport\" content=\"initial-scale=1,maximum-scale=1,user-scalable=no\">\n" +
                "    <link rel=\"stylesheet\" href=\"https://unpkg.com/leaflet@1.9.4/dist/leaflet.css\" />\n" +
                "    <script src=\"https://unpkg.com/leaflet@1.9.4/dist/leaflet.js\"></script>\n" +
                "    <style>\n" +
                "        body { margin: 0; padding: 0; }\n" +
                "        #map { position: absolute; top: 0; bottom: 0; width: 100%; height: 100%; }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div id=\"map\"></div>\n" +
                "    <script>\n" +
                "        // Initialize the map\n" +
                "        var map = L.map('map').setView([40, -74.5], 9);\n" +
                "        \n" +
                "        // Add the OpenStreetMap tile layer\n" +
                "        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {\n" +
                "            attribution: '&copy; <a href=\"https://www.openstreetmap.org/copyright\">OpenStreetMap</a> contributors'\n" +
                "        }).addTo(map);\n" +
                "        \n" +
                "        // Store markers and routes for later reference\n" +
                "        var markers = [];\n" +
                "        var routeLine = null;\n" +
                "        \n" +
                "        // Function to add a marker\n" +
                "        function addMarker(latitude, longitude, color, draggable, popupText) {\n" +
                "            try {\n" +
                "                console.log('Adding marker at', latitude, longitude);\n" +
                "                \n" +
                "                // Create a custom icon with the specified color\n" +
                "                var markerIcon = L.divIcon({\n" +
                "                    className: 'custom-marker',\n" +
                "                    html: `<div style=\"background-color: ${color}; width: 20px; height: 20px; border-radius: 50%; border: 2px solid white;\"></div>`,\n" +
                "                    iconSize: [24, 24],\n" +
                "                    iconAnchor: [12, 12]\n" +
                "                });\n" +
                "                \n" +
                "                // Create the marker\n" +
                "                var marker = L.marker([latitude, longitude], {\n" +
                "                    icon: markerIcon,\n" +
                "                    draggable: draggable\n" +
                "                }).addTo(map);\n" +
                "                \n" +
                "                // Add popup if text is provided\n" +
                "                if (popupText) {\n" +
                "                    marker.bindPopup(popupText);\n" +
                "                }\n" +
                "                \n" +
                "                // Store the marker for later reference\n" +
                "                markers.push(marker);\n" +
                "                \n" +
                "                return true;\n" +
                "            } catch (error) {\n" +
                "                console.error('Error adding marker:', error);\n" +
                "                return false;\n" +
                "            }\n" +
                "        }\n" +
                "        \n" +
                "        // Function to fly to a location\n" +
                "        function flyTo(latitude, longitude, zoom) {\n" +
                "            try {\n" +
                "                console.log('Flying to', latitude, longitude, 'with zoom', zoom);\n" +
                "                map.setView([latitude, longitude], zoom, {\n" +
                "                    animate: true,\n" +
                "                    duration: 1.5\n" +
                "                });\n" +
                "                return true;\n" +
                "            } catch (error) {\n" +
                "                console.error('Error flying to location:', error);\n" +
                "                return false;\n" +
                "            }\n" +
                "        }\n" +
                "        \n" +
                "        // Function to draw a route between waypoints\n" +
                "        function drawRoute(waypoints, mapboxKey) {\n" +
                "            try {\n" +
                "                console.log('Drawing route with waypoints:', waypoints);\n" +
                "                \n" +
                "                // Remove existing route if any\n" +
                "                if (routeLine) {\n" +
                "                    map.removeLayer(routeLine);\n" +
                "                }\n" +
                "                \n" +
                "                // Convert waypoints from strings to coordinates\n" +
                "                var coordinates = [];\n" +
                "                for (var i = 0; i < waypoints.length; i++) {\n" +
                "                    var parts = waypoints[i].split(',');\n" +
                "                    if (parts.length === 2) {\n" +
                "                        // Note: Leaflet uses [lat, lng] format, opposite of MapBox\n" +
                "                        coordinates.push([parseFloat(parts[1]), parseFloat(parts[0])]);\n" +
                "                    }\n" +
                "                }\n" +
                "                \n" +
                "                // Draw a simple line between the points\n" +
                "                routeLine = L.polyline(coordinates, {\n" +
                "                    color: 'red',\n" +
                "                    weight: 5,\n" +
                "                    opacity: 0.7\n" +
                "                }).addTo(map);\n" +
                "                \n" +
                "                // Fit the map to show the entire route\n" +
                "                map.fitBounds(routeLine.getBounds(), {\n" +
                "                    padding: [50, 50]\n" +
                "                });\n" +
                "                \n" +
                "                return true;\n" +
                "            } catch (error) {\n" +
                "                console.error('Error drawing route:', error);\n" +
                "                return false;\n" +
                "            }\n" +
                "        }\n" +
                "    </script>\n" +
                "</body>\n" +
                "</html>";
    }

    /**
     * Execute a JavaScript function when the map is ready
     */
    private void executeWhenReady(Runnable action) {
        if (mapInitialized.get()) {
            action.run();
        } else {
            pendingOperations.add(action);
        }
    }

    /**
     * Add a marker to the map
     */
    public void addCustomMarker(double latitude, double longitude, String color, boolean draggable, String popupText) {
        executeWhenReady(() -> {
            Platform.runLater(() -> {
                try {
                    // Escape single quotes in the popup text
                    String escapedPopupText = popupText.replace("'", "\\'");

                    // Execute the JavaScript function
                    webEngine.executeScript(
                            String.format("addMarker(%f, %f, '%s', %b, '%s')",
                                    latitude, longitude, color, draggable, escapedPopupText));
                } catch (Exception e) {
                    System.err.println("Error adding marker: " + e.getMessage());
                }
            });
        });
    }

    /**
     * Fly to a specific location on the map
     */
    public void flyToLocation(double latitude, double longitude, double zoom) {
        executeWhenReady(() -> {
            Platform.runLater(() -> {
                try {
                    webEngine.executeScript(
                            String.format("flyTo(%f, %f, %f)", latitude, longitude, zoom));
                } catch (Exception e) {
                    System.err.println("Error flying to location: " + e.getMessage());
                }
            });
        });
    }

    /**
     * Draw a route on the map
     */
    public void drawRoute(List<String> waypoints) {
        if (waypoints == null || waypoints.size() < 2) {
            System.out.println("Error: At least a start and end location are required.");
            return;
        }

        executeWhenReady(() -> {
            Platform.runLater(() -> {
                try {
                    // Convert waypoints list into a JavaScript-friendly string
                    String waypointsJsArray = waypoints.stream()
                            .map(wp -> "\"" + wp + "\"") // Ensure each waypoint is quoted
                            .collect(Collectors.joining(",", "[", "]"));

                    // JavaScript call with correctly formatted waypoints
                    String js = String.format("drawRoute(%s, '%s')", waypointsJsArray, mapBoxKey);

                    // Execute JavaScript in the browser view
                    webEngine.executeScript(js);
                } catch (Exception e) {
                    System.err.println("Error drawing route: " + e.getMessage());
                }
            });
        });
    }

    /**
     * Get the WebView as a JavaFX Node
     */
    public Node getView() {
        return webView;
    }
}
