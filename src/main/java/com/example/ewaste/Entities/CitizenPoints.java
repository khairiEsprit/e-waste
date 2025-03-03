package com.example.ewaste.Entities;

import java.util.Objects;

/**
 * Représente les points accumulés par un citoyen.
 */
public class CitizenPoints {
    private final String email; // Identifiant unique du citoyen
    private final String firstName;
    private final String lastName;
    private int totalPoints; // Total des points accumulés

    /**
     * Constructeur par défaut.
     */
    public CitizenPoints() {
        this.email = "";
        this.firstName = "";
        this.lastName = "";
        this.totalPoints = 0;
    }

    /**
     * Constructeur avec paramètres.
     *
     * @param email      L'email du citoyen.
     * @param firstName  Le prénom du citoyen.
     * @param lastName   Le nom de famille du citoyen.
     * @param totalPoints Le total des points accumulés.
     */
    public CitizenPoints(String email, String firstName, String lastName, int totalPoints) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("L'email ne peut pas être vide ou null.");
        }
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("Le prénom ne peut pas être vide ou null.");
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de famille ne peut pas être vide ou null.");
        }
        if (totalPoints < 0) {
            throw new IllegalArgumentException("Le total des points ne peut pas être négatif.");
        }

        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.totalPoints = totalPoints;
    }

    // Getters
    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    // Setters (seulement pour totalPoints, car les autres champs sont immuables)
    public void setTotalPoints(int totalPoints) {
        if (totalPoints < 0) {
            throw new IllegalArgumentException("Le total des points ne peut pas être négatif.");
        }
        this.totalPoints = totalPoints;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CitizenPoints that = (CitizenPoints) o;
        return Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }

    @Override
    public String toString() {
        return "CitizenPoints{" +
                "email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", totalPoints=" + totalPoints +
                '}';
    }
}