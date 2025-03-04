package com.example.ewaste.Entities;

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

    @Override
    public String toString() {
        return displayName;
    }

    // Méthode pour convertir une chaîne en enum
    public static type fromString(String text) {
        for (type t : type.values()) {
            if (t.displayName.equalsIgnoreCase(text)) {
                return t;
            }
        }
        throw new IllegalArgumentException("Aucun enum correspondant trouvé pour : " + text);
    }
}