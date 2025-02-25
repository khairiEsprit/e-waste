package com.example.ewaste.Entities;

import java.sql.Timestamp;
import java.util.Random;

public class capteur {
    private int id_c;
    private int id_poubelle;
    private float distance_mesuree;
    private Timestamp date_mesure;
    private float porteeMaximale; // Portée maximale du capteur en cm
    private float precision; // Précision du capteur en cm (± valeur)
    private Random random; // Générateur de nombres aléatoires

    // Constructeurs
    public capteur() {
        this.random = new Random();
    }
    public capteur(int id_poubelle, float distance_mesuree, Timestamp date_mesure) {
        this.id_c = 0; // Valeur par défaut
        this.id_poubelle = id_poubelle;
        this.distance_mesuree = distance_mesuree;
        this.date_mesure = date_mesure;
        this.porteeMaximale = 150.0f; // Valeur par défaut
        this.precision = 1.0f; // Valeur par défaut
    }

    public capteur(int id_c, int id_poubelle, float distance_mesuree, Timestamp date_mesure, float porteeMaximale, float precision) {
        this.id_c = id_c;
        this.id_poubelle = id_poubelle;
        this.distance_mesuree = distance_mesuree;
        this.date_mesure = date_mesure;
        this.porteeMaximale = porteeMaximale;
        this.precision = precision;
        this.random = new Random();
    }

    // Méthode pour simuler la mesure de distance
    public float simulerDistanceMesuree(int hauteurTotale, float dernierNiveau) {
        if (hauteurTotale <= 0) {
            throw new IllegalArgumentException("La hauteur totale doit être supérieure à 0 cm.");
        }

        // Vérifier que la portée maximale est suffisante
        if (porteeMaximale < hauteurTotale) {
            throw new IllegalArgumentException("La portée maximale du capteur doit être supérieure ou égale à la hauteur totale de la poubelle.");
        }

        // Simuler une variation progressive du niveau de remplissage
        float variation = (random.nextFloat() * 10) - 5; // Variation aléatoire entre -5 et +5 cm
        float distanceMesuree = dernierNiveau + variation;

        // Ajouter une marge d'erreur aléatoire pour simuler l'imprécision du capteur
        float erreur = (random.nextFloat() * 2 * precision) - precision; // Erreur entre -precision et +precision
        distanceMesuree += erreur;

        // S'assurer que la distance reste dans les limites [0, porteeMaximale]
        distanceMesuree = Math.max(0, Math.min(porteeMaximale, distanceMesuree));

        // S'assurer que la distance ne dépasse pas la hauteur totale de la poubelle
        distanceMesuree = Math.min(distanceMesuree, hauteurTotale);

        // Arrondir la distance à 2 décimales
        distanceMesuree = Math.round(distanceMesuree * 100) / 100.0f;

        // Mettre à jour les attributs de la classe
        this.distance_mesuree = distanceMesuree;
        this.date_mesure = new Timestamp(System.currentTimeMillis());

        return distanceMesuree;
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