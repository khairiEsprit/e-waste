package com.example.ewaste.Entities;

import java.sql.Timestamp;

public class capteur_pression {
    private int id_cp;
    private int poubelle_id;
    private double poids_mesure;
    private Timestamp date_mesure;
    private double precision_capteur;
    private boolean en_service;

    public capteur_pression() {}

    public capteur_pression(int id_cp, int poubelle_id, double poids_mesure, 
                          Timestamp date_mesure, double precision_capteur, 
                          boolean en_service) {
        this.id_cp = id_cp;
        this.poubelle_id = poubelle_id;
        this.poids_mesure = poids_mesure;
        this.date_mesure = date_mesure;
        this.precision_capteur = precision_capteur;
        this.en_service = en_service;
    }

    // Getters
    public int getId_cp() { return id_cp; }
    public int getPoubelle_id() { return poubelle_id; }
    public double getPoids_mesure() { return poids_mesure; }
    public Timestamp getDate_mesure() { return date_mesure; }
    public double getPrecision_capteur() { return precision_capteur; }
    public boolean isEn_service() { return en_service; }

    // Setters
    public void setId_cp(int id_cp) { this.id_cp = id_cp; }
    public void setPoubelle_id(int poubelle_id) { this.poubelle_id = poubelle_id; }
    public void setPoids_mesure(double poids_mesure) { this.poids_mesure = poids_mesure; }
    public void setDate_mesure(Timestamp date_mesure) { this.date_mesure = date_mesure; }
    public void setPrecision_capteur(double precision_capteur) { this.precision_capteur = precision_capteur; }
    public void setEn_service(boolean en_service) { this.en_service = en_service; }
}
