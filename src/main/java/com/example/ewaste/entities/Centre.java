package com.example.ewaste.entities;

public class Centre {
    private int id;
    private String nom;
    private String adresse;
    private int telephone;
    private String email;
    public Centre() {}
    public Centre(int id, String nom, String adresse, int telephone, String email) {
        this.id = id;
        this.nom = nom;
        this.adresse = adresse;
        this.telephone = telephone;
        this.email = email;
    }
    public Centre(String nom, String adresse, int telephone, String email) {
        this.nom = nom;
        this.adresse = adresse;
        this.telephone = telephone;
        this.email = email;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getNom() {
        return nom;
    }
    public void setNom(String nom) {
        this.nom = nom;
    }
    public String getAdresse() {
        return adresse;
    }
    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }
    public int getTelephone() {
        return telephone;
    }
    public void setTelephone(int telephone) {
        this.telephone = telephone;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Centre{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", adresse='" + adresse + '\'' +
                ", telephone=" + telephone +
                ", email='" + email + '\'' +
                '}';
    }
}



