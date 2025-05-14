package com.example.ewaste.Repository;


import com.example.ewaste.Entities.Employee;
import com.example.ewaste.Entities.User;
import com.example.ewaste.Entities.UserRole;
import com.example.ewaste.Interfaces.EntityCrud;
import com.example.ewaste.Utils.DataBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class EmployeeRepository implements EntityCrud<Employee> {

    private final Connection conn = DataBase.getInstance().getConnection();

    @Override
    public void addEntity(Employee employee) {

    }

    @Override
    public void updateEntity(Employee employee) {

    }

    @Override
    public void deleteEntity(int id) {

    }

    @Override
    public List<Employee> displayEntity() {
        return List.of();
    }

    @Override
    public Employee display(int id) {
        return null;
    }

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
        String query = "SELECT * FROM user WHERE roles LIKE ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, "%ROLE_EMPLOYEE%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setProfile_image(rs.getString("profile_image"));
                user.setActive("active".equalsIgnoreCase(rs.getString("active")));
                user.setFirst_name(rs.getString("first_name"));
                user.setLast_name(rs.getString("last_name"));
                user.setEmail(rs.getString("email"));
                user.setBirthdate(rs.getDate("birthdate"));
                user.setPhone(rs.getString("phone"));
                user.setRoles(rs.getString("roles"));
                employees.add(user);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching employee list: " + e.getMessage());
        }
        return employees;
    }

}
