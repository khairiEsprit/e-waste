package com.example.ewaste.Models;

import java.sql.Timestamp;
import java.util.Date;

public class Historique_Poubelle {
    private int id;
    private int id_poubelle;
    private Date date_evenement ;
    private type type_evenement ;
    private String description ;
    private float quantite_dechets;

    public Historique_Poubelle() {}
    public Historique_Poubelle(int id, int id_poubelle, Date date_evenement, type type_evenement, String description , float quantite_dechets) {
        this.id = id;
        this.id_poubelle = id_poubelle;
        date_evenement = date_evenement;
        this.type_evenement = type_evenement;
        this.description = description;
        this.quantite_dechets = quantite_dechets;
    }

    public Historique_Poubelle(int id_poubelle, Date date_evenement, type type_evenement, String description, float quantite_dechets) {
        this.id_poubelle = id_poubelle;
        this.date_evenement = date_evenement;
        this.type_evenement = type_evenement;
        this.description = description;
        this.quantite_dechets = quantite_dechets;
    }

    public float getQuantite_dechets() {
        return quantite_dechets;
    }

    public void setQuantite_dechets(float quantite_dechets) {
        this.quantite_dechets = quantite_dechets;
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


    public Date getDate_evenement() {
        return date_evenement;
    }

    public void setDate_evenement(Date date_evenement) {
        this.date_evenement = date_evenement;
    }

    public type getType_evenement() {
        return type_evenement;
    }

    public void setType_evenement(type type_evenement) {
        this.type_evenement = type_evenement;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Historique_Poubelle{" +
                "id=" + id +
                ", id_poubelle=" + id_poubelle +
                ", Date_evenement=" + date_evenement +
                ", type_evenement=" + type_evenement +
                ", description='" + description + '\'' +
                ", quantite_dechets=" + quantite_dechets +
                '}';
    }
}
