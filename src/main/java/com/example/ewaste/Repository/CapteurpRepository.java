package com.example.ewaste.Repository;

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

    public CapteurpRepository() {
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

    @Override
    public void ajouter(capteurp cp) throws SQLException {
        String sql = "INSERT INTO capteurp (poubelle_id, quantite, date_m) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, cp.getPoubelle_id());
            ps.setDouble(2, cp.getQuantite());
            ps.setTimestamp(3, cp.getDate_m());
            ps.executeUpdate();
        }
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String sql = "DELETE FROM capteurp WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();
    }

    @Override
    public void modifier(capteurp cp) throws SQLException {
        try {
            String sql = "UPDATE capteurp SET poubelle_id = ?, quantite = ?, date_m = ? WHERE id = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, cp.getPoubelle_id());
            ps.setDouble(2, cp.getQuantite());
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
    public List<capteurp> recuperer() throws SQLException {
        String sql = "SELECT * FROM capteurp";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);

        List<capteurp> capteursp = new ArrayList<>();
        while (rs.next()) {
            capteurp cp = new capteurp(
                rs.getInt("id"),
                rs.getInt("poubelle_id"),
                rs.getDouble("quantite"),
                rs.getTimestamp("date_m")
            );
            capteursp.add(cp);
        }
        return capteursp;
    }

    public void mettreAJourQuantiteDechets(int idPoubelle, double quantiteAjoutee) throws SQLException {
        String sqlSelect = "SELECT quantite FROM capteurp WHERE poubelle_id = ?";
        PreparedStatement psSelect = connection.prepareStatement(sqlSelect);
        psSelect.setInt(1, idPoubelle);
        ResultSet rs = psSelect.executeQuery();

        if (rs.next()) {
            double quantiteActuelle = rs.getDouble("quantite");
            double nouvelleQuantite = quantiteActuelle + quantiteAjoutee;

            String sqlUpdate = "UPDATE capteurp SET quantite = ?, date_m = ? WHERE poubelle_id = ?";
            PreparedStatement psUpdate = connection.prepareStatement(sqlUpdate);
            psUpdate.setDouble(1, nouvelleQuantite);
            psUpdate.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            psUpdate.setInt(3, idPoubelle);
            psUpdate.executeUpdate();
        } else {
            throw new SQLException("Aucune poubelle trouvée avec l'ID : " + idPoubelle);
        }
    }
}

