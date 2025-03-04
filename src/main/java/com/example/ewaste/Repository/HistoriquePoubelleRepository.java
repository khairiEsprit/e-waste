package com.example.ewaste.Repository;

import com.example.ewaste.Entities.Historique_Poubelle;
import com.example.ewaste.Entities.PlanificationTache;
import com.example.ewaste.Entities.type;
import com.example.ewaste.Interfaces.IService;
import com.example.ewaste.Utils.DataBase;

import java.sql.Connection;
import java.sql.*;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class HistoriquePoubelleRepository implements IService<Historique_Poubelle> {
    private Connection connection;

    public HistoriquePoubelleRepository() {
        connection = DataBase.getInstance().getConnection();
    }

    @Override
    public void ajouter(Historique_Poubelle h) throws SQLException {
        String sql = "INSERT INTO historique ( id_poubelle, date_evenement, type_evenement, description,quantite_dechets)  VALUES ( ?, ?, ?, ?,?)";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, h.getId_poubelle());
        ps.setTimestamp(2, new Timestamp(h.getDate_evenement().getTime())); // Convertir Date en Timestamp
        ps.setString(3, h.getType_evenement().toString()); // Convertir l'énumération en String
        ps.setString(4, h.getDescription());
        ps.setFloat(5, h.getQuantite_dechets());
        ps.executeUpdate();
    }


    @Override
    public void supprimer(int id) throws SQLException {
        String sql = "DELETE FROM historique WHERE  id= ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();
    }

    @Override
    public List<Historique_Poubelle> afficher() throws SQLException {
        return List.of();
    }

    @Override
    public List<PlanificationTache> afficher(int id_centre) throws SQLException {
        return List.of();
    }

    @Override
    public void modifier(Historique_Poubelle h) throws SQLException {
        String sql = "UPDATE historique SET id_poubelle=?,date_evenement=?,type_evenement=?,description=?,quantite_dechets=? WHERE id=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, h.getId_poubelle());
        ps.setTimestamp(2, new Timestamp(h.getDate_evenement().getTime())); // Convertir Date en Timestamp
        ps.setString(3, h.getType_evenement().toString()); // Convertir l'énumération en String
        ps.setString(4, h.getDescription());
        ps.setFloat(5, h.getQuantite_dechets());
        ps.setInt(6, h.getId());
        ps.executeUpdate();
    }

    @Override
    public List<PlanificationTache> recuperer() throws SQLException {
        return List.of();
    }

    public List<Historique_Poubelle> recupererr() throws SQLException {
        String sql = "SELECT * FROM historique";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);

        List<Historique_Poubelle> historiquePoubelles = new ArrayList<>();
        while (rs.next()) {
            String typeEvenementStr = rs.getString("type_evenement");
            type typeEvenement;

            // Convertir la chaîne en enum
            switch (typeEvenementStr.toUpperCase()) {
                case "REMPLISSAGE":
                    typeEvenement = type.REMPLISSAGE;
                    break;
                case "VIDAGE":
                    typeEvenement = type.VIDAGE;
                    break;
                case "PANNE":
                    typeEvenement = type.PANNE;
                    break;
                case "REPARATION":
                    typeEvenement = type.REPARATION;
                    break;
                default:
                    throw new IllegalArgumentException("Type d'événement inconnu : " + typeEvenementStr);
            }

            Historique_Poubelle h = new Historique_Poubelle(
                    rs.getInt("id"),
                    rs.getInt("id_poubelle"),
                    rs.getTimestamp("date_evenement"),
                    typeEvenement,
                    rs.getString("description"),
                    rs.getFloat("quantite_dechets")
            );
            historiquePoubelles.add(h);
        }
        return historiquePoubelles;
    }
    public List<Historique_Poubelle> recupererParPoubelle(int idPoubelle) throws SQLException {
        String sql = "SELECT * FROM historique WHERE id_poubelle = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, idPoubelle);
        ResultSet rs = ps.executeQuery();

        List<Historique_Poubelle> historiquePoubelles = new ArrayList<>();
        while (rs.next()) {
            Historique_Poubelle h = new Historique_Poubelle(
                    rs.getInt("id"),
                    rs.getInt("id_poubelle"),
                    rs.getTimestamp("date_evenement"),
                    type.fromString(rs.getString("type_evenement")), // Utiliser fromString
                    rs.getString("description"),
                    rs.getFloat("quantite_dechets")
            );
            historiquePoubelles.add(h);
        }
        return historiquePoubelles;
    }

    public List<Historique_Poubelle> recupererParPoubelleEtType(int idPoubelle, type typeEvenement) throws SQLException {
        String sql = "SELECT * FROM historique WHERE id_poubelle = ? AND type_evenement = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, idPoubelle);
        ps.setString(2, typeEvenement.toString());
        ResultSet rs = ps.executeQuery();

        List<Historique_Poubelle> historiquePoubelles = new ArrayList<>();
        while (rs.next()) {
            Historique_Poubelle h = new Historique_Poubelle(
                    rs.getInt("id"),
                    rs.getInt("id_poubelle"),
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
            return rs.getInt(1) > 0; // Retourne true si la poubelle existe
        }
        return false;
    }

    }



