package com.example.ewaste.repository;

import com.example.ewaste.entities.Centre;
import com.example.ewaste.interfaces.IService;
import com.example.ewaste.utils.DataBase;

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

    public boolean existeCentre(Float longitude,Float altitude) throws SQLException {
        String sql = "SELECT * FROM `centre` WHERE `longitude` = ? AND `altitude` = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setFloat(1, longitude);
            ps.setFloat(1, altitude);  // Si vous voulez tester la longitude/altitude, vous devrez ajuster la requête ici.
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
}
