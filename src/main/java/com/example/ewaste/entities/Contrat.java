package com.example.ewaste.entities;

import java.time.LocalDate;

public class Contrat {
    private int id;
    private int idCentre;
    private int idCitoyen;
    private LocalDate dateDebut;
    private LocalDate dateFin;

    public Contrat() {}

    public Contrat(int id,int idCentre,int idCitoyen,LocalDate dateDebut,LocalDate dateFin) {
        this.id = id;
        this.idCentre = idCentre;
        this.idCitoyen = idCitoyen;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
    }
    public Contrat(int idCentre,int idCitoyen,LocalDate dateDebut,LocalDate dateFin) {
        this.idCentre = idCentre;
        this.idCitoyen = idCitoyen;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
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
    public int getIdCitoyen() {
        return idCitoyen;
    }
    public void setIdCitoyen(int idCitoyen) {
        this.idCitoyen = idCitoyen;
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

    @Override
    public String toString() {
        return "Contrat{" +
                "id=" + id +
                ", idCentre=" + idCentre +
                ", idCitoyen=" + idCitoyen +
                ", dateDebut='" + dateDebut + '\'' +
                ", dateFin='" + dateFin + '\'' +
                '}';
    }
}
















