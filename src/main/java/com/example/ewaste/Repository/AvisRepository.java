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

        String sql = "INSERT INTO avis (user_id, nom, avis, description, image, audio_file, video_file, media_type, note, created_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, avis.getUserId()); // Can be null
            stmt.setString(2, avis.getName());
            stmt.setString(3, avis.getDescription()); // Using description as 'avis' field
            stmt.setString(4, avis.getDescription()); // Also set as description field
            stmt.setString(5, avis.getImage());
            stmt.setString(6, avis.getAudioFile());
            stmt.setString(7, avis.getVideoFile());
            stmt.setString(8, avis.getMediaType());
            stmt.setInt(9, avis.getRating());
            stmt.setTimestamp(10, avis.getCreatedAt() != null ?
                Timestamp.valueOf(avis.getCreatedAt()) : Timestamp.valueOf(java.time.LocalDateTime.now()));

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
                Avis avis = mapResultSetToAvis(rs);
                avisList.add(avis);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return avisList;
    }

    // Helper method to map ResultSet to Avis object
    private Avis mapResultSetToAvis(ResultSet rs) throws SQLException {
        Avis avis = new Avis();
        avis.setId(rs.getInt("id"));

        // Handle nullable fields
        Object userId = rs.getObject("user_id");
        if (userId != null) {
            avis.setUserId((Integer) userId);
        }

        avis.setName(rs.getString("nom"));

        // Get description from 'avis' column if available, otherwise from 'description'
        String avisText = rs.getString("avis");
        String description = rs.getString("description");
        avis.setDescription(avisText != null ? avisText : description);

        avis.setImage(rs.getString("image"));
        avis.setAudioFile(rs.getString("audio_file"));
        avis.setVideoFile(rs.getString("video_file"));
        avis.setMediaType(rs.getString("media_type"));
        avis.setRating(rs.getInt("note"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            avis.setCreatedAt(createdAt.toLocalDateTime());
        } else {
            avis.setCreatedAt(java.time.LocalDateTime.now());
        }

        return avis;
    }

    // Update an existing Avis
    public boolean update(Avis avis) throws SQLException {
        if (findById(avis.getId()) == null) {
            throw new IllegalArgumentException("L'avis avec l'ID " + avis.getId() + " n'existe pas");
        }

        String sql = "UPDATE avis SET user_id = ?, nom = ?, avis = ?, description = ?, image = ?, " +
                     "audio_file = ?, video_file = ?, media_type = ?, note = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, avis.getUserId()); // Can be null
            stmt.setString(2, avis.getName());
            stmt.setString(3, avis.getDescription()); // Using description as 'avis' field
            stmt.setString(4, avis.getDescription()); // Also set as description field
            stmt.setString(5, avis.getImage());
            stmt.setString(6, avis.getAudioFile());
            stmt.setString(7, avis.getVideoFile());
            stmt.setString(8, avis.getMediaType());
            stmt.setInt(9, avis.getRating());
            stmt.setInt(10, avis.getId());

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
                    return mapResultSetToAvis(rs);
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

    public int getTotalReviews() throws SQLException {
        String sql = "SELECT COUNT(*) FROM avis";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1); // Retourne le nombre total d'avis
            }
        }
        return 0;
    }

    public double getAverageRating() throws SQLException {
        String sql = "SELECT AVG(note) AS average FROM avis";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble(1); // Retourne la note moyenne
            }
        }
        return 0.0;
    }

    // Ajout de la méthode pour compter les avis par note
    public int getReviewCountByRating(int rating) throws SQLException {
        String sql = "SELECT COUNT(*) FROM avis WHERE note = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, rating);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1); // Retourne le nombre d'avis pour la note donnée
            }
        }
        return 0;
    }
}