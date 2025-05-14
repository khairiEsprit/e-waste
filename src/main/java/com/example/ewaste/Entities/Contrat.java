package com.example.ewaste.Entities;

import java.time.LocalDate;

public class Contrat {
    private int id;
    private int centreId;
    private int employeId;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private String signaturePath;

    public Contrat() {}

    public Contrat(int id, int centreId, int employeId, LocalDate dateDebut, LocalDate dateFin, String signaturePath) {
        this.id = id;
        this.centreId = centreId;
        this.employeId = employeId;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.signaturePath = signaturePath;
    }

    public Contrat(int centreId, int employeId, LocalDate dateDebut, LocalDate dateFin, String signaturePath) {
        this.centreId = centreId;
        this.employeId = employeId;
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

    public int getCentreId() {
        return centreId;
    }

    public void setCentreId(int centreId) {
        this.centreId = centreId;
    }

    public int getEmployeId() {
        return employeId;
    }

    public void setEmployeId(int employeId) {
        this.employeId = employeId;
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

    public String getSignaturePath() {
        return signaturePath;
    }

    public void setSignaturePath(String signaturePath) {
        this.signaturePath = signaturePath;
    }

    @Override
    public String toString() {
        return "Contrat{" +
                "id=" + id +
                ", centre_id=" + centreId +
                ", employe_id=" + employeId +
                ", date_debut='" + dateDebut + '\'' +
                ", date_fin='" + dateFin + '\'' +
                ", signature_path='" + signaturePath + '\'' +
                '}';
    }
}