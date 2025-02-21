package com.example.ewaste.Entities;

import java.sql.Date;

public class PlanificationTache {
    private int id;
    private int id_tache;
    private String priorite;
    private Date date_limite;

    public PlanificationTache(int id, int id_tache, String priorite, Date date_limite) {
        this.id = id;
        this.id_tache = id_tache;
        this.priorite = priorite;
        this.date_limite = date_limite;
    }

    public PlanificationTache(int id_tache, String priorite, Date date_limite) {
        this.id_tache = id_tache;
        this.priorite = priorite;
        this.date_limite = date_limite;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_tache() {
        return id_tache;
    }

    public void setId_tache(int id_tache) {
        this.id_tache = id_tache;
    }

    public String getPriorite() {
        return priorite;
    }

    public void setPriorite(String priorite) {
        this.priorite = priorite;
    }

    public Date getDate_limite() {
        return date_limite;
    }

    public void setDate_limite(Date date_limite) {
        this.date_limite = date_limite;
    }

    @Override
    public String toString() {
        return "PlanificationTache{" +
                "id=" + id +
                ", id_tache=" + id_tache +
                ", priorite='" + priorite + '\'' +
                ", date_limite=" + date_limite +
                '}';
    }
}
