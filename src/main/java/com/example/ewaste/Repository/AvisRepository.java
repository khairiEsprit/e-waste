package com.example.ewaste.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.example.ewaste.Entities.Avis;

public class AvisRepository {

    private final Connection conn;

    // Constructor to initialize the connection
    public AvisRepository(Connection conn) {
        this.conn = conn;
    }

    // Create a new Avis (feedback)
    public boolean create(Avis avis) {
        String sql = "INSERT INTO avis (nom, description, note) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, avis.getName());
            stmt.setString(2, avis.getDescription());
            stmt.setInt(3, avis.getRating());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Read all Avis (feedback)
    public List<Avis> readAll() {
        String sql = "SELECT * FROM avis";
        List<Avis> avisList = new ArrayList<>();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Avis avis = new Avis(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("description"),
                        rs.getInt("note")
                );
                avisList.add(avis);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return avisList;
    }

    // Update an existing Avis
    public boolean update(Avis avis) {
        String sql = "UPDATE avis SET nom = ?, description = ?, note = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, avis.getName());
            stmt.setString(2, avis.getDescription());
            stmt.setInt(3, avis.getRating());
            stmt.setInt(4, avis.getId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Delete an Avis by its ID
    public boolean delete(int id) {
        String sql = "DELETE FROM avis WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Find an Avis by ID
    public Avis findById(int id) {
        String sql = "SELECT * FROM avis WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Avis(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("description"),
                        rs.getInt("note")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}

