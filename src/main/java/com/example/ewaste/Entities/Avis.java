package com.example.ewaste.Entities;

import java.time.LocalDateTime;

public class Avis {
    private int id;
    private Integer userId;
    private String name;
    private String description;
    private String image;
    private String audioFile;
    private String videoFile;
    private String mediaType;
    private int rating; // Rating from 1 to 5 stars
    private LocalDateTime createdAt;

    // Default constructor
    public Avis() {
        this.createdAt = LocalDateTime.now();
    }

    // Basic constructor (for backward compatibility)
    public Avis(int id, String name, String description, int rating) {
        this();
        this.id = id;
        this.name = name;
        this.description = description;
        this.rating = rating;
    }

    // Full constructor
    public Avis(int id, Integer userId, String name, String description, String image,
                String audioFile, String videoFile, String mediaType, int rating, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.description = description;
        this.image = image;
        this.audioFile = audioFile;
        this.videoFile = videoFile;
        this.mediaType = mediaType;
        this.rating = rating;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAudioFile() {
        return audioFile;
    }

    public void setAudioFile(String audioFile) {
        this.audioFile = audioFile;
    }

    public String getVideoFile() {
        return videoFile;
    }

    public void setVideoFile(String videoFile) {
        this.videoFile = videoFile;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

