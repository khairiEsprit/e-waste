package com.example.ewaste.Repository;

import com.example.ewaste.Controllers.DataBaseConn;
import com.example.ewaste.Entities.Event;
import com.example.ewaste.Entities.Participation;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ListEvenementRepository {

    private final Connection connection;

    public ListEvenementRepository() {
        this.connection = DataBaseConn.getInstance().getConnection();
    }

    // Méthode pour récupérer tous les événements
    public List<Event> getAllEvenements() {
        List<Event> evenements = new ArrayList<>();
        String query = "SELECT * FROM evenements"; // Remplacez "evenements" par le nom de votre table

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                // Convert java.sql.Date to LocalDateTime
                Date sqlDate = rs.getDate("date");
                LocalDateTime dateTime = null;
                if (sqlDate != null) {
                    dateTime = LocalDateTime.of(sqlDate.toLocalDate(), LocalTime.MIDNIGHT);
                }

                // Create Event with proper column names
                Event evenement = new Event();
                evenement.setId(rs.getInt("id"));
                evenement.setTitle(rs.getString("title"));
                evenement.setDescription(rs.getString("description"));

                // Try to get image column (could be imageUrl or image_name)
                try {
                    evenement.setImageName(rs.getString("imageUrl"));
                } catch (SQLException e) {
                    try {
                        evenement.setImageName(rs.getString("image_name"));
                    } catch (SQLException e2) {
                        evenement.setImageName(null);
                    }
                }

                evenement.setRemainingPlaces(rs.getInt("remainingPlaces"));
                evenement.setLocation(rs.getString("location"));
                evenement.setDate(dateTime);

                // Set default participation mode
                evenement.setParticipationMode("on-site");

                evenements.add(evenement);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des événements : " + e.getMessage());
        }
        return evenements;
    }

    // Méthode pour récupérer un événement par son ID
    public Optional<Event> getEvenementById(int id) {
        String query = "SELECT * FROM evenements WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // Convert java.sql.Date to LocalDateTime
                Date sqlDate = rs.getDate("date");
                LocalDateTime dateTime = null;
                if (sqlDate != null) {
                    dateTime = LocalDateTime.of(sqlDate.toLocalDate(), LocalTime.MIDNIGHT);
                }

                // Create Event with proper column names
                Event evenement = new Event();
                evenement.setId(rs.getInt("id"));
                evenement.setTitle(rs.getString("title"));
                evenement.setDescription(rs.getString("description"));

                // Try to get image column (could be imageUrl or image_name)
                try {
                    evenement.setImageName(rs.getString("imageUrl"));
                } catch (SQLException e) {
                    try {
                        evenement.setImageName(rs.getString("image_name"));
                    } catch (SQLException e2) {
                        evenement.setImageName(null);
                    }
                }

                evenement.setRemainingPlaces(rs.getInt("remainingPlaces"));
                evenement.setLocation(rs.getString("location"));
                evenement.setDate(dateTime);

                // Set default participation mode
                evenement.setParticipationMode("on-site");

                return Optional.of(evenement);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de l'événement : " + e.getMessage());
        }
        return Optional.empty();
    }

    // Méthode pour ajouter un nouvel événement
    public void addEvenement(Event evenement) {
        // Try to determine if the table uses imageUrl or image_name column
        String imageColumnName = "imageUrl"; // Default
        try {
            // Check if the table has image_name column
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM evenements LIMIT 1");
            rs.getMetaData().getColumnLabel(rs.findColumn("image_name"));
            imageColumnName = "image_name"; // If we get here, image_name exists
            stmt.close();
        } catch (SQLException e) {
            // If error, stick with default "imageUrl"
            System.out.println("Using imageUrl column for compatibility");
        }

        String query = "INSERT INTO evenements (title, description, " + imageColumnName + ", remainingPlaces, location, date, participation_mode, google_meet_link) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, evenement.getTitle());
            pstmt.setString(2, evenement.getDescription());
            pstmt.setString(3, evenement.getImageName()); // Use getImageName for both column types
            pstmt.setInt(4, evenement.getRemainingPlaces());
            pstmt.setString(5, evenement.getLocation());

            // Convert LocalDateTime to Timestamp
            if (evenement.getDate() != null) {
                pstmt.setTimestamp(6, Timestamp.valueOf(evenement.getDate()));
            } else {
                pstmt.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            }

            // Set participation mode and Google Meet link
            pstmt.setString(7, evenement.getParticipationMode() != null ?
                            evenement.getParticipationMode() : "on-site");
            pstmt.setString(8, evenement.getGoogleMeetLink());

            pstmt.executeUpdate();

            // Récupérer l'ID généré
            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                evenement.setId(generatedKeys.getInt(1));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de l'événement : " + e.getMessage());
        }
    }

    // Méthode pour mettre à jour un événement existant
    public boolean updateParticipation(Participation participation) {
        String query = "UPDATE participations SET firstName = ?, lastName = ?, email = ?, phone = ?, city = ?, country = ?, zipCode = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            // Définir les paramètres de la requête
            pstmt.setString(1, participation.getFirstName());
            pstmt.setString(2, participation.getLastName());
            pstmt.setString(3, participation.getEmail());
            pstmt.setString(4, participation.getPhone());
            pstmt.setString(5, participation.getCity());
            pstmt.setString(6, participation.getCountry());
            pstmt.setString(7, participation.getZipCode());
            pstmt.setInt(8, participation.getId()); // ID pour identifier la participation à mettre à jour

            // Exécuter la mise à jour
            int rowsUpdated = pstmt.executeUpdate();
            return rowsUpdated > 0; // Retourne true si au moins une ligne a été mise à jour
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de la participation : " + e.getMessage());
            return false;
        }
    }

    // Méthode pour supprimer un événement par son ID
    public boolean deleteEvenement(int id) {
        String query = "DELETE FROM evenements WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, id);

            int rowsDeleted = pstmt.executeUpdate();
            return rowsDeleted > 0; // Retourne true si au moins une ligne a été supprimée
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de l'événement : " + e.getMessage());
            return false;
        }
    }

    // Méthode pour rechercher des événements par titre
    public List<Event> searchEvenementsByTitle(String title) {
        List<Event> evenements = new ArrayList<>();
        String query = "SELECT * FROM evenements WHERE title LIKE ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, "%" + title + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                // Convert java.sql.Date to LocalDateTime
                Date sqlDate = rs.getDate("date");
                LocalDateTime dateTime = null;
                if (sqlDate != null) {
                    dateTime = LocalDateTime.of(sqlDate.toLocalDate(), LocalTime.MIDNIGHT);
                }

                // Create Event with proper column names
                Event evenement = new Event();
                evenement.setId(rs.getInt("id"));
                evenement.setTitle(rs.getString("title"));
                evenement.setDescription(rs.getString("description"));

                // Try to get image column (could be imageUrl or image_name)
                try {
                    evenement.setImageName(rs.getString("imageUrl"));
                } catch (SQLException e) {
                    try {
                        evenement.setImageName(rs.getString("image_name"));
                    } catch (SQLException e2) {
                        evenement.setImageName(null);
                    }
                }

                evenement.setRemainingPlaces(rs.getInt("remainingPlaces"));
                evenement.setLocation(rs.getString("location"));
                evenement.setDate(dateTime);

                // Set default participation mode
                evenement.setParticipationMode("on-site");

                evenements.add(evenement);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des événements : " + e.getMessage());
        }
        return evenements;
    }

    // Méthode pour récupérer le total des points d'un citoyen
    public int getTotalPointsByEmail(String email) {
        String query = "SELECT SUM(pointsEarned) AS totalPoints FROM participations WHERE email = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("totalPoints");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des points : " + e.getMessage());
        }
        return 0; // Retourne 0 si le citoyen n'a pas de points
    }

    // Méthode pour réinitialiser les points d'un citoyen
    public boolean resetPoints(String email) {
        String query = "UPDATE participations SET pointsEarned = 0 WHERE email = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, email);
            int rowsUpdated = pstmt.executeUpdate();
            return rowsUpdated > 0; // Retourne true si les points ont été réinitialisés
        } catch (SQLException e) {
            System.err.println("Erreur lors de la réinitialisation des points : " + e.getMessage());
            return false;
        }
    }

    // Méthode pour vérifier si un citoyen a gagné une remise
    public boolean hasWonRemise(String email) {
        int totalPoints = getTotalPointsByEmail(email);
        return totalPoints >= 100; // Retourne true si le citoyen a atteint 100 points
    }
}