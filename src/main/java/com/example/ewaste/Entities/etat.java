
package com.example.ewaste.Entities;

public enum etat {

    FONCTIONNEL("Fonctionnel"),
    EN_PANNE("En panne");

    private final String label;


    etat(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}