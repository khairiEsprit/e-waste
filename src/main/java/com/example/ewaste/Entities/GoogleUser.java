package com.example.ewaste.Entities;

public class GoogleUser {
    private String sub;
    private String name;
    private String givenName;
    private String familyName;
    private String picture;
    private String email;
    private boolean emailVerified;

    public GoogleUser(String sub, String name, String givenName, String familyName, String picture, String email, boolean emailVerified) {
        this.sub = sub;
        this.name = name;
        this.givenName = givenName;
        this.familyName = familyName;
        this.picture = picture;
        this.email = email;
        this.emailVerified = emailVerified;
    }

    // Getters and Setters
    public String getSub() { return sub; }
    public String getName() { return name; }
    public String getGivenName() { return givenName; }
    public String getFamilyName() { return familyName; }
    public String getPicture() { return picture; }
    public String getEmail() { return email; }
    public boolean isEmailVerified() { return emailVerified; }
}
