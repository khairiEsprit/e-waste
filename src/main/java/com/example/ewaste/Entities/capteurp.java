package com.example.ewaste.Entities;

import java.sql.Timestamp;

public class capteurp {
    private int id;
    private int poubelle_id;  // Renommé de id_poubelle
    private double quantite;  // Changé de float à double
    private Timestamp date_m;

    public capteurp() {
    }

    public capteurp(int id, int poubelle_id, double quantite, Timestamp date_m) {
        this.id = id;
        this.poubelle_id = poubelle_id;
        this.quantite = quantite;
        this.date_m = date_m;
    }

    public capteurp(int poubelle_id, double quantite, Timestamp date_m) {
        this.poubelle_id = poubelle_id;
        this.quantite = quantite;
        this.date_m = date_m;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPoubelle_id() {
        return poubelle_id;
    }

    public void setPoubelle_id(int poubelle_id) {
        this.poubelle_id = poubelle_id;
    }

    public double getQuantite() {
        return quantite;
    }

    public void setQuantite(double quantite) {
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
                "id=" + id +
                ", poubelle_id=" + poubelle_id +
                ", quantite=" + quantite +
                ", date_m=" + date_m +
                '}';
    }
}
