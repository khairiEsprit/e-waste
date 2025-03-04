package com.example.ewaste.Entities;

import java.sql.Timestamp;

public class capteurp {
    private int id;
    private int id_poubelle;
    private float quantite ;
    private Timestamp date_m;

    public capteurp() {
    }

    public capteurp(int id, int id_poubelle, float quantite, Timestamp date_m) {
        this.id = id;
        this.id_poubelle = id_poubelle;
        this.quantite = quantite;
        this.date_m = date_m;
    }

    public capteurp(int id_poubelle, float quantite, Timestamp date_m) {
        this.id_poubelle = id_poubelle;
        this.quantite = quantite;
        this.date_m = date_m;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_poubelle() {
        return id_poubelle;
    }

    public void setId_poubelle(int id_poubelle) {
        this.id_poubelle = id_poubelle;
    }

    public float getQuantite() {
        return quantite;
    }

    public void setQuantite(float quantite) {
        this.quantite = quantite;
    }

    public Timestamp getDate_m() {
        return date_m;
    }

    public void setDate_m(Timestamp date_m) {
        this.date_m = date_m;
    }

    @Override
    public String toString() {
        return "capteurp{" +
                ", id_poubelle=" + id_poubelle +
                ", quantite=" + quantite +
                ", date_m=" + date_m +
                '}';
    }
}
