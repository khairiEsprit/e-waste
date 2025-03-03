package com.example.ewaste.Repository;

import com.example.ewaste.Entities.Participation;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ParticipationRepository {
    private final Connection conn = DataBaseConn.getInstance().getConnection();

    // Create (Insert) a new participation
    public boolean create(Participation participation) {
        String sql = "INSERT INTO participation (firstName, lastName, email, phone, city, country, zipCode, pointsEarned) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, participation.getFirstName());
            stmt.setString(2, participation.getLastName());
            stmt.setString(3, participation.getEmail());
            stmt.setString(4, participation.getPhone());
            stmt.setString(5, participation.getCity());
            stmt.setString(6, participation.getCountry());
            stmt.setString(7, participation.getZipCode());
            stmt.setInt(8, participation.getPointsEarned()); // Ajout du champ pointsEarned
            return stmt.executeUpdate() > 0;  // Retourne true si une ligne a été insérée
        } catch (SQLException e) {
            if (e.getSQLState().equals("23000")) {  // Code d'erreur pour violation de contrainte d'unicité
                System.err.println("Erreur : Une participation avec cet e-mail existe déjà.");
            } else {
                e.printStackTrace();
            }
            return false;
        }
    }

    // Read (Select) a participation by its ID
    public Participation getById(int id) {
        String sql = "SELECT * FROM participation WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Participation(
                        rs.getInt("id"),
                        rs.getString("firstName"),
                        rs.getString("lastName"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("city"),
                        rs.getString("country"),
                        rs.getString("zipCode"),
                        rs.getInt("pointsEarned") // Ajout du champ pointsEarned
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Read (Select) all participations
    public List<Participation> getAll() {
        List<Participation> participations = new ArrayList<>();
        String sql = "SELECT * FROM participation";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                participations.add(new Participation(
                        rs.getInt("id"),
                        rs.getString("firstName"),
                        rs.getString("lastName"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("city"),
                        rs.getString("country"),
                        rs.getString("zipCode"),
                        rs.getInt("pointsEarned") // Ajout du champ pointsEarned
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return participations;
    }

    // Update an existing participation
    public boolean updateParticipation(Participation participation) {
        String sql = "UPDATE participation SET firstName = ?, lastName = ?, email = ?, phone = ?, city = ?, country = ?, zipCode = ?, pointsEarned = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, participation.getFirstName());
            stmt.setString(2, participation.getLastName());
            stmt.setString(3, participation.getEmail());
            stmt.setString(4, participation.getPhone());
            stmt.setString(5, participation.getCity());
            stmt.setString(6, participation.getCountry());
            stmt.setString(7, participation.getZipCode());
            stmt.setInt(8, participation.getPointsEarned()); // Ajout du champ pointsEarned
            stmt.setInt(9, participation.getId());
            return stmt.executeUpdate() > 0;  // Retourne true si une ligne a été mise à jour
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Delete a participation by its ID
    public boolean delete(int id) {
        String sql = "DELETE FROM participation WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;  // Retourne true si une ligne a été supprimée
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Vérifier si une participation avec le même e-mail existe déjà
    public boolean isParticipationExists(String email) {
        String sql = "SELECT COUNT(*) FROM participation WHERE email = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // Retourne true si une participation existe déjà
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Méthode pour récupérer le total des points d'un citoyen
    public int getTotalPointsByEmail(String email) {
        String sql = "SELECT SUM(pointsEarned) AS totalPoints FROM participation WHERE email = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("totalPoints");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0; // Retourne 0 si le citoyen n'a pas de points
    }

    // Méthode pour réinitialiser les points d'un citoyen
    public boolean resetPoints(String email) {
        String sql = "UPDATE participation SET pointsEarned = 0 WHERE email = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            return stmt.executeUpdate() > 0; // Retourne true si les points ont été réinitialisés
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Méthode pour vérifier si un citoyen a gagné une remise
    public boolean hasWonRemise(String email) {
        int totalPoints = getTotalPointsByEmail(email);
        return totalPoints >= 100; // Retourne true si le citoyen a atteint 100 points
    }
}