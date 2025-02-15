package com.example.ewaste.repository;

import com.example.ewaste.Models.Historique_Poubelle;
import com.example.ewaste.Models.type;
import com.example.ewaste.interfaces.IService;
import com.example.ewaste.utils.DataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceHistoriquePoubelle implements IService<Historique_Poubelle> {
    private Connection connection;

    public ServiceHistoriquePoubelle() {
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
    public List<Historique_Poubelle> recuperer() throws SQLException {
        String sql = "SELECT * FROM historique";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);

        List<Historique_Poubelle> historiquePoubelles = new ArrayList<>();
        while (rs.next()) {
            Historique_Poubelle h = new Historique_Poubelle(
                    rs.getInt("id"),
                    rs.getInt("id_poubelle"),
                    rs.getTimestamp("date_evenement"), // Récupérer Timestamp et le convertir en Date
                    type.valueOf(rs.getString("type_evenement")), // Convertir String en enum
                    rs.getString("description"),
                    rs.getFloat("quantite_dechets")
            );
            historiquePoubelles.add(h);
        }
        return historiquePoubelles;
    }
    }



