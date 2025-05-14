package com.example.ewaste.Entities;

public enum etat {
    FONCTIONNEL("Fonctionnel"),
    NON_FONCTIONNEL("Non Fonctionnel"),
    EN_REPARATION("En Réparation"),
    HORS_SERVICE("Hors Service");

    private final String label;

    etat(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    // Méthode pour convertir une String en enum (insensible à la casse et aux espaces/underscores)
    public static etat fromString(String value) {
        if (value == null) return HORS_SERVICE;

        String normalized = value.trim().toUpperCase()
                .replace(" ", "_")
                .replace("-", "_");

        try {
            return valueOf(normalized);
        } catch (IllegalArgumentException e) {
            return HORS_SERVICE; // Valeur par défaut si non reconnue
        }
    }
}