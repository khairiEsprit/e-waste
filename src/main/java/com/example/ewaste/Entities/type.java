package com.example.ewaste.Entities;

public enum type {
    REMPLISSAGE("Remplissage"),
    VIDAGE("Vidage"),
    PANNE("Panne"),
    REPARATION("Réparation"),
    AJOUT_DECHETS("Ajout de déchets");

    private final String label;

    type(String label) {
        this.label = label;
    }

    public String getLabel() {
        return this.label;
    }

    public static type fromString(String text) {
        for (type t : type.values()) {
            if (t.label.equalsIgnoreCase(text) || t.name().equalsIgnoreCase(text)) {
                return t;
            }
        }
        throw new IllegalArgumentException("Aucun type trouvé avec le texte: " + text);
    }

    @Override
    public String toString() {
        return this.label;
    }
}