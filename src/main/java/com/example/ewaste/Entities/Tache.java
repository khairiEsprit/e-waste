package com.example.ewaste.Entities;

public class Tache {
    private int id;
    private int id_centre;
    private int id_employe;
    private float latitude;
    private float longitude;
    private String message;
    private String etat;

    public Tache(int id, int id_centre, int id_employe, float latitude, float longitude, String message, String etat) {
        this.id = id;
        this.id_centre = id_centre;
        this.id_employe = id_employe;
        this.latitude = latitude;
        this.longitude = longitude;
        this.message = message;
        this.etat = etat;
    }

    public Tache(int id_centre, int id_employe, float latitude, float longitude, String message, String etat) {
        this.id_centre = id_centre;
        this.id_employe = id_employe;
        this.latitude = latitude;
        this.longitude = longitude;
        this.message = message;
        this.etat = etat;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_centre() {
        return id_centre;
    }

    public void setId_centre(int id_centre) {
        this.id_centre = id_centre;
    }

    public int getId_employe() {
        return id_employe;
    }

    public void setId_employe(int id_employe) {
        this.id_employe = id_employe;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }

    @Override
    public String toString() {
        return "Tache{" +
                "id=" + id +
                ", id_centre=" + id_centre +
                ", id_employe=" + id_employe +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", message='" + message + '\'' +
                ", etat='" + etat + '\'' +
                '}';
    }
}
