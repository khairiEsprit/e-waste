package com.example.ewaste.Entities;

public class Demande {
    private int id;
    private int utilisateur_id;
    private int centre_id;
    private String adresse;
    private String emailUtilisateur;
    private String message;
    private String type;

    public Demande() {
    }

    public Demande(int id, int utilisateur_id, int centre_id, String adresse, String emailUtilisateur, String message, String type) {
        this.id = id;
        this.utilisateur_id = utilisateur_id;
        this.centre_id = centre_id;
        this.adresse = adresse;
        this.emailUtilisateur = emailUtilisateur;
        this.message = message;
        this.type = type;
    }

    public Demande(int utilisateur_id, int centre_id, String adresse, String emailUtilisateur, String message, String type) {
        this.utilisateur_id = utilisateur_id;
        this.centre_id = centre_id;
        this.adresse = adresse;
        this.emailUtilisateur = emailUtilisateur;
        this.message = message;
        this.type = type;
    }

    public Demande(String adresse, String emailUtilisateur, String message, String type) {
        this.adresse = adresse;
        this.emailUtilisateur = emailUtilisateur;
        this.message = message;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getutilisateur_id() {
        return utilisateur_id;
    }

    public void setutilisateur_id(int utilisateur_id) {
        this.utilisateur_id = utilisateur_id;
    }

    public int getcentre_id() {
        return centre_id;
    }

    public void setcentre_id(int centre_id) {
        this.centre_id = centre_id;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getEmailUtilisateur() {
        return emailUtilisateur;
    }

    public void setEmailUtilisateur(String emailUtilisateur) {
        this.emailUtilisateur = emailUtilisateur;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Demande{" +
                "id=" + id +
                ", utilisateur_id=" + utilisateur_id +
                ", centre_id=" + centre_id +
                ", adresse='" + adresse + '\'' +
                ", emailUtilisateur='" + emailUtilisateur + '\'' +
                ", message='" + message + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
