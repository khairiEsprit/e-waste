package com.example.ewaste.Repository;

import com.example.ewaste.Entities.User;
import com.example.ewaste.Entities.UserRole;
import com.example.ewaste.Exceptions.DatabaseException;
import com.example.ewaste.Interfaces.EntityCrud;
import com.example.ewaste.Utils.DataBase;

import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.example.ewaste.Repository.AuthRepository.hashPassword2;


public class UserRepository implements EntityCrud<User> {
    private final Connection conn = DataBase.getInstance().getConnection();

    public int countusers() throws SQLException {
        String query = "SELECT COUNT(*) AS count FROM user";
        PreparedStatement statement = conn.prepareStatement(query);
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            return resultSet.getInt("count");
        } else {
            return 0;
        }
    }


    public List<User> getUsersByRole(String role) throws SQLException {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM user WHERE roles LIKE ?";
        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setString(1, "%ROLE_" + role + "%");
        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            User user = new User();
            mapResultSetToUser(rs, user);
            users.add(user);
        }
        return users;
    }

    // Helper method to map ResultSet to User object
    private void mapResultSetToUser(ResultSet rs, User user) throws SQLException {
        user.setId(rs.getInt("id"));

        // Basic information
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setActive(rs.getBoolean("active"));
        user.setFreeze(rs.getBoolean("freeze"));
        user.setRoles(rs.getString("roles"));

        // Profile information
        user.setFirst_name(rs.getString("first_name"));
        user.setLast_name(rs.getString("last_name"));
        user.setPhone(rs.getString("phone"));
        user.setBirthdate(rs.getDate("birthdate"));
        user.setLanguage(rs.getString("language"));
        user.setWallet_address(rs.getString("wallet_address"));

        // Profile image and face recognition
        user.setProfile_image(rs.getString("profile_image"));
        user.setIs_face_recognition_enabled(rs.getBoolean("is_face_recognition_enabled"));
        user.setFace_embeddings(rs.getString("face_embeddings"));
        user.setFace_photo_path(rs.getString("face_photo_path"));

        // User experience
        user.setHas_seen_guide(rs.getBoolean("has_seen_guide"));

        // Authentication and timestamps
        user.setLast_login(rs.getTimestamp("last_login"));
        user.setConfirmation_token(rs.getString("confirmation_token"));
        user.setPassword_requested_at(rs.getTimestamp("password_requested_at"));
        user.setCreated_at(rs.getTimestamp("created_at"));
    }

    // Create
    public void addEntity(User user) {
        String query = "INSERT INTO user (first_name, last_name, phone, email, password, birthdate, roles, " +
                "profile_image, active, freeze, is_face_recognition_enabled, has_seen_guide, created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, user.getFirst_name());
            pstmt.setString(2, user.getLast_name());
            pstmt.setString(3, user.getPhone());
            pstmt.setString(4, user.getEmail());
            pstmt.setString(5, hashPassword2(user.getPassword()));
            pstmt.setDate(6, user.getBirthdate());
            pstmt.setString(7, user.getRoles());
            pstmt.setString(8, user.getProfile_image());
            pstmt.setBoolean(9, user.isActive());
            pstmt.setBoolean(10, user.isFreeze());
            pstmt.setBoolean(11, user.isIs_face_recognition_enabled());
            pstmt.setBoolean(12, user.isHas_seen_guide());

            // Set current timestamp if not provided
            Timestamp createdAt = user.getCreated_at();
            if (createdAt == null) {
                createdAt = new Timestamp(System.currentTimeMillis());
            }
            pstmt.setTimestamp(13, createdAt);

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
        String sql = "SELECT * FROM user";

        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                User user = new User();
                mapResultSetToUser(rs, user);
                users.add(user);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to retrieve users", e);
        }
        return users;
    }

    @Override
    public User display(int id) {
        return getUserById(id);
    }

    public void updateEntity(User u) {
        String query = "UPDATE user SET first_name = ?, last_name = ?, phone = ?, email = ?, " +
                "password = ?, birthdate = ?, roles = ?, profile_image = ?, active = ?, freeze = ?, " +
                "is_face_recognition_enabled = ?, face_embeddings = ?, face_photo_path = ?, " +
                "has_seen_guide = ?, last_login = ?, confirmation_token = ?, " +
                "password_requested_at = ?, language = ?, wallet_address = ? WHERE id = ?";

        try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
            preparedStatement.setString(1, u.getFirst_name());
            preparedStatement.setString(2, u.getLast_name());
            preparedStatement.setString(3, u.getPhone());
            preparedStatement.setString(4, u.getEmail());
            preparedStatement.setString(5, u.getPassword());
            preparedStatement.setDate(6, u.getBirthdate());
            preparedStatement.setString(7, u.getRoles());
            preparedStatement.setString(8, u.getProfile_image());
            preparedStatement.setBoolean(9, u.isActive());
            preparedStatement.setBoolean(10, u.isFreeze());
            preparedStatement.setBoolean(11, u.isIs_face_recognition_enabled());
            preparedStatement.setString(12, u.getFace_embeddings());
            preparedStatement.setString(13, u.getFace_photo_path());
            preparedStatement.setBoolean(14, u.isHas_seen_guide());
            preparedStatement.setTimestamp(15, u.getLast_login());
            preparedStatement.setString(16, u.getConfirmation_token());
            preparedStatement.setTimestamp(17, u.getPassword_requested_at());
            preparedStatement.setString(18, u.getLanguage());
            preparedStatement.setString(19, u.getWallet_address());
            preparedStatement.setInt(20, u.getId());

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

    public void updateEmailPhoneAndImage(User u) {
        String query = "UPDATE user SET email = ?, phone = ?, profile_image = ? WHERE id = ?";

        try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
            preparedStatement.setString(1, u.getEmail());
            preparedStatement.setString(2, u.getPhone());
            preparedStatement.setString(3, u.getProfile_image());
            preparedStatement.setInt(4, u.getId());

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("User email, phone, and image updated successfully");
            } else {
                System.out.println("User not found");
            }
        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
        }
    }

    public User getUserById(int id) {
        String query = "SELECT * FROM user WHERE id = ?";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    User user = new User();
                    mapResultSetToUser(resultSet, user);
                    return user;
                }
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return null;
    }

    public void deleteEntity(int userId) {
        String sql = "DELETE FROM user WHERE id = ?";

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
        String query = "SELECT id FROM user WHERE email = ?";

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
        String query = "SELECT * FROM user WHERE email = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    mapResultSetToUser(rs, user);
                    return user;
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to retrieve user by email", e);
        }

        return null; // Return null if the user is not found
    }

    public User getUserByName(String userName) {
        String query = "SELECT * FROM user WHERE first_name = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, userName);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    mapResultSetToUser(rs, user);
                    return user;
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to retrieve user by name", e);
        }

        return null; // Return null if the user is not found
    }
}
