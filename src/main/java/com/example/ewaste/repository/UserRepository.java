package com.example.ewaste.repository;

import com.example.ewaste.entities.User;
import com.example.ewaste.entities.UserRole;
import com.example.ewaste.exceptions.DatabaseException;
import com.example.ewaste.interfaces.EntityCrud;
import com.example.ewaste.utils.DataBase;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

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
        String query = "INSERT INTO utilisateur (nom, prenom, telephone, email, mdp, DateNss, role) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, user.getFirstName());
            pstmt.setString(2, "test");
//            pstmt.setString(3, user.getPhone());
            pstmt.setString(4, user.getEmail());
            pstmt.setString(5, hashPassword2(user.getPassword()));
            pstmt.setDate(6, new Date(user.getDNaissance().getTime()));
            pstmt.setString(7, user.getRole().toString());
//            pstmt.setString(8, user.getPhoto());
//            pstmt.setInt(9, user.getCentreId());
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
                user.setFirstName(rs.getString("nom"));
                user.setLastName(rs.getString("prenom"));
                user.setEmail(rs.getString("email"));
//                user.setPhone(rs.getString("telephone"));
                user.setRole(UserRole.valueOf(rs.getString("role")));
                user.setDNaissance(rs.getDate("DateNss"));
//                user.setPhoto(rs.getString("photo"));
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

    public ObservableList<User> getEmployeList() throws SQLException {
        ObservableList<User> employees = FXCollections.observableArrayList();
        String query = "SELECT * FROM utilisateur WHERE role = ?";

        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setString(1, "employe");
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            User user = new User(
                    rs.getString("nom"), rs.getString("email"), rs.getString("mdp"),
                    rs.getDate("DateNss"), UserRole.valueOf(rs.getString("role")));
            employees.add(user);
        }
        return employees;
    }

    public void updateEntity(User u) {
        String query = "UPDATE utilisateur SET nom = ?, prenom = ?, email = ?, mdp = ?, DateNss = ?, role = ?, WHERE id = ?";

        try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
            preparedStatement.setString(1, u.getFirstName());
            preparedStatement.setString(2, u.getLastName());
//            preparedStatement.setString(3, u.getPhone());
            preparedStatement.setString(4, u.getEmail());
            preparedStatement.setString(5, u.getPassword());
            preparedStatement.setDate(6, new Date(u.getDNaissance().getTime()));
            preparedStatement.setString(7, u.getRole().toString());
//            preparedStatement.setString(8, u.getPhoto());
//            preparedStatement.setInt(9, u.getCentreId());
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

    public User getUserById(int id) {
        String query = "SELECT * FROM utilisateur WHERE id = ?";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    User user = new User();
                    user.setId(resultSet.getInt("id"));
                    user.setFirstName(resultSet.getString("nom"));
                    user.setLastName(resultSet.getString("prenom"));
                    user.setEmail(resultSet.getString("email"));
                    user.setRole(UserRole.valueOf(resultSet.getString("role")));
//                    user.setPhone(resultSet.getString("telephone"));
                    user.setDNaissance(resultSet.getDate("DateNss"));
//                    user.setPhoto(resultSet.getString("photo"));
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
}
