package com.example.ewaste.Entities;

import java.sql.Timestamp;
import java.util.Random;
public class capteur {
    private int id_c;
    private int poubelle_id;  // Renommé de id_poubelle
    private double distance_mesuree;  // Changé de float à double
    private Timestamp date_mesure;
    private double portee_maximale;  // Renommé de porteeMaximale et changé en double
    private double precision_capteur;  // Renommé de precision et changé en double

    public capteur() {}

    public capteur(int id_c, int poubelle_id, double distance_mesuree, 
                  Timestamp date_mesure, double portee_maximale, 
                  double precision_capteur) {
        this.id_c = id_c;
        this.poubelle_id = poubelle_id;
        this.distance_mesuree = distance_mesuree;
        this.date_mesure = date_mesure;
        this.portee_maximale = portee_maximale;
        this.precision_capteur = precision_capteur;
    }

    // Getters et Setters mis à jour
    public int getId_c() { return id_c; }
    public void setId_c(int id_c) { this.id_c = id_c; }

    public int getPoubelle_id() { return poubelle_id; }
    public void setPoubelle_id(int poubelle_id) { this.poubelle_id = poubelle_id; }

    public double getDistance_mesuree() { return distance_mesuree; }
    public void setDistance_mesuree(double distance_mesuree) { 
        this.distance_mesuree = distance_mesuree; 
    }

    public Timestamp getDate_mesure() { return date_mesure; }
    public void setDate_mesure(Timestamp date_mesure) { 
        this.date_mesure = date_mesure; 
    }

    public double getPortee_maximale() { return portee_maximale; }
    public void setPortee_maximale(double portee_maximale) { 
        this.portee_maximale = portee_maximale; 
    }

    public double getPrecision_capteur() { return precision_capteur; }
    public void setPrecision_capteur(double precision_capteur) { 
        this.precision_capteur = precision_capteur; 
    }

    @Override
    public String toString() {
        return "capteur{" +
                "id_c=" + id_c +
                ", poubelle_id=" + poubelle_id +
                ", distance_mesuree=" + distance_mesuree +
                ", date_mesure=" + date_mesure +
                ", portee_maximale=" + portee_maximale +
                ", precision_capteur=" + precision_capteur +
                '}';
    }
}