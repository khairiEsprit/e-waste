package com.example.ewaste.Entities;

public class Centre {
    private int id;
    private String nom;
    private float latitude;
    private float longitude;
    private int telephone;
    private String email;

    public Centre() {}

    public Centre(int id, String nom) {
        this.id = id;
        this.nom = nom;
    }
    public Centre(String nom,float latitude,float longitude) {
        this.id = id;
        this.nom = nom;
    }
    public Centre(int id, String nom, float latitude, float longitude, int telephone, String email) {
        this.id = id;
        this.nom = nom;
        this.latitude = latitude;
        this.longitude = longitude;
        this.telephone = telephone;
        this.email = email;
    }

    public Centre(String nom, float latitude, float longitude, int telephone, String email) {
        this.nom = nom;
        this.latitude = latitude;
        this.longitude = longitude;
        this.telephone = telephone;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
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

    public int getTelephone() {
        return telephone;
    }

    public void setTelephone(int telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return nom;
    }
}
