package com.example.ewaste.entities;


import java.time.LocalDateTime;
import java.util.Date;

public class User {
    private String photoUrl;
    private int id;
    private String status;
    private String nom;
    private String prenom;
    private String email;
    private LocalDateTime createdAt;
    private String password;
    private Date DateNss;
    private int telephone;
    private UserRole role;

    // Default constructor
    public User() {}

    // Constructor without id (for new users where id is auto-generated)
    public User(String nom, String email, String password, Date DateNss, UserRole role) {
        this.nom = nom;
        this.email = email;
        this.password = password;
        this.DateNss = DateNss;
        this.role = role;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    // Constructor with id (for users already persisted)
    public User(int id,String photoUrl, String status, String nom, String prenom, String email, Date DateNss, int telephone, UserRole role) {
        this.id = id;
        this.status = status;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.DateNss = DateNss;
        this.telephone = telephone;
        this.role = role;
        this.photoUrl = photoUrl;
    }

    public String getStatus() {
        return status;
    }
    // Getters and Setters

    public Integer getId() {
        return id;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getDateNss() {
        return DateNss;
    }

    public void setDateNss(Date DateNss) {
        this.DateNss = DateNss;
    }

    public int getTelephone() {
        return telephone;
    }

    public void setTelephone(int telephone) {
        this.telephone = telephone;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", email='" + email + '\'' +
                ", createdAt=" + createdAt +
                ", password='" + password + '\'' +
                ", DateNss=" + DateNss +
                ", telephone=" + telephone +
                ", role=" + role +
                '}';
    }
}
