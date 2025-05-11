package com.example.ewaste.Utils;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeLineCap;
import javafx.util.Duration;

/**
 * A custom progress spinner implementation to replace MFXProgressSpinner
 */
public class CustomProgressSpinner extends Region {

    private final Arc arc;
    private final Circle circle;
    private final DoubleProperty progress = new SimpleDoubleProperty(0);
    private final Timeline timeline;
    private String progressColor = "#0C162C";

    public CustomProgressSpinner() {
        // Create the background circle
        circle = new Circle(50, 50, 40);
        circle.setFill(Color.TRANSPARENT);
        circle.setStroke(Color.LIGHTGRAY);
        circle.setStrokeWidth(5);

        // Create the progress arc
        arc = new Arc(50, 50, 40, 40, 90, 0);
        arc.setFill(Color.TRANSPARENT);
        arc.setStroke(Color.web(progressColor));
        arc.setStrokeWidth(5);
        arc.setStrokeLineCap(StrokeLineCap.ROUND);
        arc.setType(ArcType.OPEN);

        // Add the shapes to the region
        getChildren().addAll(circle, arc);

        // Create the animation
        timeline = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(progress, 0)),
            new KeyFrame(Duration.seconds(1.5), new KeyValue(progress, 360))
        );
        timeline.setCycleCount(Animation.INDEFINITE);

        // Bind the arc length to the progress property
        progress.addListener((obs, oldVal, newVal) -> {
            arc.setLength(-newVal.doubleValue());
        });

        // Start the animation
        timeline.play();

        // Set the preferred size
        setPrefSize(100, 100);
    }

    @Override
    protected void layoutChildren() {
        final double width = getWidth();
        final double height = getHeight();

        // Center the circle and arc
        final double centerX = width / 2;
        final double centerY = height / 2;
        final double radius = Math.min(width, height) / 2 - 5;

        circle.setCenterX(centerX);
        circle.setCenterY(centerY);
        circle.setRadius(radius);

        arc.setCenterX(centerX);
        arc.setCenterY(centerY);
        arc.setRadiusX(radius);
        arc.setRadiusY(radius);
    }

    public void setPrefSize(double width, double height) {
        super.setPrefSize(width, height);
        super.setMinSize(width, height);
        super.setMaxSize(width, height);
    }

    // Custom method to set progress color instead of overriding the final setStyle method
    public void setProgressStyle(String style) {
        if (style.contains("-fx-progress-color:")) {
            String colorValue = style.substring(style.indexOf("-fx-progress-color:") + 19);
            colorValue = colorValue.substring(0, colorValue.indexOf(";") > 0 ? colorValue.indexOf(";") : colorValue.length()).trim();
            progressColor = colorValue;
            arc.setStroke(Color.web(progressColor));
        }
    }
}
