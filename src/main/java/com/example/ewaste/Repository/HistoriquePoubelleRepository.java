package com.example.ewaste.Repository;

import com.example.ewaste.Entities.Historique_Poubelle;
import com.example.ewaste.Entities.type;
import com.example.ewaste.Interfaces.IService;
import com.example.ewaste.Utils.DataBase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class HistoriquePoubelleRepository implements IService<Historique_Poubelle> {
    private Connection connection;

    public HistoriquePoubelleRepository() {
        connection = DataBase.getInstance().getConnection();
    }

    // Vérifie si un enregistrement similaire existe déjà
    private boolean existeEnregistrement(int poubelleId, java.util.Date dateEvenement, type typeEvenement) throws SQLException {
        String sql = "SELECT COUNT(*) FROM historique WHERE poubelle_id = ? AND type_evenement = ? " +
                    "AND ABS(TIMESTAMPDIFF(SECOND, date_evenement, ?)) < 60"; // 60 secondes de différence max
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, poubelleId);
        ps.setString(2, typeEvenement.toString());
        ps.setTimestamp(3, new java.sql.Timestamp(dateEvenement.getTime()));
        ResultSet rs = ps.executeQuery();
        rs.next();
        return rs.getInt(1) > 0;
    }

    // Met à jour l'enregistrement existant au lieu d'en créer un nouveau
    private void mettreAJourEnregistrement(int poubelleId, java.util.Date dateEvenement, type typeEvenement, 
                                       String description, double quantiteDechets) throws SQLException {
        String sql = "UPDATE historique SET quantite_dechets = quantite_dechets + ?, description = ? " +
                    "WHERE poubelle_id = ? AND type_evenement = ? " +
                    "AND ABS(TIMESTAMPDIFF(SECOND, date_evenement, ?)) < 60";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setDouble(1, quantiteDechets);
        ps.setString(2, description);
        ps.setInt(3, poubelleId);
        ps.setString(4, typeEvenement.toString());
        ps.setTimestamp(5, new java.sql.Timestamp(dateEvenement.getTime()));
        ps.executeUpdate();
    }

    public void enregistrerOperation(int poubelleId, type typeOperation, double quantite) throws SQLException {
        // Vérifier d'abord si la poubelle existe
        if (!existePoubelle(poubelleId)) {
            throw new SQLException("La poubelle avec l'ID " + poubelleId + " n'existe pas.");
        }

        // Créer un nouvel enregistrement dans l'historique
        java.util.Date dateEvenement = new java.util.Date();
        String description = "";
        switch (typeOperation) {
            case REMPLISSAGE -> description = "Remplissage détecté";
            case VIDAGE -> description = "Vidage détecté";
            case PANNE -> description = "Panne détectée";
            case REPARATION -> description = "Réparation effectuée";
            case AJOUT_DECHETS -> description = "Ajout de déchets";
        }

        // Vérifier si un enregistrement similaire existe déjà
        if (existeEnregistrement(poubelleId, dateEvenement, typeOperation)) {
            // Mettre à jour l'enregistrement existant au lieu d'en créer un nouveau
            mettreAJourEnregistrement(poubelleId, dateEvenement, typeOperation, description, quantite);
        } else {
            // Créer un nouvel enregistrement
            Historique_Poubelle historique = new Historique_Poubelle(
                poubelleId,
                dateEvenement,
                typeOperation,
                description,
                quantite
            );
            ajouter(historique);
        }
    }

    @Override
    public void ajouter(Historique_Poubelle h) throws SQLException {
        // Vérifier d'abord si un enregistrement similaire existe déjà
        if (existeEnregistrement(h.getPoubelle_id(), h.getDate_evenement(), h.getType_evenement())) {
            String sql = "UPDATE historique SET quantite_dechets = quantite_dechets + ? WHERE poubelle_id = ? AND date_evenement = ? AND type_evenement = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setDouble(1, h.getQuantite_dechets());
            ps.setInt(2, h.getPoubelle_id());
            ps.setTimestamp(3, new Timestamp(h.getDate_evenement().getTime()));
            ps.setString(4, h.getType_evenement().toString());
            ps.executeUpdate();
        } else {
            String sql = "INSERT INTO historique (id, poubelle_id, date_evenement, type_evenement, description, quantite_dechets) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, h.getId());
            ps.setInt(2, h.getPoubelle_id());
            ps.setTimestamp(3, new Timestamp(h.getDate_evenement().getTime()));
            ps.setString(4, h.getType_evenement().toString());
            ps.setString(5, h.getDescription());
            ps.setDouble(6, h.getQuantite_dechets());
            ps.executeUpdate();
        }
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String sql = "DELETE FROM historique WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();
    }

    @Override
    public void modifier(Historique_Poubelle h) throws SQLException {
        String sql = "UPDATE historique SET poubelle_id=?, date_evenement=?, type_evenement=?, description=?, quantite_dechets=? WHERE id=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, h.getPoubelle_id());
        ps.setTimestamp(2, new Timestamp(h.getDate_evenement().getTime()));
        ps.setString(3, h.getType_evenement().toString());
        ps.setString(4, h.getDescription());
        ps.setDouble(5, h.getQuantite_dechets());
        ps.setInt(6, h.getId());
        ps.executeUpdate();
    }

    @Override
    public List<Historique_Poubelle> recuperer() throws SQLException {
        String sql = "SELECT * FROM historique";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);

        List<Historique_Poubelle> historiquePoubelles = new ArrayList<>();
        while (rs.next()) {
            Historique_Poubelle h = new Historique_Poubelle(
                    rs.getInt("id"),
                    rs.getInt("poubelle_id"),
                    rs.getTimestamp("date_evenement"),
                    type.fromString(rs.getString("type_evenement")),
                    rs.getString("description"),
                    rs.getFloat("quantite_dechets")
            );
            historiquePoubelles.add(h);
        }
        return historiquePoubelles;
    }

    public List<Historique_Poubelle> recupererParPoubelle(int idPoubelle) throws SQLException {
        String sql = "SELECT * FROM historique WHERE poubelle_id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, idPoubelle);
        ResultSet rs = ps.executeQuery();

        List<Historique_Poubelle> historiquePoubelles = new ArrayList<>();
        while (rs.next()) {
            Historique_Poubelle h = new Historique_Poubelle(
                    rs.getInt("id"),
                    rs.getInt("poubelle_id"),
                    rs.getTimestamp("date_evenement"),
                    type.fromString(rs.getString("type_evenement")),
                    rs.getString("description"),
                    rs.getFloat("quantite_dechets")
            );
            historiquePoubelles.add(h);
        }
        return historiquePoubelles;
    }

    public List<Historique_Poubelle> recupererParPoubelleEtType(int idPoubelle, type typeEvenement) throws SQLException {
        String sql = "SELECT * FROM historique WHERE poubelle_id = ? AND type_evenement = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, idPoubelle);
        ps.setString(2, typeEvenement.toString());
        ResultSet rs = ps.executeQuery();

        List<Historique_Poubelle> historiquePoubelles = new ArrayList<>();
        while (rs.next()) {
            Historique_Poubelle h = new Historique_Poubelle(
                    rs.getInt("id"),
                    rs.getInt("poubelle_id"),
                    rs.getTimestamp("date_evenement"),
                    type.valueOf(rs.getString("type_evenement")),
                    rs.getString("description"),
                    rs.getFloat("quantite_dechets")
            );
            historiquePoubelles.add(h);
        }
        return historiquePoubelles;
    }

    public boolean existePoubelle(int idPoubelle) throws SQLException {
        String sql = "SELECT COUNT(*) FROM poubelle WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, idPoubelle);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt(1) > 0;
        }
        return false;
    }
}



