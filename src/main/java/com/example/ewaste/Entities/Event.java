package com.example.ewaste.Entities;

import java.time.LocalDateTime;

public class Event {
    private int id;
    private String title;
    private String description;
    private String imageName;
    private int remainingPlaces;
    private String location;
    private LocalDateTime date;
    private String participationMode;
    private String googleMeetLink;

    // Default constructor
    public Event() {
        this.participationMode = "on-site";
        this.remainingPlaces = 0;
    }

    // Basic constructor (for backward compatibility)
    public Event(int id, String title, String description, String imageUrl, int remainingPlaces, String location, LocalDateTime date) {
        this();
        this.id = id;
        this.title = title;
        this.description = description;
        this.imageName = imageUrl; // Map old imageUrl to new imageName
        this.remainingPlaces = remainingPlaces;
        this.location = location;
        this.date = date;
    }

    // Full constructor
    public Event(int id, String title, String description, String imageName, int remainingPlaces,
                String location, LocalDateTime date, String participationMode, String googleMeetLink) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.imageName = imageName;
        this.remainingPlaces = remainingPlaces;
        this.location = location;
        this.date = date;
        this.participationMode = participationMode != null ? participationMode : "on-site";
        this.googleMeetLink = googleMeetLink;
    }

    public boolean isAvailable() {
        return remainingPlaces > 0; // The event is available if there are remaining places
    }

    public boolean isOnline() {
        return "online".equalsIgnoreCase(participationMode);
    }

    public boolean isHybrid() {
        return "hybrid".equalsIgnoreCase(participationMode);
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

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    // For backward compatibility
    public String getImageUrl() {
        return imageName;
    }

    // For backward compatibility
    public void setImageUrl(String imageUrl) {
        this.imageName = imageUrl;
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

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getParticipationMode() {
        return participationMode;
    }

    public void setParticipationMode(String participationMode) {
        this.participationMode = participationMode;
    }

    public String getGoogleMeetLink() {
        return googleMeetLink;
    }

    public void setGoogleMeetLink(String googleMeetLink) {
        this.googleMeetLink = googleMeetLink;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", imageName='" + imageName + '\'' +
                ", remainingPlaces=" + remainingPlaces +
                ", location='" + location + '\'' +
                ", date=" + date +
                ", participationMode='" + participationMode + '\'' +
                ", googleMeetLink='" + googleMeetLink + '\'' +
                '}';
    }
}