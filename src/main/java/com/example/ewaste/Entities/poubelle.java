package com.example.ewaste.Entities;
import java.util.Date;

public class poubelle {
    private int id;
    private int centre_id;
    private String nomCentre;
    private String adresse;
    private int niveau;
    private etat etat;
    private Date date_installation;
    private int hauteur_totale;
    private double latitude;
    private double longitude;
    private double revenu_genere;
    private int hauteurTotale;

    // Constructors
    public poubelle() {}

    public poubelle(int id, int centre_id, String nomCentre, String adresse, int niveau, etat etat, 
                   Date date_installation, int hauteur_totale, double latitude, 
                   double longitude, double revenu_genere) {
        this.id = id;
        this.centre_id = centre_id;
        this.nomCentre = nomCentre;
        this.adresse = adresse;
        this.niveau = niveau;
        this.etat = etat;
        this.date_installation = date_installation;
        this.hauteur_totale = hauteur_totale;
        this.latitude = latitude;
        this.longitude = longitude;
        this.revenu_genere = revenu_genere;
    }

    public poubelle(int centre_id, String nomCentre, String adresse, int niveau, etat etat, Date date_installation, int hauteur_totale) {
        this.centre_id = centre_id;
        this.nomCentre = nomCentre;
        this.adresse = adresse;
        this.niveau = niveau;
        this.etat = etat;
        this.date_installation = date_installation;
        this.hauteur_totale = hauteur_totale;
    }

    public poubelle(int id, String adresse, int niveau, etat etat, Date date_installation, int hauteurTotale) {
        this.id = id;
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
        return centre_id;
    }

    public void setId_centre(int centre_id) {
        this.centre_id = centre_id;
    }

    public String getNomCentre() {
        return nomCentre;
    }

    public void setNomCentre(String nomCentre) {
        this.nomCentre = nomCentre;
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

    public int getHauteurTotaleOld() {
        return hauteur_totale;
    }

    public void setHauteurTotaleOld(int hauteur_totale) {
        this.hauteur_totale = hauteur_totale;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getRevenu_genere() {
        return revenu_genere;
    }

    public void setRevenu_genere(double revenu_genere) {
        this.revenu_genere = revenu_genere;
    }

    @Override
    public String toString() {
        return "poubelle{" +
                "id=" + id +
                ", centre_id=" + centre_id +
                ", nomCentre='" + nomCentre + '\'' +
                ", adresse='" + adresse + '\'' +
                ", niveau=" + niveau +
                ", etat=" + etat +
                ", date_installation=" + date_installation +
                ", hauteur_totale=" + hauteur_totale +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", revenu_genere=" + revenu_genere +
                ", hauteurTotale=" + hauteurTotale +
                '}';
    }

    public void mettreAJourNiveauRemplissage(float distanceMesuree) {
        if (hauteur_totale <= 0) {
            throw new IllegalArgumentException("La hauteur totale doit être supérieure à 0");
        }
        this.niveau = (int) (((hauteur_totale - distanceMesuree) / hauteur_totale) * 100);
    }

    public void verifierSeuilCritique() {
        if (this.niveau >= 90) {
            System.out.println("Notification : La poubelle à l'adresse " + this.adresse + " est presque pleine !");
        }
    }
}
