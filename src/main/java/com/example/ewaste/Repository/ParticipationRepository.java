package com.example.ewaste.Repository;


import com.example.ewaste.Entities.Participation;
import com.example.ewaste.Utils.DataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ParticipationRepository {
    private final Connection conn = DataBase.getInstance().getConnection();

    // Create (Insert) a new participation
    public boolean create(Participation participation) {
        String sql = "INSERT INTO participation (firstName, lastName, email, phone, city, zipCode) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, participation.getFirstName());
            stmt.setString(2, participation.getLastName());
            stmt.setString(3, participation.getEmail());
            stmt.setString(4, participation.getPhone());
            stmt.setString(5, participation.getCity());
            stmt.setString(6, participation.getZipCode());
            return stmt.executeUpdate() > 0;  // returns true if a record was inserted
        } catch (SQLException e) {
            e.printStackTrace();
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
                        rs.getString("zipCode")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;  // returns null if no participation found with the given ID
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
                        rs.getString("zipCode")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return participations;
    }

    // Update an existing participation
    public boolean update(Participation participation) {
        String sql = "UPDATE participation SET firstName = ?, lastName = ?, email = ?, phone = ?, city = ?, zipCode = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, participation.getFirstName());
            stmt.setString(2, participation.getLastName());
            stmt.setString(3, participation.getEmail());
            stmt.setString(4, participation.getPhone());
            stmt.setString(5, participation.getCity());
            stmt.setString(6, participation.getZipCode());
            stmt.setInt(7, participation.getId());
            return stmt.executeUpdate() > 0;  // returns true if the record was updated
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
            return stmt.executeUpdate() > 0;  // returns true if the record was deleted
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
