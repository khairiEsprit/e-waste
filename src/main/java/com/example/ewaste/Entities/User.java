package com.example.ewaste.Entities;

import java.sql.Date;
import java.sql.Timestamp;

public class User {
    // Primary fields
    private int id;
    private String password;
    private String email;
    private boolean active;
    private boolean freeze;
    private String roles; // Stored as JSON or serialized array

    // Profile information
    private String first_name; // Renamed from nom
    private String last_name; // Renamed from prenom
    private String phone; // Renamed from telephone, changed to String
    private Date birthdate; // Renamed from DateNss
    private String language;
    private String wallet_address;

    // Profile image and face recognition
    private String profile_image; // Renamed from photoUrl
    private boolean is_face_recognition_enabled;
    private String face_embeddings;
    private String face_photo_path;

    // User experience
    private boolean has_seen_guide;

    // Authentication and timestamps
    private Timestamp last_login;
    private String confirmation_token;
    private Timestamp password_requested_at;
    private Timestamp created_at; // Changed from LocalDateTime to Timestamp

    // Default constructor
    public User() {
        // Set default values for non-nullable fields
        this.active = true;
        this.freeze = false;
        this.is_face_recognition_enabled = false;
        this.has_seen_guide = false;
        this.roles = "ROLE_USER"; // Default role
    }

    // Constructor without id (for new users where id is auto-generated)
    public User(String first_name, String email, String password, Date birthdate, UserRole role) {
        this();
        this.first_name = first_name;
        this.email = email;
        this.password = password;
        this.birthdate = birthdate;
        setRoleFromEnum(role);
    }

    // Constructor with id (for users already persisted)
    public User(int id, String profile_image, boolean active, String first_name, String last_name,
                String email, Date birthdate, String phone, UserRole role) {
        this();
        this.id = id;
        this.active = active;
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.birthdate = birthdate;
        this.phone = phone;
        this.profile_image = profile_image;
        setRoleFromEnum(role);
    }

    // Helper method to convert UserRole enum to roles string
    private void setRoleFromEnum(UserRole role) {
        if (role != null) {
            this.roles = "ROLE_" + role.toString();
        } else {
            this.roles = "ROLE_USER";
        }
    }

    // Helper method to convert roles string to UserRole enum
    public UserRole getRoleAsEnum() {
        if (roles != null && roles.startsWith("ROLE_")) {
            String roleStr = roles.substring(5); // Remove "ROLE_" prefix
            try {
                return UserRole.valueOf(roleStr);
            } catch (IllegalArgumentException e) {
                return UserRole.CITOYEN; // Default role
            }
        }
        return UserRole.CITOYEN; // Default role
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isFreeze() {
        return freeze;
    }

    public void setFreeze(boolean freeze) {
        this.freeze = freeze;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getWallet_address() {
        return wallet_address;
    }

    public void setWallet_address(String wallet_address) {
        this.wallet_address = wallet_address;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    public boolean isIs_face_recognition_enabled() {
        return is_face_recognition_enabled;
    }

    public void setIs_face_recognition_enabled(boolean is_face_recognition_enabled) {
        this.is_face_recognition_enabled = is_face_recognition_enabled;
    }

    public String getFace_embeddings() {
        return face_embeddings;
    }

    public void setFace_embeddings(String face_embeddings) {
        this.face_embeddings = face_embeddings;
    }

    public String getFace_photo_path() {
        return face_photo_path;
    }

    public void setFace_photo_path(String face_photo_path) {
        this.face_photo_path = face_photo_path;
    }

    public boolean isHas_seen_guide() {
        return has_seen_guide;
    }

    public void setHas_seen_guide(boolean has_seen_guide) {
        this.has_seen_guide = has_seen_guide;
    }

    public Timestamp getLast_login() {
        return last_login;
    }

    public void setLast_login(Timestamp last_login) {
        this.last_login = last_login;
    }

    public String getConfirmation_token() {
        return confirmation_token;
    }

    public void setConfirmation_token(String confirmation_token) {
        this.confirmation_token = confirmation_token;
    }

    public Timestamp getPassword_requested_at() {
        return password_requested_at;
    }

    public void setPassword_requested_at(Timestamp password_requested_at) {
        this.password_requested_at = password_requested_at;
    }

    public Timestamp getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Timestamp created_at) {
        this.created_at = created_at;
    }

    // Compatibility methods for existing code
    public String getNom() {
        return first_name;
    }

    public void setNom(String nom) {
        this.first_name = nom;
    }

    public String getPrenom() {
        return last_name;
    }

    public void setPrenom(String prenom) {
        this.last_name = prenom;
    }

    public Date getDateNss() {
        return birthdate;
    }

    public void setDateNss(Date dateNss) {
        this.birthdate = dateNss;
    }

    public String getTelephone() {
        return phone;
    }

    public void setTelephone(String telephone) {
        this.phone = telephone;
    }

    // For backward compatibility with code that expects an int
    public void setTelephone(int telephone) {
        this.phone = String.valueOf(telephone);
    }

    public String getPhotoUrl() {
        return profile_image;
    }

    public void setPhotoUrl(String photoUrl) {
        this.profile_image = photoUrl;
    }

    public UserRole getRole() {
        return getRoleAsEnum();
    }

    public void setRole(UserRole role) {
        setRoleFromEnum(role);
    }

    public String getStatus() {
        return active ? "active" : "inactive";
    }

    public void setStatus(String status) {
        this.active = "active".equalsIgnoreCase(status);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", first_name='" + first_name + '\'' +
                ", last_name='" + last_name + '\'' +
                ", email='" + email + '\'' +
                ", active=" + active +
                ", created_at=" + created_at +
                ", password='" + password + '\'' +
                ", birthdate=" + birthdate +
                ", phone='" + phone + '\'' +
                ", roles='" + roles + '\'' +
                '}';
    }
}
