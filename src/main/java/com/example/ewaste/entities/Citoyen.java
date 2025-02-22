package com.example.ewaste.Entities;

public class Citoyen extends User{

    public Citoyen() {
        super();
        this.setRole(UserRole.CITOYEN);
    }

    // Parameterized constructor that automatically sets the role to CITOYEN
//    public Citoyen( String firstName,String password, String email, Date dNaissance, int tlp) {
//      super(firstName,email,password,dNaissance,tlp,UserRole.CITOYEN, LocalDateTime.now());
//    }

}
