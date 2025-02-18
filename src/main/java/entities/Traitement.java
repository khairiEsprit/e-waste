package entities;

import java.time.LocalDateTime;

public class Traitement {
    private int id;
    private int idDemande;
    private String status;
    private LocalDateTime dateTraitement;
    private String commentaire;

    public Traitement() {
    }


    public Traitement(int id, int idDemande, String status, LocalDateTime dateTraitement, String commentaire) {
        this.id = id;
        this.idDemande = idDemande;
        this.status = status;
        this.dateTraitement = dateTraitement;
        this.commentaire = commentaire;
    }

    // Constructor without ID (for inserting new Traitement)
    public Traitement(int idDemande, String status, LocalDateTime dateTraitement, String commentaire) {
        this.idDemande = idDemande;
        this.status = status;
        this.dateTraitement = dateTraitement;
        this.commentaire = commentaire;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdDemande() {
        return idDemande;
    }

    public void setIdDemande(int idDemande) {
        this.idDemande = idDemande;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getDateTraitement() {
        return dateTraitement;
    }

    public void setDateTraitement(LocalDateTime dateTraitement) {
        this.dateTraitement = dateTraitement;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    // toString Method
    @Override
    public String toString() {
        return "Traitement{" +
                "id=" + id +
                ", idDemande=" + idDemande +
                ", status='" + status + '\'' +
                ", dateTraitement=" + dateTraitement +
                ", commentaire='" + commentaire + '\'' +
                '}';
    }
}
