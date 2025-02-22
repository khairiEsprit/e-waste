package com.example.ewaste.Controllers;

import static com.teamdev.jxbrowser.engine.RenderingMode.HARDWARE_ACCELERATED;
import com.teamdev.jxbrowser.engine.Engine;
import com.teamdev.jxbrowser.engine.EngineOptions;
import com.teamdev.jxbrowser.view.javafx.BrowserView;
import io.github.cdimascio.dotenv.Dotenv;
import javafx.application.Platform;
import javafx.scene.Node;

import java.util.List;
import java.util.stream.Collectors;

public class MapBox {
    private Engine engine;
    private com.teamdev.jxbrowser.browser.Browser browser;
    private BrowserView browserView;

    Dotenv dotenv = Dotenv.configure()
            .directory("C:/Users/User/Documents/e-waste/e-waste") // Adjust the path accordingly
            .filename(".env")
            .load();

    String licence = dotenv.get("LICENCE_KEY");
    String mapBoxKey = dotenv.get("MAPBOX_KEY");

    public MapBox() {
        // Initialize the engine with hardware acceleration and your license key.
        EngineOptions options = EngineOptions.newBuilder(HARDWARE_ACCELERATED)
                .licenseKey(licence) // Replace with your actual JxBrowser license key.
                .build();
        engine = Engine.newInstance(options);

        // Create a new Browser instance.
        browser = engine.newBrowser();

        // Your Mapbox HTML content with a <base> tag for the proper base URL.
        String htmlContent = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <base href=\"http://localhost\">\n" +
                "    <meta charset=\"utf-8\">\n" +
                "    <title>Mapbox GL JS map</title>\n" +
                "    <meta name=\"viewport\" content=\"initial-scale=1,maximum-scale=1,user-scalable=no\">\n" +
                "    <link href=\"https://api.mapbox.com/mapbox-gl-js/v3.10.0/mapbox-gl.css\" rel=\"stylesheet\">\n" +
                "    <script src=\"https://api.mapbox.com/mapbox-gl-js/v3.10.0/mapbox-gl.js\"></script>\n" +
                "    <style>\n" +
                "        body { margin: 0; padding: 0; }\n" +
                "        #map { position: absolute; top: 0; bottom: 0; width: 100%; }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div id=\"map\"></div>\n" +
                "    <script>\n" +
                "        mapboxgl.accessToken = '" + mapBoxKey + "';\n" +
                "        window.map = new mapboxgl.Map({\n" +
                "            container: 'map',\n" +
                "            style: 'mapbox://styles/mapbox/streets-v12',\n" +
                "            center: [-74.5, 40],\n" +
                "            zoom: 9\n" +
                "        });\n" +
                "\n" +
                "window.drawRoute = function(waypoints, mapboxKey) {\n" +
                "    if (!waypoints || waypoints.length < 2) {\n" +
                "        console.error(\"At least two waypoints are required.\");\n" +
                "        return;\n" +
                "    }\n" +
                "\n" +
                "    // Convert waypoints array into a Mapbox API-compatible string format\n" +
                "    const coordinates = waypoints.join(\";\");\n" +
                "\n" +
                "    const url = `https://api.mapbox.com/directions/v5/mapbox/driving/${coordinates}?geometries=geojson&access_token=${mapboxKey}`;\n" +
                "\n" +
                "    fetch(url)\n" +
                "        .then(response => response.json())\n" +
                "        .then(data => {\n" +
                "            if (!data.routes || data.routes.length === 0) {\n" +
                "                console.error('No route found.');\n" +
                "                return;\n" +
                "            }\n" +
                "\n" +
                "            const route = data.routes[0].geometry;\n" +
                "\n" +
                "            if (window.map.getSource('route')) {\n" +
                "                window.map.getSource('route').setData({\n" +
                "                    type: 'Feature',\n" +
                "                    properties: {},\n" +
                "                    geometry: route\n" +
                "                });\n" +
                "            } else {\n" +
                "                window.map.addSource('route', {\n" +
                "                    type: 'geojson',\n" +
                "                    data: {\n" +
                "                        type: 'Feature',\n" +
                "                        properties: {},\n" +
                "                        geometry: route\n" +
                "                    }\n" +
                "                });\n" +
                "\n" +
                "                window.map.addLayer({\n" +
                "                    id: 'route',\n" +
                "                    type: 'line',\n" +
                "                    source: 'route',\n" +
                "                    layout: {\n" +
                "                        'line-join': 'round',\n" +
                "                        'line-cap': 'round'\n" +
                "                    },\n" +
                "                    paint: {\n" +
                "                        'line-color': '#ff0000',\n" +
                "                        'line-width': 5\n" +
                "                    }\n" +
                "                });\n" +
                "            }\n" +
                "        })\n" +
                "        .catch(error => console.error('Error fetching route:', error));\n" +
                "};\n" +
                "    </script>\n" +
                "</body>\n" +
                "</html>";


        // Load the HTML content into the browser's main frame (only one argument is allowed).
        browser.mainFrame().ifPresent(frame -> frame.loadHtml(htmlContent));

        // Create the JavaFX BrowserView component.
        browserView = BrowserView.newInstance(browser);
    }

    public void addCustomMarker(double latitude, double longitude, String color, boolean draggable, String popupText) {
        // Escape single quotes in the popup text so it doesn't break the JavaScript string.
        popupText = popupText.replace("'", "\\'");
        String js = String.format(
                "var marker = new mapboxgl.Marker({color: '%s', draggable: %b})" +
                        ".setLngLat([%f, %f])" +
                        ".addTo(window.map);" +
                        "var popup = new mapboxgl.Popup({ offset: 25 }).setText('%s');" +
                        "marker.setPopup(popup);",
                color, draggable, longitude, latitude, popupText
        );
        Platform.runLater(() ->
                browser.mainFrame().ifPresent(frame -> frame.executeJavaScript(js))
        );
    }

    public void flyToLocation(double latitude, double longitude, double zoom) {
        String js = String.format(
                "window.map.flyTo({center: [%f, %f], zoom: %f, essential: true});",
                longitude, latitude, zoom  // Note: Mapbox expects [lng, lat]
        );
        Platform.runLater(() ->
                browser.mainFrame().ifPresent(frame -> frame.executeJavaScript(js))
        );
    }

    public void drawRoute(List<String> waypoints) {
        if (waypoints == null || waypoints.size() < 2) {
            System.out.println("Error: At least a start and end location are required.");
            return;
        }

        // Convert waypoints list into a JavaScript-friendly string
        String waypointsJsArray = waypoints.stream()
                .map(wp -> "\"" + wp + "\"") // Ensure each waypoint is quoted
                .collect(Collectors.joining(",", "[", "]"));

        // JavaScript call with correctly formatted waypoints
        String js = String.format("window.drawRoute(%s, '%s');", waypointsJsArray, mapBoxKey);

        // Execute JavaScript in the browser view
        Platform.runLater(() ->
                browser.mainFrame().ifPresent(frame -> frame.executeJavaScript(js))
        );
    }




    // Return the BrowserView as a JavaFX Node.
    public Node getView() {
        return browserView;
    }

    // Clean up resources when you're done.
    public void shutdown() {
        if (engine != null) {
            engine.close();
        }
    }
}