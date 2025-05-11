package com.example.ewaste.Repository;

import com.example.ewaste.Entities.Participation;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ParticipationRepository {
    private final Connection conn = DataBaseConn.getInstance().getConnection();

    // Create (Insert) a new participation
    public boolean create(Participation participation) {
        String sql = "INSERT INTO participation (event_id, user_id, first_name, last_name, email, phone, city, zip_code, country, " +
                     "reminder_sent, participation_mode, google_meet_link, google_authenticated) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, participation.getEventId()); // Can be null
            stmt.setObject(2, participation.getUserId()); // Can be null
            stmt.setString(3, participation.getFirstName());
            stmt.setString(4, participation.getLastName());
            stmt.setString(5, participation.getEmail());
            stmt.setString(6, participation.getPhone());
            stmt.setString(7, participation.getCity());
            stmt.setString(8, participation.getZipCode());
            stmt.setString(9, participation.getCountry());
            stmt.setBoolean(10, participation.isReminderSent());
            stmt.setString(11, participation.getParticipationMode() != null ?
                participation.getParticipationMode() : "on-site");
            stmt.setString(12, participation.getGoogleMeetLink());
            stmt.setBoolean(13, participation.isGoogleAuthenticated());

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

    // Helper method to map ResultSet to Participation object
    private Participation mapResultSetToParticipation(ResultSet rs) throws SQLException {
        Participation participation = new Participation();
        participation.setId(rs.getInt("id"));

        // Handle nullable fields
        Object eventId = rs.getObject("event_id");
        if (eventId != null) {
            participation.setEventId((Integer) eventId);
        }

        Object userId = rs.getObject("user_id");
        if (userId != null) {
            participation.setUserId((Integer) userId);
        }

        participation.setFirstName(rs.getString("first_name"));
        participation.setLastName(rs.getString("last_name"));
        participation.setEmail(rs.getString("email"));
        participation.setPhone(rs.getString("phone"));
        participation.setCity(rs.getString("city"));
        participation.setZipCode(rs.getString("zip_code"));
        participation.setCountry(rs.getString("country"));
        participation.setReminderSent(rs.getBoolean("reminder_sent"));
        participation.setParticipationMode(rs.getString("participation_mode"));
        participation.setGoogleMeetLink(rs.getString("google_meet_link"));
        participation.setGoogleAuthenticated(rs.getBoolean("google_authenticated"));

        // For backward compatibility
        participation.setPointsEarned(0);

        return participation;
    }

    // Read (Select) a participation by its ID
    public Participation getById(int id) {
        String sql = "SELECT * FROM participation WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToParticipation(rs);
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
                participations.add(mapResultSetToParticipation(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return participations;
    }

    // Update an existing participation
    public boolean updateParticipation(Participation participation) {
        String sql = "UPDATE participation SET event_id = ?, user_id = ?, first_name = ?, last_name = ?, " +
                     "email = ?, phone = ?, city = ?, zip_code = ?, country = ?, reminder_sent = ?, " +
                     "participation_mode = ?, google_meet_link = ?, google_authenticated = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, participation.getEventId()); // Can be null
            stmt.setObject(2, participation.getUserId()); // Can be null
            stmt.setString(3, participation.getFirstName());
            stmt.setString(4, participation.getLastName());
            stmt.setString(5, participation.getEmail());
            stmt.setString(6, participation.getPhone());
            stmt.setString(7, participation.getCity());
            stmt.setString(8, participation.getZipCode());
            stmt.setString(9, participation.getCountry());
            stmt.setBoolean(10, participation.isReminderSent());
            stmt.setString(11, participation.getParticipationMode() != null ?
                participation.getParticipationMode() : "on-site");
            stmt.setString(12, participation.getGoogleMeetLink());
            stmt.setBoolean(13, participation.isGoogleAuthenticated());
            stmt.setInt(14, participation.getId());

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

    // Vérifier si une participation existe pour un événement et un utilisateur spécifiques
    public boolean isParticipationExistsForEventAndUser(Integer eventId, Integer userId) {
        if (eventId == null || userId == null) {
            return false;
        }

        String sql = "SELECT COUNT(*) FROM participation WHERE event_id = ? AND user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, eventId);
            stmt.setInt(2, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Méthode pour récupérer le total des points d'un citoyen (maintenu pour compatibilité)
    public int getTotalPointsByEmail(String email) {
        // Cette méthode est maintenue pour la compatibilité avec le code existant
        // Dans la nouvelle structure, les points ne sont plus stockés dans la table participation
        return 0;
    }

    // Méthode pour réinitialiser les points d'un citoyen (maintenu pour compatibilité)
    public boolean resetPoints(String email) {
        // Cette méthode est maintenue pour la compatibilité avec le code existant
        // Dans la nouvelle structure, les points ne sont plus stockés dans la table participation
        return true;
    }

    // Méthode pour vérifier si un citoyen a gagné une remise (maintenu pour compatibilité)
    public boolean hasWonRemise(String email) {
        // Cette méthode est maintenue pour la compatibilité avec le code existant
        // Dans la nouvelle structure, les points ne sont plus stockés dans la table participation
        return false;
    }

    // Méthode pour obtenir les participations d'un utilisateur
    public List<Participation> getParticipationsByUserId(Integer userId) {
        if (userId == null) {
            return new ArrayList<>();
        }

        List<Participation> participations = new ArrayList<>();
        String sql = "SELECT * FROM participation WHERE user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                participations.add(mapResultSetToParticipation(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return participations;
    }

    // Méthode pour obtenir les participations à un événement
    public List<Participation> getParticipationsByEventId(Integer eventId) {
        if (eventId == null) {
            return new ArrayList<>();
        }

        List<Participation> participations = new ArrayList<>();
        String sql = "SELECT * FROM participation WHERE event_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, eventId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                participations.add(mapResultSetToParticipation(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return participations;
    }
}