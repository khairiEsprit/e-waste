package com.example.ewaste.Entities;

public class Participation {
    private int id;
    private Integer eventId;
    private Integer userId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String city;
    private String country;
    private String zipCode;
    private boolean reminderSent;
    private String participationMode;
    private String googleMeetLink;
    private boolean googleAuthenticated;
    private int pointsEarned; // Kept for backward compatibility

    // Default constructor
    public Participation() {
        this.reminderSent = false;
        this.participationMode = "on-site";
        this.googleAuthenticated = false;
        this.pointsEarned = 0;
    }

    // Basic constructor (for backward compatibility)
    public Participation(int id, String firstName, String lastName, String email, String phone, String city, String country, String zipCode, int pointsEarned) {
        this();
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.city = city;
        this.country = country;
        this.zipCode = zipCode;
        this.pointsEarned = pointsEarned;
    }

    // Full constructor
    public Participation(int id, Integer eventId, Integer userId, String firstName, String lastName,
                        String email, String phone, String city, String zipCode, String country,
                        boolean reminderSent, String participationMode, String googleMeetLink,
                        boolean googleAuthenticated) {
        this.id = id;
        this.eventId = eventId;
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.city = city;
        this.zipCode = zipCode;
        this.country = country;
        this.reminderSent = reminderSent;
        this.participationMode = participationMode != null ? participationMode : "on-site";
        this.googleMeetLink = googleMeetLink;
        this.googleAuthenticated = googleAuthenticated;
        this.pointsEarned = 0; // Default value
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public boolean isReminderSent() {
        return reminderSent;
    }

    public void setReminderSent(boolean reminderSent) {
        this.reminderSent = reminderSent;
    }

    public String getParticipationMode() {
        return participationMode;
    }

    public void setParticipationMode(String participationMode) {
        this.participationMode = participationMode;
    }

    public String getGoogleMeetLink() {
        return googleMeetLink;
    }

    public void setGoogleMeetLink(String googleMeetLink) {
        this.googleMeetLink = googleMeetLink;
    }

    public boolean isGoogleAuthenticated() {
        return googleAuthenticated;
    }

    public void setGoogleAuthenticated(boolean googleAuthenticated) {
        this.googleAuthenticated = googleAuthenticated;
    }

    public int getPointsEarned() {
        return pointsEarned;
    }

    public void setPointsEarned(int pointsEarned) {
        this.pointsEarned = pointsEarned;
    }

    @Override
    public String toString() {
        return "Participation{" +
                "id=" + id +
                ", eventId=" + eventId +
                ", userId=" + userId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", city='" + city + '\'' +
                ", country='" + country + '\'' +
                ", zipCode='" + zipCode + '\'' +
                ", reminderSent=" + reminderSent +
                ", participationMode='" + participationMode + '\'' +
                ", googleMeetLink='" + googleMeetLink + '\'' +
                ", googleAuthenticated=" + googleAuthenticated +
                ", pointsEarned=" + pointsEarned +
                '}';
    }
}