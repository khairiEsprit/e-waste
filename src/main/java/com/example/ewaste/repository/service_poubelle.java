package com.example.ewaste.repository;

import com.example.ewaste.Models.etat;
import com.example.ewaste.Models.poubelle;
import com.example.ewaste.interfaces.IService;
import com.example.ewaste.utils.DataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class service_poubelle implements IService<poubelle>
{

    private Connection connection;

    public service_poubelle() {
        connection = DataBase.getInstance().getConnection();
    }
    @Override
    public void ajouter(poubelle p) throws SQLException {
        String sql = "INSERT INTO poubelle (id_centre, adresse, niveau, etat, date_installation) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, p.getId_centre());
        ps.setString(2, p.getAdresse());
        ps.setInt(3, p.getNiveau());
        ps.setString(4, p.getEtat().toString()); // Convertir l'énumération en String
        ps.setDate(5, new java.sql.Date(p.getDate_installation().getTime()));
        ps.executeUpdate();
    }


    @Override
    public void supprimer(int id) throws SQLException {
        String sql = "DELETE FROM poubelle WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();
    }

    @Override
    public void modifier(poubelle p) throws SQLException {
        try {
            String sql = "UPDATE poubelle SET id_centre = ?, adresse = ?, niveau = ?, etat = ?, date_installation = ? WHERE id = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, p.getId_centre());
            ps.setString(2, p.getAdresse());
            ps.setInt(3, p.getNiveau());
            ps.setString(4, p.getEtat().toString());
            ps.setDate(5, new java.sql.Date(p.getDate_installation().getTime()));
            ps.setInt(6, p.getId());

            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("La poubelle a été modifiée avec succès.");
            } else {
                System.out.println("Aucune poubelle trouvée avec l'ID spécifié.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la modification de la poubelle : " + e.getMessage());
            throw e;
        }
    }


    @Override
    public List<poubelle> recuperer() throws SQLException {
        String sql = "SELECT * FROM poubelle";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);

        List<poubelle> poubelles = new ArrayList<>();
        while (rs.next()) {
            poubelle p = new poubelle(
                    rs.getInt("id_centre"),
                    rs.getString("adresse"),
                    rs.getInt("niveau"),
                    etat.valueOf(rs.getString("etat")),
                    rs.getDate("date_installation")
            );
            p.setId(rs.getInt("id"));
            poubelles.add(p);

        }
        return poubelles;
    }
    }


