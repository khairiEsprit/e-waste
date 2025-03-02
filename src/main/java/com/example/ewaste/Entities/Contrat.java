package com.example.ewaste.Entities;

import java.time.LocalDate;

public class Contrat {
    private int id;
    private int idCentre;
    private int idEmploye;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private String signaturePath;

    public Contrat() {}

    public Contrat(int id,int idCentre,int idEmploye,LocalDate dateDebut,LocalDate dateFin,String signaturePath) {
        this.id = id;
        this.idCentre = idCentre;
        this.idEmploye = idEmploye;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.signaturePath = signaturePath;
    }
    public Contrat(int idCentre,int idEmploye,LocalDate dateDebut,LocalDate dateFin,String signaturePath) {
        this.idCentre = idCentre;
        this.idEmploye = idEmploye;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.signaturePath = signaturePath;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getIdCentre() {
        return idCentre;
    }
    public void setIdCentre(int idCentre) {
        this.idCentre = idCentre;
    }
    public int getIdEmploye() {
        return idEmploye;
    }
    public void setIdEmploye(int idEmploye) {
        this.idEmploye = idEmploye;
    }
    public LocalDate getDateDebut() {
        return dateDebut;
    }
    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }
    public LocalDate getDateFin() {
        return dateFin;
    }
    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }

    public String getSignaturePath() {return signaturePath;}

    public void setSignaturePath(String signaturePath) {this.signaturePath = signaturePath;}

    @Override
    public String toString() {
        return "Contrat{" +
                "id=" + id +
                ", id_centre=" + idCentre +
                ", id_employe=" + idEmploye +
                ", date_debut='" + dateDebut + '\'' +
                ", date_fin='" + dateFin + '\'' +
                '}';
    }
}
















