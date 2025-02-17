package com.example.ewaste.Models;

import java.util.Date;

public class poubelle {
    private int id;
    private int id_centre;
    private String adresse;
    private int niveau;
    private etat etat ;
    private Date date_installation;

    public poubelle() {}

    public poubelle(int id, int id_centre, String adresse, int niveau, com.example.ewaste.Models.etat etat, Date date_installation) {
        this.id = id;
        this.id_centre = id_centre;
        this.adresse = adresse;
        this.niveau = niveau;
        this.etat = etat;
        this.date_installation = date_installation;
    }

    public poubelle(int id_centre, String adresse, int niveau, com.example.ewaste.Models.etat etat, Date date_installation) {
        this.id_centre = id_centre;
        this.adresse = adresse;
        this.niveau = niveau;
        this.etat = etat;
        this.date_installation = date_installation;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_centre() {
        return id_centre;
    }

    public void setId_centre(int id_centre) {
        this.id_centre = id_centre;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public int getNiveau() {
        return niveau;
    }

    public void setNiveau(int niveau) {
        this.niveau = niveau;
    }

    public Date getDate_installation() {
        return date_installation;
    }

    public void setDate_installation(Date date_installation) {
        this.date_installation = date_installation;
    }

    public com.example.ewaste.Models.etat getEtat() {
        return etat;
    }

    public void setEtat(com.example.ewaste.Models.etat etat) {
        this.etat = etat;
    }

    @Override
    public String toString() {
        return "poubelle{" +
                "id=" + id +
                ", id_centre=" + id_centre +
                ", adresse='" + adresse + '\'' +
                ", niveau=" + niveau +
                ", etat=" + etat +
                ", date_installation=" + date_installation +
                '}';
    }
}
