package com.example.ewaste;


import com.example.ewaste.entities.Demande;
import com.example.ewaste.entities.Traitement;
import com.example.ewaste.repository.DemandeRepository;
import com.example.ewaste.repository.TraitementRepository;


import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        DemandeRepository demandeRepository = new DemandeRepository();
        TraitementRepository traitementRepository = new TraitementRepository();

        try {
            // CRUD operations for Demande

            // Add a new Demande
//            Demande newDemande = new Demande(1, 1, "Ariana", "houssem@example.com", "This is a test houssem", "Type1");
//            demandeRepository.ajouter(newDemande);
//            System.out.println("Demande added successfully!");

            // Modify an existing Demande
//            Demande updatedDemande = new Demande(2, 3, 2, "Ariana Soghra", "updateduser@example.com", "Updated message", "Type2");
//            demandeRepository.modifier(updatedDemande);
//            System.out.println("Demande updated successfully!");
//
//            // Delete a Demande by ID
//            int idToDelete = 2;
//            demandeRepository.supprimer(idToDelete);
//            System.out.println("Demande deleted successfully!");

            // Fetch and display all Demandes
            List<Demande> demandes = demandeRepository.afficher();
            System.out.println("List of Demandes:");
            for (Demande demande : demandes) {
                System.out.println(demande);
            }

            // CRUD operations for Traitement

            // Add a new Traitement
//            Traitement newTraitement = new Traitement(4, "Pending", LocalDateTime.now(), "Initial processing of demande");
//            traitementRepository.ajouter(newTraitement);
//            System.out.println("Traitement added successfully!");

            //Modify an existing Traitement
            Traitement updatedTraitement = new Traitement(4, 4, "Completed", LocalDateTime.now(), "Demande has been completed");
            traitementRepository.modifier(updatedTraitement);
            System.out.println("Traitement updated successfully!");

            // Delete a Traitement by ID
//            int idTraitementToDelete = 1;
//            traitementRepository.supprimer(idTraitementToDelete);
//            System.out.println("Traitement deleted successfully!");

            // Fetch and display all Traitements
            List<Traitement> traitements = traitementRepository.afficher();
            System.out.println("List of Traitements:");
            for (Traitement traitement : traitements) {
                System.out.println(traitement);
            }

        } catch (SQLException e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }
}
