package com.example.ewaste.Models;
public enum type {
    REMPLISSAGE("Remplissage"),
    VIDAGE("Vidage"),
    PANNE("Panne"),
    REPARATION("Réparation");

    private final String displayName;

    type(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}