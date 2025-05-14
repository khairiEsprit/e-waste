package com.example.ewaste.Repository;

import com.example.ewaste.Entities.capteur_pression;
import com.example.ewaste.Entities.type;
import com.example.ewaste.Interfaces.IService;
import com.example.ewaste.Utils.DataBase;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CapteurPressionRepository implements IService<capteur_pression> {
    private Connection connection;

    public CapteurPressionRepository() {
        connection = DataBase.getInstance().getConnection();
    }

    @Override
    public void ajouter(capteur_pression cp) throws SQLException {
        String sql = "INSERT INTO capteur_pression (poubelle_id, poids_mesure, date_mesure, " +
                    "precision_capteur, en_service) VALUES (?, ?, ?, ?, ?)";
        
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, cp.getPoubelle_id());
        ps.setDouble(2, cp.getPoids_mesure());
        ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
        ps.setDouble(4, cp.getPrecision_capteur());
        ps.setBoolean(5, cp.isEn_service());
        ps.executeUpdate();

        // Vérifier si c'est une opération de vidage ou de remplissage
        verifierOperation(cp);
    }

    private void verifierOperation(capteur_pression cp) throws SQLException {
        // Récupérer la dernière mesure pour cette poubelle
        String sql = "SELECT poids_mesure FROM capteur_pression WHERE poubelle_id = ? " +
                    "ORDER BY date_mesure DESC LIMIT 1 OFFSET 1";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, cp.getPoubelle_id());
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            double ancienPoids = rs.getDouble("poids_mesure");
            double difference = cp.getPoids_mesure() - ancienPoids;

            // Déterminer le type d'opération
            type operationType;
            if (Math.abs(difference) < cp.getPrecision_capteur()) {
                return; // Pas de changement significatif
            } else if (difference > 0) {
                operationType = type.REMPLISSAGE;
            } else {
                operationType = type.VIDAGE;
            }

            // Enregistrer l'opération dans l'historique
            HistoriquePoubelleRepository histRepo = new HistoriquePoubelleRepository();
            histRepo.enregistrerOperation(cp.getPoubelle_id(), operationType, Math.abs(difference));
        }
    }

    @Override
    public void supprimer(int id_cp) throws SQLException {
        String sql = "DELETE FROM capteur_pression WHERE id_cp = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id_cp);
        ps.executeUpdate();
    }

    @Override
    public void modifier(capteur_pression cp) throws SQLException {
        String sql = "UPDATE capteur_pression SET poubelle_id = ?, poids_mesure = ?, " +
                    "date_mesure = ?, precision_capteur = ?, en_service = ? WHERE id_cp = ?";
        
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, cp.getPoubelle_id());
        ps.setDouble(2, cp.getPoids_mesure());
        ps.setTimestamp(3, cp.getDate_mesure());
        ps.setDouble(4, cp.getPrecision_capteur());
        ps.setBoolean(5, cp.isEn_service());
        ps.setInt(6, cp.getId_cp());
        ps.executeUpdate();
    }

    @Override
    public List<capteur_pression> recuperer() throws SQLException {
        String sql = "SELECT * FROM capteur_pression";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);

        List<capteur_pression> capteurs = new ArrayList<>();
        while (rs.next()) {
            capteur_pression cp = new capteur_pression(
                rs.getInt("id_cp"),
                rs.getInt("poubelle_id"),
                rs.getDouble("poids_mesure"),
                rs.getTimestamp("date_mesure"),
                rs.getDouble("precision_capteur"),
                rs.getBoolean("en_service")
            );
            capteurs.add(cp);
        }
        return capteurs;
    }
}
