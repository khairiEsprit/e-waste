package com.example.ewaste.Entities;

public class poubelleK {
    private int id;
    private double latitude;
    private double longitude;
    private int fillLevel;
    private boolean isWorking;
    private int centerId; // New attribute for center linkage

    public poubelleK(int id, double latitude, double longitude, int fillLevel, boolean isWorking, int centerId) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.fillLevel = fillLevel;
        this.isWorking = isWorking;
        this.centerId = centerId;
    }

    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public int getFillLevel() { return fillLevel; }
    public boolean isWorking() { return isWorking; }
    public int getCenterId() { return centerId; }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setFillLevel(int fillLevel) {
        this.fillLevel = fillLevel;
    }

    public void setWorking(boolean working) {
        isWorking = working;
    }

    public void setCenterId(int centerId) {
        this.centerId = centerId;
    }
}