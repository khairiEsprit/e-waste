package com.example.ewaste.Repository;


import com.example.ewaste.Entities.Employee;
import com.example.ewaste.Entities.User;
import com.example.ewaste.Entities.UserRole;
import com.example.ewaste.Interfaces.IService;
import com.example.ewaste.Utils.DataBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class EmployeeRepository implements IService<Employee> {

    private final Connection conn = DataBase.getInstance().getConnection();



    public int getEmployeeCount() throws SQLException {
        String query = "SELECT COUNT(*) FROM utilisateur WHERE role = ?";
        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setString(1, "EMPLOYEE");

        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            return rs.getInt(1); // Retrieve the count value
        }
        return 0; // Return 0 if no records found
    }

    public ObservableList<User> getEmployeList() throws SQLException {
        ObservableList<User> employees = FXCollections.observableArrayList();
        String query = "SELECT * FROM utilisateur WHERE role = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, "EMPLOYEE");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                User user = new User(
                        rs.getInt("id"),
                        rs.getString("photo"),  // Corrected order: photoUrl comes first
                        rs.getString("status"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("email"),
                        rs.getDate("DateNss"),
                        rs.getInt("telephone"),
                        UserRole.valueOf(rs.getString("role")) // Ensure the role exists in your enum
                );
                employees.add(user);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching employee list: " + e.getMessage());
        }
        return employees;
    }

    @Override
    public void ajouter(Employee employee) throws SQLException {

    }

    @Override
    public void modifier(Employee employee) throws SQLException {

    }

    @Override
    public void supprimer(int id) throws SQLException {

    }

    @Override
    public List<Employee> afficher() throws SQLException {
        return List.of();
    }
}
