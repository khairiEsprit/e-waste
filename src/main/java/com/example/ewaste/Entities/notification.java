package com.example.ewaste.Entities;

import java.util.Date;

public class notification {
    private int id_n;
    private int id_poubelle;
    private float niveau_remplissage;
    private String message;
    private Statut statut;
    private Date date_envoi;

    public notification() {
    }

    public notification(int id_poubelle, float niveau_remplissage, String message, Statut statut, Date date_envoi) {
        this.id_poubelle = id_poubelle;
        this.niveau_remplissage = niveau_remplissage;
        this.message = message;
        this.statut = statut;
        this.date_envoi = date_envoi;
    }

    public notification(int id_n, int id_poubelle, float niveau_remplissage, String message, Statut statut, Date date_envoi) {
        this.id_n = id_n;
        this.id_poubelle = id_poubelle;
        this.niveau_remplissage = niveau_remplissage;
        this.message = message;
        this.statut = statut;
        this.date_envoi = date_envoi;
    }

    public int getId_n() {
        return id_n;
    }

    public void setId_n(int id_n) {
        this.id_n = id_n;
    }

    public int getId_poubelle() {
        return id_poubelle;
    }

    public void setId_poubelle(int id_poubelle) {
        this.id_poubelle = id_poubelle;
    }

    public float getNiveau_remplissage() {
        return niveau_remplissage;
    }

    public void setNiveau_remplissage(float niveau_remplissage) {
        this.niveau_remplissage = niveau_remplissage;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Statut getStatut() {
        return statut;
    }

    public void setStatut(Statut statut) {
        this.statut = statut;
    }

    public Date getDate_envoi() {
        return date_envoi;
    }

    public void setDate_envoi(Date date_envoi) {
        this.date_envoi = date_envoi;
    }

    @Override
    public String toString() {
        return "notification{" +
                "id_n=" + id_n +
                ", id_poubelle=" + id_poubelle +
                ", niveau_remplissage=" + niveau_remplissage +
                ", message='" + message + '%' +
                ", statut=" + statut +
                ", date_envoi=" + date_envoi +
                '}';
    }
}
