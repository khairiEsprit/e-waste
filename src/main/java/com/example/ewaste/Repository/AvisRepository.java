package com.example.ewaste.Repository;

import com.example.ewaste.Entities.Avis;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AvisRepository {

    private final Connection conn;

    // Constructor to initialize the connection
    public AvisRepository(Connection conn) {
        this.conn = conn;
    }

    // Create a new Avis (feedback)
    public boolean create(Avis avis) throws SQLException {
        if (avis.getName() == null || avis.getName().isEmpty()) {
            throw new IllegalArgumentException("Le nom de l'avis ne peut pas être vide");
        }
        if (avis.getDescription() == null || avis.getDescription().isEmpty()) {
            throw new IllegalArgumentException("La description de l'avis ne peut pas être vide");
        }
        if (avis.getRating() < 1 || avis.getRating() > 5) {
            throw new IllegalArgumentException("La note doit être comprise entre 1 et 5");
        }

        // Vérifier si un avis avec les mêmes données existe déjà
        if (exists(avis)) {
            throw new SQLException("Un avis avec les mêmes coordonnées existe déjà.");
        }

        String sql = "INSERT INTO avis (nom, description, note) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, avis.getName());
            stmt.setString(2, avis.getDescription());
            stmt.setInt(3, avis.getRating());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new SQLException("Erreur lors de la création de l'avis", e);
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
    public boolean update(Avis avis) throws SQLException {
        if (findById(avis.getId()) == null) {
            throw new IllegalArgumentException("L'avis avec l'ID " + avis.getId() + " n'existe pas");
        }

        String sql = "UPDATE avis SET nom = ?, description = ?, note = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, avis.getName());
            stmt.setString(2, avis.getDescription());
            stmt.setInt(3, avis.getRating());
            stmt.setInt(4, avis.getId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new SQLException("Erreur lors de la mise à jour de l'avis", e);
        }
    }

    // Delete an Avis by its ID
    public boolean delete(int id) throws SQLException {
        if (findById(id) == null) {
            throw new IllegalArgumentException("L'avis avec l'ID " + id + " n'existe pas");
        }

        String sql = "DELETE FROM avis WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new SQLException("Erreur lors de la suppression de l'avis", e);
        }
    }

    // Find an Avis by ID
    public Avis findById(int id) {
        String sql = "SELECT * FROM avis WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Avis(
                            rs.getInt("id"),
                            rs.getString("nom"),
                            rs.getString("description"),
                            rs.getInt("note")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Vérifier si un avis avec les mêmes données existe déjà
    public boolean exists(Avis avis) throws SQLException {
        String sql = "SELECT COUNT(*) FROM avis WHERE nom = ? AND description = ? AND note = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, avis.getName());
            stmt.setString(2, avis.getDescription());
            stmt.setInt(3, avis.getRating());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0; // Retourne true si un avis existe déjà
                }
            }
        }
        return false;
    }
}