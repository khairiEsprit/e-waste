package com.example.ewaste.repository;

import com.example.hellofx.DataBase.DbConnection;
import com.example.hellofx.Entities.User;
import com.example.hellofx.Entities.UserRole;
import com.example.hellofx.Exceptions.DatabaseException;
import com.example.hellofx.Interfaces.EntityCrud;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.example.hellofx.Repository.AuthRepository.hashPassword2;

public class UserRepository implements EntityCrud<User> {
    private final Connection conn = DbConnection.getConnection();

    public int countusers() throws SQLException {
        String query = "SELECT COUNT(*) AS count FROM users";
        PreparedStatement statement = conn.prepareStatement(query) ;
        ResultSet resultSet = statement.executeQuery() ;
        if (resultSet.next()) {
            return resultSet.getInt("count");
        } else {
            return 0;
        }
    }





    // Create
    public void addEntity(User user) {
        // Specify the columns into which you want to insert values.
        String query = "INSERT INTO users (first_name,last_name, email, password, birth_date, role) VALUES (?,?, ?, ?, ?, ?)";

        try (conn;
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, user.getFirstName());
            // If you need to insert the last name, uncomment the following line and adjust the query/parameters.
             pstmt.setString(2,"testtt");
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, hashPassword2(user.getPassword()));
            pstmt.setDate(5, new Date(user.getDNaissance().getTime()));
            pstmt.setString(6, user.getRole().toString());
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
        String sql = "SELECT * FROM users";

        try (Connection conn = DbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setFirstName(rs.getString("first_name"));
                user.setLastName(rs.getString("last_name"));
                user.setEmail(rs.getString("email"));
                user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                users.add(user);
            }

        } catch (SQLException e) {
            throw new DatabaseException("Failed to retrieve users", e);
        }
        return users;
    }

    public ObservableList<User> getUsersList() {
        ObservableList<User> x = FXCollections.observableArrayList();

        Connection conn = DbConnection.getConnection();
        String query = "select * from user";
        Statement s1;
        ResultSet r1;
        try {
            s1 = conn.createStatement();
            r1 = s1.executeQuery(query);
            User u;
            while (r1.next()) {
                u = new User(r1.getString("first_name"), r1.getString("email"), r1.getString("password"),
                        r1.getDate("birth_date"), UserRole.valueOf(r1.getNString("role")), r1.getTimestamp("created_at").toLocalDateTime());
                x.add(u);


            }
        } catch (Exception ex) {
            System.err.println(ex.getMessage());

        }
        return x;

    }


    public ObservableList<User> getEmployeList() throws SQLException {
        ObservableList<User> employees = FXCollections.observableArrayList();
        String query = "SELECT * FROM user WHERE role = ?";

        Connection conn = DbConnection.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(query);

        pstmt.setString(1, "employe");
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            User user = new User(
                    rs.getString("first_name"), rs.getString("email"), rs.getString("password"),
                    rs.getDate("birth_date"), UserRole.valueOf(rs.getNString("role")), rs.getTimestamp("created_at").toLocalDateTime());

            employees.add(user);
        }

        return employees;
    }

    @Override
    public User display(int id) {
        return null;


    }


    public void updateEntity(User u) {
        String query = "UPDATE user SET role = ?, firstName = ?,email = ?,password = ?,birthDate = ? WHERE id = ?";


        try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
            preparedStatement.setString(1, u.getRole().toString());
            preparedStatement.setString(2, u.getFirstName());
            preparedStatement.setString(3, u.getEmail());
            preparedStatement.setString(4, u.getPassword());
            preparedStatement.setDate(5, (Date) u.getDNaissance());
            preparedStatement.setInt(6, u.getId());

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Employee Ajouté  jawek jben");
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("fama mochkla ");
                alert.showAndWait();
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }


    }



    public User getUserById(int id){
        String query = "SELECT * FROM users WHERE id = ?";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    User user = new User();
                    user.setId(resultSet.getInt("id"));
                    user.setFirstName(resultSet.getString("first_name"));
                    user.setLastName(resultSet.getString("last_name"));
                    user.setEmail(resultSet.getString("email"));
                    user.setRole(UserRole.valueOf(resultSet.getString("role")));
                    user.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
                    return user;
                }
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return null;
    }

    public User getUserByEmail(String email){
        String query = "SELECT * FROM users WHERE email = ?";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, email);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    User user = new User();
                    user.setId(resultSet.getInt("id"));
                    user.setFirstName(resultSet.getString("first_name"));
                    user.setLastName(resultSet.getString("last_name"));
                    user.setEmail(resultSet.getString("email"));
                    user.setRole(UserRole.valueOf(resultSet.getString("role")));
                    user.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
                    return user;
                }
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return null;
    }


    // Delete
    public void deleteEntity(int userId) {
        String sql = "DELETE FROM users WHERE id = ?";

        try (conn;
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
            if(pstmt.executeUpdate() > 0) {
                System.out.println("User deleted successfully");
            } else {
                System.out.println("User not found");
            }

        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete user", e);
        }
    }
}