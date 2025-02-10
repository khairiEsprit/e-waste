package com.example.ewaste.entities;

import java.time.LocalDateTime;
import java.util.Date;

public class User {
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDateTime createdAt;
    private String password;
    private Date DNaissance;
    private int tlp;
    private UserRole role;

    // Enum for user roles


    // Default constructor
    public User() {}

    // Constructor without id (for new users where id is auto-generated)
    public User(String firstName, String email,String password,  Date DNaissance, UserRole role) {
        this.firstName = firstName;
//        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.DNaissance = DNaissance;
//        this.tlp = tlp;
        this.role = role;
//        this.createdAt = createdAt;
    }

    // Constructor with id (for users already persisted)
    public User(int id, String firstName, String lastName, String email, String password, Date DNaissance, int tlp, UserRole role, LocalDateTime createdAt) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.DNaissance = DNaissance;
        this.tlp = tlp;
        this.role = role;
        this.createdAt = createdAt;
    }



    // Getters and Setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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

    public Date getDNaissance() {
        return DNaissance;
    }

    public void setDNaissance(Date DNaissance) {
        this.DNaissance = DNaissance;
    }

    public int getTlp() {
        return tlp;
    }

    public void setTlp(int tlp) {
        this.tlp = tlp;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }
}
