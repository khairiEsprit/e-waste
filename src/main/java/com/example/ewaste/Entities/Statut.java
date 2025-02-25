package com.example.ewaste.Entities;

public enum Statut {
        Envoyee("Envoyee"),
        Lue("Lue");

    private final String displayName;
    Statut(String displayName) {
        this.displayName = displayName;
    }



    public String getDisplayName() {
        return displayName;
    }
}
