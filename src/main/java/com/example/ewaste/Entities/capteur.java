package com.example.ewaste.Entities;

import java.sql.Timestamp;
import java.util.Random;

public class capteur {
    private int id_c;
    private int id_poubelle;
    private float distance_mesuree;
    private Timestamp date_mesure;
    private float porteeMaximale =200.0F; // Portée maximale du capteur en cm
    private float precision=1.0F; // Précision du capteur en cm (± valeur)

    // Constructeurs
    public capteur() {

    }
    public capteur(int id_poubelle, float distance_mesuree, Timestamp date_mesure) {
        this.id_c = 0; // Valeur par défaut
        this.id_poubelle = id_poubelle;
        this.distance_mesuree = distance_mesuree;
        this.date_mesure = date_mesure;
        this.porteeMaximale = 200.0f; // Valeur par défaut
        this.precision = 1.0f; // Valeur par défaut
    }

    public capteur(int id_c, int id_poubelle, float distance_mesuree, Timestamp date_mesure, float porteeMaximale, float precision) {
        this.id_c = id_c;
        this.id_poubelle = id_poubelle;
        this.distance_mesuree = distance_mesuree;
        this.date_mesure = date_mesure;
        this.porteeMaximale = porteeMaximale;
        this.precision = precision;

    }

    // Getters et Setters
    public int getId_c() {
        return id_c;
    }

    public void setId_c(int id_c) {
        this.id_c = id_c;
    }

    public int getId_poubelle() {
        return id_poubelle;
    }

    public void setId_poubelle(int id_poubelle) {
        this.id_poubelle = id_poubelle;
    }

    public float getDistance_mesuree() {
        return distance_mesuree;
    }

    public void setDistance_mesuree(float distance_mesuree) {
        this.distance_mesuree = distance_mesuree;
    }

    public Timestamp getDate_mesure() {
        return date_mesure;
    }

    public void setDate_mesure(Timestamp date_mesure) {
        this.date_mesure = date_mesure;
    }

    public float getPorteeMaximale() {
        return porteeMaximale;
    }

    public void setPorteeMaximale(float porteeMaximale) {
        this.porteeMaximale = porteeMaximale;
    }

    public float getPrecision() {
        return precision;
    }

    public void setPrecision(float precision) {
        this.precision = precision;
    }

    @Override
    public String toString() {
        return "capteur{" +
                "id_c=" + id_c +
                ", id_poubelle=" + id_poubelle +
                ", distance_mesuree=" + distance_mesuree +
                ", date_mesure=" + date_mesure +
                ", porteeMaximale=" + porteeMaximale +
                ", precision=" + precision +
                '}';
    }
}