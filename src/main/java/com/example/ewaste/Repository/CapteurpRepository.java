package com.example.ewaste.Repository;

import com.example.ewaste.Entities.PlanificationTache;
import com.example.ewaste.Entities.capteur;
import com.example.ewaste.Entities.capteurp;
import com.example.ewaste.Interfaces.IService;
import com.example.ewaste.Utils.DataBase;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CapteurpRepository implements IService<capteurp> {
    private Connection connection;

    public CapteurpRepository()  {
        this.connection = DataBase.getInstance().getConnection();
    }

    public int choisirPoubelleAleatoire() throws SQLException {
        String query = "SELECT id FROM poubelle ORDER BY RAND() LIMIT 1";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getInt("id");
            } else {
                throw new SQLException("Aucune poubelle trouvée.");
            }
        }
    }

    public void ajouter(capteurp cp) throws SQLException {
        String sql = "INSERT INTO capteurp (id_poubelle, quantite, date_m) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, cp.getId_poubelle());
            ps.setFloat(2, cp.getQuantite());
            ps.setTimestamp(3, cp.getDate_m());
            ps.executeUpdate();
        }
    }


    @Override
    public void supprimer(int id) throws SQLException {
        String sql = "DELETE FROM capteurp WHERE id_c = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();
    }

    @Override
    public List<capteurp> afficher() throws SQLException {
        return List.of();
    }

    @Override
    public List<PlanificationTache> afficher(int id_centre) throws SQLException {
        return List.of();
    }

    @Override
    public void modifier(capteurp cp) throws SQLException {
        try {
            String sql = "UPDATE capteurp SET id_poubelle = ?, quantite = ?, date_m = ? WHERE id_c = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, cp.getId_poubelle());
            ps.setFloat(2, cp.getQuantite());
            ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(4, cp.getId());

            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Le capteur a été modifié avec succès.");
            } else {
                System.out.println("Aucun capteur trouvé avec l'ID spécifié.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la modification du capteur : " + e.getMessage());
            throw e;
        }
    }

    @Override
    public List<PlanificationTache> recuperer() throws SQLException {
        return List.of();
    }

    public List<capteurp> recupererr() throws SQLException {
        String sql = "SELECT * FROM capteurp";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);

        List<capteurp> capteursp = new ArrayList<>();
        while (rs.next()) {
            capteurp cp = new capteurp(
                    rs.getInt("id"),
                    rs.getInt("id_poubelle"),
                    rs.getFloat("quantite"),
                    rs.getTimestamp("date_m")
            );
            capteursp.add(cp);
        }
        return capteursp;
    }
//    public int choisirPoubelleAleatoire() throws SQLException {
//        String sql = "SELECT id_poubelle FROM capteurp";
//        Statement statement = connection.createStatement();
//        ResultSet rs = statement.executeQuery(sql);
//
//        List<Integer> idPoubelles = new ArrayList<>();
//        while (rs.next()) {
//            idPoubelles.add(rs.getInt("id_poubelle"));
//        }
//
//        if (idPoubelles.isEmpty()) {
//            throw new SQLException("Aucune poubelle trouvée dans la base de données.");
//        }
//
//        Random random = new Random();
//        return idPoubelles.get(random.nextInt(idPoubelles.size()));
//    }

    public void mettreAJourQuantiteDechets(int idPoubelle, float quantiteAjoutee) throws SQLException {
        String sqlSelect = "SELECT quantite FROM capteurp WHERE id_poubelle = ?";
        PreparedStatement psSelect = connection.prepareStatement(sqlSelect);
        psSelect.setInt(1, idPoubelle);
        ResultSet rs = psSelect.executeQuery();

        if (rs.next()) {
            float quantiteActuelle = rs.getFloat("quantite");
            float nouvelleQuantite = quantiteActuelle + quantiteAjoutee;

            String sqlUpdate = "UPDATE capteurp SET quantite = ?, date_m = ? WHERE id_poubelle = ?";
            PreparedStatement psUpdate = connection.prepareStatement(sqlUpdate);
            psUpdate.setFloat(1, nouvelleQuantite);
            psUpdate.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            psUpdate.setInt(3, idPoubelle);
            psUpdate.executeUpdate();
        } else {
            throw new SQLException("Aucune poubelle trouvée avec l'ID : " + idPoubelle);
        }
    }
}

