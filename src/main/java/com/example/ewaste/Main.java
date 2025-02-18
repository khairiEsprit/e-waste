package com.example.ewaste;

import com.example.ewaste.Models.Historique_Poubelle;
import com.example.ewaste.Models.etat;
import com.example.ewaste.Models.poubelle;
import com.example.ewaste.Models.type;
import com.example.ewaste.repository.HistoriquePoubelleRepository;
import com.example.ewaste.repository.PoubelleRepository;

import java.sql.SQLException;

import java.util.Date;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        /*try {
            PoubelleRepository service = new PoubelleRepository();
            HistoriquePoubelleRepository service_h = new HistoriquePoubelleRepository();


            // Créer une nouvelle poubelle

            poubelle p = new poubelle(9, "90000 Rue de zarzis", 100, etat.FONCTIONNEL, new Date());

            poubelle p2 = new poubelle(7, "8000 Rue de Arina", 0, etat.EN_PANNE, new Date());

            poubelle p3= new poubelle(10, "200 esprit", 100, etat.FONCTIONNEL, new Date());
            poubelle p4 = new poubelle(6, "353 Actia", 25, etat.FONCTIONNEL, new Date());
            // Ajouter la poubelle
            service.ajouter(p);
            service.ajouter(p2);
            service.ajouter(p3);
            service.ajouter(p4);
            System.out.println("Poubelle ajoutée : " + p);
            //Modifier la poubelle
            service.modifier(new poubelle(17,10, "456 Avenue de Lyon", 50, etat.FONCTIONNEL, new Date()));

            // Récupérer et afficher toutes les poubelles
            List<poubelle> poubelles = service.recuperer();
            for (poubelle pb : poubelles) {
                System.out.println(pb);
            }

            // Supprimer la poubelle
            service.supprimer(4);
            //HISTORIQUE*****************************************************************************
            Historique_Poubelle h = new Historique_Poubelle(145,new Date(), type.REMPLISSAGE,"la pubelle est rempli",45.5f);
            Historique_Poubelle h1= new Historique_Poubelle(144,new Date(),type.REPARATION,"la pubelle est en coure de Reparation ",0.0f);
            Historique_Poubelle h2= new Historique_Poubelle(149,new Date(),type.PANNE,"la pubelle est en panne",0.0f);
            Historique_Poubelle h3= new Historique_Poubelle(151,new Date(),type.VIDAGE,"la pubelle est vide",00.0f);

            service_h.ajouter(h);
            service_h.ajouter(h1);
            service_h.ajouter(h2);
            service_h.ajouter(h3);
            System.out.println("Événement ajouté : " + h);
            h.setDescription("Vidage complet de la poubelle");
            service_h.modifier(h);
            System.out.println("Événement modifié : " + h);
            // Récupérer tous les événements
            List<Historique_Poubelle> historiquePoubelles = service_h.recuperer();
            for (Historique_Poubelle hp : historiquePoubelles) {
                System.out.println(hp);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }*/
    }
}