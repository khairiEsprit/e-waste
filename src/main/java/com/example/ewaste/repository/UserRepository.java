package com.example.ewaste.repository;


import com.example.ewaste.entities.User;
import com.example.ewaste.entities.UserRole;
import com.example.ewaste.exceptions.DatabaseException;
import com.example.ewaste.interfaces.EntityCrud;
import com.example.ewaste.utils.DataBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.example.ewaste.repository.AuthRepository.hashPassword2;


public class UserRepository implements EntityCrud<User> {
    private final Connection conn = DataBase.getInstance().getConnection();

    public int countusers() throws SQLException {
        String query = "SELECT COUNT(*) AS count FROM utilisateur";
        PreparedStatement statement = conn.prepareStatement(query);
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            return resultSet.getInt("count");
        } else {
            return 0;
        }
    }



    // Create
    public void addEntity(User user) {
        String query = "INSERT INTO utilisateur (nom, prenom, telephone, email, mdp, DateNss, role, photo) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, user.getNom());
            pstmt.setString(2, "test");
            pstmt.setInt(3, user.getTelephone());
            pstmt.setString(4, user.getEmail());
            pstmt.setString(5, hashPassword2(user.getPassword()));
            pstmt.setDate(6, new Date(user.getDateNss().getTime()));
            pstmt.setString(7, user.getRole().toString());
            pstmt.setString(8, user.getPhotoUrl());
            pstmt.executeUpdate();

            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int userId = generatedKeys.getInt(1);
                user.setId(userId);
            }
        } catch (SQLException | NoSuchAlgorithmException e) {
            throw new DatabaseException("Failed to create user", e);
        }
    }

    // Read All
    public List<User> displayEntity() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM utilisateur";

        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setNom(rs.getString("nom"));
                user.setPrenom(rs.getString("prenom"));
                user.setEmail(rs.getString("email"));
                user.setTelephone(rs.getInt("telephone"));
                user.setRole(UserRole.valueOf(rs.getString("role")));
                user.setDateNss(rs.getDate("DateNss"));
                user.setPhotoUrl(rs.getString("photo"));
                users.add(user);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to retrieve users", e);
        }
        return users;
    }

    @Override
    public User display(int id) {
        return null;
    }



    public void updateEntity(User u) {
        String query = "UPDATE utilisateur SET nom = ?, prenom = ?, telephone = ?, email = ?, mdp = ?, DateNss = ?, role = ?, photo = ?, WHERE id = ?";

        try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
            preparedStatement.setString(1, u.getNom());
            preparedStatement.setString(2, u.getPrenom());
            preparedStatement.setInt(3, u.getTelephone());
            preparedStatement.setString(4, u.getEmail());
            preparedStatement.setString(5, u.getPassword());
            preparedStatement.setDate(6, new Date(u.getDateNss().getTime()));
            preparedStatement.setString(7, u.getRole().toString());
            preparedStatement.setString(8, u.getPhotoUrl());
            preparedStatement.setInt(10, u.getId());

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("User updated successfully");
            } else {
                System.out.println("User not found");
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }


    public void updateEmailAndPhone(User u) {
        String query = "UPDATE utilisateur SET email = ?, telephone = ? WHERE id = ?";

        try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
            preparedStatement.setString(1, u.getEmail());
            preparedStatement.setInt(2, u.getTelephone());
            preparedStatement.setInt(3, u.getId());

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("User email and phone updated successfully");
            } else {
                System.out.println("User not found");
            }
        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
        }
    }


    public User getUserById(int id) {
        String query = "SELECT * FROM utilisateur WHERE id = ?";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    User user = new User();
                    user.setId(resultSet.getInt("id"));
                    user.setNom(resultSet.getString("nom"));
                    user.setPrenom(resultSet.getString("prenom"));
                    user.setEmail(resultSet.getString("email"));
                    user.setRole(UserRole.valueOf(resultSet.getString("role")));
                    user.setTelephone(resultSet.getInt("telephone"));
                    user.setDateNss(resultSet.getDate("DateNss"));
                    user.setPhotoUrl(resultSet.getString("photo"));
                    return user;
                }
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return null;
    }

    public void deleteEntity(int userId) {
        String sql = "DELETE FROM utilisateur WHERE id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("User deleted successfully");
            } else {
                System.out.println("User not found");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete user", e);
        }
    }

    public int getUserIdByEmail(String email) {
        String query = "SELECT id FROM utilisateur WHERE email = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to retrieve user ID by email", e);
        }

        return -1; // Return -1 if the user is not found
    }

    public User getUserByEmail(String email) {
        String query = "SELECT * FROM utilisateur WHERE email = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setNom(rs.getString("nom"));
                    user.setPrenom(rs.getString("prenom"));
                    user.setEmail(rs.getString("email"));
                    user.setRole(UserRole.valueOf(rs.getString("role")));
                    user.setTelephone(rs.getInt("telephone"));
                    user.setDateNss(rs.getDate("DateNss"));
                    user.setPhotoUrl(rs.getString("photo"));
                    return user;
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to retrieve user by email", e);
        }

        return null; // Return null if the user is not found
    }

}

