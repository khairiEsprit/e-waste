package com.example.ewaste.entities;

public class Demande {
    private int id;
    private int idUtilisateur;
    private int idCentre;
    private String adresse;
    private String emailUtilisateur;
    private String message;
    private String type;

    public Demande() {
    }

    public Demande(int id, int idUtilisateur, int idCentre, String adresse, String emailUtilisateur, String message, String type) {
        this.id = id;
        this.idUtilisateur = idUtilisateur;
        this.idCentre = idCentre;
        this.adresse = adresse;
        this.emailUtilisateur = emailUtilisateur;
        this.message = message;
        this.type = type;
    }

    public Demande(int idUtilisateur, int idCentre, String adresse, String emailUtilisateur, String message, String type) {
        this.idUtilisateur = idUtilisateur;
        this.idCentre = idCentre;
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

    public int getIdUtilisateur() {
        return idUtilisateur;
    }

    public void setIdUtilisateur(int idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }

    public int getIdCentre() {
        return idCentre;
    }

    public void setIdCentre(int idCentre) {
        this.idCentre = idCentre;
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
                ", idUtilisateur=" + idUtilisateur +
                ", idCentre=" + idCentre +
                ", adresse='" + adresse + '\'' +
                ", emailUtilisateur='" + emailUtilisateur + '\'' +
                ", message='" + message + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
