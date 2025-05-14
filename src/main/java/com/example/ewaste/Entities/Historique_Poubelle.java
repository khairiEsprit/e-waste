package com.example.ewaste.Entities;
import java.util.Date;

public class Historique_Poubelle {
    private int id;
    private int poubelle_id;  // Changé de id_poubelle
    private Date date_evenement;
    private type type_evenement;
    private String description;
    private double quantite_dechets;  // Changé de float à double

    public Historique_Poubelle() {}

    public Historique_Poubelle(int id, int poubelle_id, Date date_evenement, 
                              type type_evenement, String description, 
                              double quantite_dechets) {
        this.id = id;
        this.poubelle_id = poubelle_id;
        this.date_evenement = date_evenement;
        this.type_evenement = type_evenement;
        this.description = description;
        this.quantite_dechets = quantite_dechets;
    }

    public Historique_Poubelle(int poubelle_id, Date date_evenement, 
                              type type_evenement, String description, 
                              double quantite_dechets) {
        this.poubelle_id = poubelle_id;
        this.date_evenement = date_evenement;
        this.type_evenement = type_evenement;
        this.description = description;
        this.quantite_dechets = quantite_dechets;
    }

    // Getters et Setters mis à jour
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getPoubelle_id() { return poubelle_id; }
    public void setPoubelle_id(int poubelle_id) { this.poubelle_id = poubelle_id; }

    public Date getDate_evenement() { return date_evenement; }
    public void setDate_evenement(Date date_evenement) { this.date_evenement = date_evenement; }

    public type getType_evenement() { return type_evenement; }
    public void setType_evenement(type type_evenement) { this.type_evenement = type_evenement; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getQuantite_dechets() { return quantite_dechets; }
    public void setQuantite_dechets(double quantite_dechets) { this.quantite_dechets = quantite_dechets; }

    @Override
    public String toString() {
        return "Historique_Poubelle{" +
                "id=" + id +
                ", poubelle_id=" + poubelle_id +
                ", date_evenement=" + date_evenement +
                ", type_evenement=" + type_evenement +
                ", description='" + description + '\'' +
                ", quantite_dechets=" + quantite_dechets +
                '}';
    }
}
