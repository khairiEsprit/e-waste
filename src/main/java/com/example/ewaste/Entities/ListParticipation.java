package com.example.ewaste.Entities;


public class ListParticipation {
    // Champs de la classe
    private String lastName;    // Nom
    private String firstName;   // Prénom
    private String email;       // E-mail
    private String phone;       // Téléphone
    private String country;     // Pays
    private String postalCode;  // Code postal
    private String city;        // Ville

    // Constructeur par défaut
    public ListParticipation() {
    }

    // Constructeur avec paramètres
    public ListParticipation(String lastName, String firstName, String email, String phone, String country, String postalCode, String city) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.email = email;
        this.phone = phone;
        this.country = country;
        this.postalCode = postalCode;
        this.city = city;
    }

    // Getters et Setters

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    // Méthode toString pour afficher les détails de la participation
    @Override
    public String toString() {
        return "ListParticipation{" +
                "lastName='" + lastName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", country='" + country + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", city='" + city + '\'' +
                '}';
    }
}
