package com.example.ewaste.Repository;
import com.example.ewaste.Entities.capteur;
import com.example.ewaste.Entities.poubelle;
import com.example.ewaste.Interfaces.IService;
import com.example.ewaste.Utils.DataBase;

import java.time.LocalDateTime;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CapteurRepository implements IService<capteur> {
    private Connection connection;

    public CapteurRepository (){
        connection = DataBase.getInstance().getConnection();
    }
    @Override
    public void ajouter(capteur c) throws SQLException {
        // 1. Enregistrer la mesure du capteur
        String sql = "INSERT INTO capteur (id_poubelle, distance_mesuree, date_mesure) VALUES (?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, c.getId_poubelle());
        ps.setFloat(2, c.getDistance_mesuree());
        ps.setTimestamp(3,  Timestamp.valueOf(LocalDateTime.now()));
        ps.executeUpdate();

        // 2. Calculer et mettre à jour le niveau de la poubelle
        PoubelleRepository poubelleRepo = new PoubelleRepository();
        poubelle p = poubelleRepo.getById(c.getId_poubelle());

        if (p != null) {
            int niveau = (int) ((p.getHauteurTotale() - c.getDistance_mesuree()) / p.getHauteurTotale() * 100);
            niveau = Math.max(0, Math.min(100, niveau)); // Contrainte 0-100%

            p.setNiveau(niveau);
            poubelleRepo.modifier(p);

            // 3. Vérifier les seuils et notifier
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

    @Override
    public void supprimer(int id_c) throws SQLException {
        String sql = "DELETE FROM capteur WHERE id_c = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id_c);
        ps.executeUpdate();
    }

    @Override
    public void modifier(capteur c) throws SQLException {
        try {
            String sql = "UPDATE capteur SET  id_poubelle =?, distance_mesuree =? ,date_mesure = ? WHERE id_c= ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, c.getId_poubelle());
            ps.setFloat(2, c.getDistance_mesuree());
            ps.setTimestamp(3,  Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(4,c.getId_c());


            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Le capteur a été modifiée avec succès.");
            } else {
                System.out.println("Aucun capteur trouvée avec l'ID spécifié.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la modification de capteur : " + e.getMessage());
            throw e;
        }
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
                    rs.getInt("id_poubelle"),
                    rs.getFloat("Distance_mesuree"),
                    rs.getTimestamp("Date_mesure"),
                    rs.getFloat("porteeMaximale"),
                    rs.getFloat("precision")
            );
            c.setDistance_mesuree(rs.getInt("id_c"));
            capteurs.add(c);

        }
        return capteurs;
    }
}
