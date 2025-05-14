package com.example.ewaste.Repository;
import com.example.ewaste.Entities.PlanificationTache;
import com.example.ewaste.Entities.capteur;
import com.example.ewaste.Entities.etat;
import com.example.ewaste.Entities.poubelle;
import com.example.ewaste.Interfaces.IService;
import com.example.ewaste.Utils.DataBase;

import java.time.LocalDateTime;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CapteurRepository implements IService<capteur> {
    private Connection connection;

    public CapteurRepository (){
        connection = DataBase.getInstance().getConnection();
    }    @Override
    public void ajouter(capteur c) throws SQLException {
        // First try to find if there's an existing record for this poubelle
        String checkSql = "SELECT id_c FROM capteur WHERE poubelle_id = ?";
        PreparedStatement checkPs = connection.prepareStatement(checkSql);
        checkPs.setInt(1, c.getPoubelle_id());
        ResultSet rs = checkPs.executeQuery();

        if (rs.next()) {
            // Record exists, update it
            String updateSql = "UPDATE capteur SET distance_mesuree = ?, date_mesure = ?, " +
                             "portee_maximale = ?, precision_capteur = ? WHERE poubelle_id = ?";
            PreparedStatement updatePs = connection.prepareStatement(updateSql);
            updatePs.setDouble(1, c.getDistance_mesuree());
            updatePs.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            updatePs.setDouble(3, c.getPortee_maximale());
            updatePs.setDouble(4, c.getPrecision_capteur());
            updatePs.setInt(5, c.getPoubelle_id());
            updatePs.executeUpdate();
        } else {
            // Record doesn't exist, insert new one
            String insertSql = "INSERT INTO capteur (poubelle_id, distance_mesuree, date_mesure, " +
                             "portee_maximale, precision_capteur) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement insertPs = connection.prepareStatement(insertSql);
            insertPs.setInt(1, c.getPoubelle_id());
            insertPs.setDouble(2, c.getDistance_mesuree());
            insertPs.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            insertPs.setDouble(4, c.getPortee_maximale());
            insertPs.setDouble(5, c.getPrecision_capteur());
            insertPs.executeUpdate();
        }

        // Mettre à jour le niveau de la poubelle
        PoubelleRepository poubelleRepo = new PoubelleRepository();
        poubelle p = poubelleRepo.getById(c.getPoubelle_id());
          if (p != null) {
            int niveau = (int) ((p.getHauteurTotale() - c.getDistance_mesuree())
                               / p.getHauteurTotale() * 100);
            niveau = Math.max(0, Math.min(100, niveau));

            p.setNiveau(niveau);
            poubelleRepo.modifier(p);
            checkSeuils(p);
        }
    }

    private void checkSeuils(poubelle p) {
        if (p.getNiveau() >= 100) {
            System.out.println("🚨 Alerte : Poubelle " + p.getId() + " est pleine !");
        } else if (p.getNiveau() >= 80) {
            System.out.println("📩 Notification : Poubelle " + p.getId() + " à " + p.getNiveau() + "%");
        }
    }

    // Méthode pour simuler la mesure du niveau de remplissage
    public void simulerMesureNiveauRemplissage() throws SQLException {
        PoubelleRepository poubelleRepo = new PoubelleRepository();
        List<poubelle> poubelles = poubelleRepo.recuperer();

        if (poubelles.isEmpty()) {
            System.out.println("Aucune poubelle trouvée dans la base de données.");
            return;
        }

        // Filtrer les poubelles fonctionnelles
        List<poubelle> poubellesFonctionnelles = poubelles.stream()
                .filter(p -> p.getEtat() == etat.FONCTIONNEL) // Assurez-vous que l'énumération `etat` contient "Fonctionnel"
                .toList();

        if (poubellesFonctionnelles.isEmpty()) {
            System.out.println("Aucune poubelle fonctionnelle trouvée.");
            return;
        }

        Random random = new Random();
        poubelle poubelleAleatoire = poubellesFonctionnelles.get(random.nextInt(poubellesFonctionnelles.size()));

        int hauteurTotale = poubelleAleatoire.getHauteurTotale();
        float distanceMesuree = random.nextFloat() * hauteurTotale;        capteur nouvelleMesure = new capteur();
        nouvelleMesure.setPoubelle_id(poubelleAleatoire.getId());
        nouvelleMesure.setDistance_mesuree(distanceMesuree);
        nouvelleMesure.setDate_mesure(Timestamp.valueOf(LocalDateTime.now()));

        // Ajouter la mesure dans la base de données
        ajouter(nouvelleMesure);

        // Afficher le niveau de remplissage
        int niveauRemplissage = (int) ((hauteurTotale - distanceMesuree) / hauteurTotale * 100);
        System.out.println("Poubelle ID: " + poubelleAleatoire.getId() +
                ", Niveau de remplissage: " + niveauRemplissage + "%");
    }

    @Override
    public void supprimer(int id_c) throws SQLException {
        String sql = "DELETE FROM capteur WHERE id_c = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id_c);
        ps.executeUpdate();
    }

    @Override
    public List<capteur> afficher() throws SQLException {
        return List.of();
    }

    @Override
    public List<PlanificationTache> afficher(int id_centre) throws SQLException {
        return List.of();
    }

    @Override
    public void modifier(capteur c) throws SQLException {
        String sql = "UPDATE capteur SET poubelle_id = ?, distance_mesuree = ?, " +
                     "date_mesure = ?, portee_maximale = ?, precision_capteur = ? " +
                     "WHERE id_c = ?";

        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, c.getPoubelle_id());
        ps.setDouble(2, c.getDistance_mesuree());
        ps.setTimestamp(3, c.getDate_mesure());
        ps.setDouble(4, c.getPortee_maximale());
        ps.setDouble(5, c.getPrecision_capteur());
        ps.setInt(6, c.getId_c());

        ps.executeUpdate();
    }

    @Override
    public List<capteur> recuperer() throws SQLException {
        String sql = "SELECT * FROM capteur";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);

        List<capteur> capteurs = new ArrayList<>();
        while (rs.next()) {
            capteur c = new capteur(
                rs.getInt("id_c"),
                rs.getInt("poubelle_id"),
                rs.getDouble("distance_mesuree"),
                rs.getTimestamp("date_mesure"),
                rs.getDouble("portee_maximale"),
                rs.getDouble("precision_capteur")
            );
            capteurs.add(c);
        }
        return capteurs;
    }

    public double getPorteeMaximale() throws SQLException {
        String sql = "SELECT portee_maximale FROM capteur LIMIT 1";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);

        if (rs.next()) {
            double porteeMaximale = rs.getDouble("portee_maximale");
            // If the value from the database is 0 or negative, return the default value
            if (porteeMaximale <= 0) {
                return 150.0; // Valeur par défaut
            }
            return porteeMaximale;
        }
        return 150.0; // Valeur par défaut si aucun capteur n'est trouvé
    }
}
