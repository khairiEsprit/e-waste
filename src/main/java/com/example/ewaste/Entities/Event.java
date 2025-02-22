package com.example.ewaste.Entities;

import java.time.LocalDate;

public class Event {
    private int id;
    private String title;
    private String description;
    private String imageUrl;
    private int remainingPlaces;
    private String location;
    private LocalDate date; // Utilisez LocalDate au lieu de java.util.Date

    // Constructor
    public Event(int id, String title, String description, String imageUrl, int remainingPlaces, String location, LocalDate date) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.remainingPlaces = remainingPlaces;
        this.location = location;
        this.date = date;
    }

    public boolean isAvailable() {
        return remainingPlaces > 0; // The event is available if there are remaining places
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getRemainingPlaces() {
        return remainingPlaces;
    }

    public void setRemainingPlaces(int remainingPlaces) {
        this.remainingPlaces = remainingPlaces;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", remainingPlaces=" + remainingPlaces +
                ", location='" + location + '\'' +
                ", date=" + date +
                '}';
    }
}