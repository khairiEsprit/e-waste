package com.example.ewaste.Entities;
import java.util.Date;

public class poubelle {
    private int id;
    private int id_centre;
    private String adresse;
    private int niveau;
    private etat etat;
    private Date date_installation;
    private int hauteurTotale;
    public poubelle() {}

    public poubelle(int id,int id_centre, String adresse, int niveau, etat etat, Date date_installation, int hauteurTotale) {
        this.id = id;
        this.id_centre = id_centre;
        this.adresse = adresse;
        this.niveau = niveau;
        this.etat = etat;
        this.date_installation = date_installation;
        this.hauteurTotale = hauteurTotale;
    }

    public poubelle(int id_centre, String adresse, int niveau, etat etat, Date date_installation, int hauteurTotale) {
        this.id_centre = id_centre;
        this.adresse = adresse;
        this.niveau = niveau;
        this.etat = etat;
        this.date_installation = date_installation;
        this.hauteurTotale = hauteurTotale;
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

    public void setDate_installation(Date date_installation) {
        this.date_installation = date_installation;
    }

    public Date getDate_installation() {
        return date_installation;
    }

    public com.example.ewaste.Entities.etat getEtat() {
        return etat;
    }

    public void setEtat(com.example.ewaste.Entities.etat etat) {
        this.etat = etat;
    }

    public int getHauteurTotale() {
        return hauteurTotale;
    }
    public void setHauteurTotale(int hauteurTotale) {
        this.hauteurTotale = hauteurTotale;
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
                ", hauteurTotale=" + hauteurTotale +
                '}';
    }
    public void mettreAJourNiveauRemplissage(float distanceMesuree) {
        if (hauteurTotale <= 0) {
            throw new IllegalArgumentException("La hauteur totale doit être supérieure à 0");
        }
        this.niveau = (int) (((hauteurTotale - distanceMesuree) / hauteurTotale) * 100);
    }

    public void verifierSeuilCritique() {
        if (this.niveau >= 90) {
            System.out.println("Notification : La poubelle à l'adresse " + this.adresse + " est presque pleine !");
        }
    }
}
