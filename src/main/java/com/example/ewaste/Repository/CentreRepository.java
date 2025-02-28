package com.example.ewaste.Repository;

import com.example.ewaste.Entities.Centre;
import com.example.ewaste.Interfaces.IService;
import com.example.ewaste.Utils.DataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CentreRepository implements IService<Centre> {

    private Connection connection;

    public CentreRepository() {
        connection = DataBase.getInstance().getConnection();
    }

    @Override
    public void ajouter(Centre centre) throws SQLException {
        String sql = "INSERT INTO `centre`(`nom`, `longitude`, `altitude`, `telephone`, `email`) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, centre.getNom());
            statement.setFloat(2, centre.getLongitude());
            statement.setFloat(3, centre.getLatitude());
            statement.setInt(4, centre.getTelephone());
            statement.setString(5, centre.getEmail());
            statement.executeUpdate();
        }
    }

    @Override
    public void modifier(Centre centre) throws SQLException {
        String sql = "UPDATE `centre` SET `nom`=?, `longitude`=?, `altitude`=?, `telephone`=?, `email`=? WHERE `id`=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, centre.getNom());
            ps.setFloat(2, centre.getLongitude());
            ps.setFloat(3, centre.getLatitude());
            ps.setInt(4, centre.getTelephone());
            ps.setString(5, centre.getEmail());
            ps.setInt(6, centre.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String sql = "DELETE FROM `centre` WHERE `id`=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public List<Centre> afficher() throws SQLException {
        List<Centre> centres = new ArrayList<>();
        String sql = "SELECT * FROM `centre`";
        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {
            while (rs.next()) {
                centres.add(new Centre(rs.getInt("id"), rs.getString("nom"),
                        rs.getFloat("longitude"), rs.getFloat("altitude"),
                        rs.getInt("telephone"), rs.getString("email")));
            }
        }
        return centres;
    }

    public boolean existeCentre(float longitude, float altitude) throws SQLException {
        String query = "SELECT COUNT(*) FROM centre WHERE longitude = ? AND altitude = ?";

        try (Connection conn = DataBase.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setFloat(1, longitude);
            stmt.setFloat(2, altitude);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    public Centre getCentreById(int id) {
        Centre centre = null;
        try {
            String query = "SELECT nom, latitude, longitude FROM centre WHERE id = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                centre = new Centre(rs.getString("nom"), rs.getFloat("altitude"), rs.getFloat("longitude")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return centre;
    }

}
