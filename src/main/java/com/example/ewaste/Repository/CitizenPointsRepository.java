package com.example.ewaste.Repository;

import com.example.ewaste.Entities.CitizenPoints;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Repository pour gérer les opérations de base de données liées aux points des citoyens.
 */
public class CitizenPointsRepository {
    private final Connection connection;

    /**
     * Constructeur.
     *
     * @param connection La connexion à la base de données.
     */
    public CitizenPointsRepository(Connection connection) {
        if (connection == null) {
            throw new IllegalArgumentException("La connexion à la base de données ne peut pas être null.");
        }
        this.connection = connection;
    }

    /**
     * Ajoute des points à un citoyen.
     *
     * @param email  L'email du citoyen.
     * @param points Le nombre de points à ajouter.
     * @return true si l'opération a réussi, false sinon.
     * @throws IllegalArgumentException Si l'email est vide ou si les points sont négatifs.
     */
    public boolean addPoints(String email, int points) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("L'email ne peut pas être vide ou null.");
        }
        if (points < 0) {
            throw new IllegalArgumentException("Les points ne peuvent pas être négatifs.");
        }

        String query = "UPDATE citizens SET total_points = total_points + ? WHERE email = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, points);
            statement.setString(2, email);
            int rowsUpdated = statement.executeUpdate();
            return rowsUpdated > 0; // Retourne true si au moins une ligne a été mise à jour
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'ajout des points : " + e.getMessage(), e);
        }
    }

    /**
     * Récupère les points d'un citoyen.
     *
     * @param email L'email du citoyen.
     * @return Un objet CitizenPoints représentant les points du citoyen, ou null si le citoyen n'est pas trouvé.
     * @throws IllegalArgumentException Si l'email est vide.
     */
    public CitizenPoints getCitizenPoints(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("L'email ne peut pas être vide ou null.");
        }

        String query = "SELECT * FROM citizens WHERE email = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, email);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new CitizenPoints(
                            resultSet.getString("email"),
                            resultSet.getString("first_name"),
                            resultSet.getString("last_name"),
                            resultSet.getInt("total_points")
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des points : " + e.getMessage(), e);
        }
        return null;
    }
}