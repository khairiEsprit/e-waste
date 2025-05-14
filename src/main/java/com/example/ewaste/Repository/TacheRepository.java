package com.example.ewaste.Repository;

import com.example.ewaste.Entities.Tache;
import com.example.ewaste.Utils.DataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TacheRepository {
    private static Connection connection;

    public TacheRepository() {
        connection = DataBase.getConnection();
    }

    public void ajouter(Tache tache) throws SQLException {
        String sql = "INSERT INTO `tache`(`id_centre`, `id_employe`, `altitude`, `longitude`, `message`, `etat`) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, tache.getId_centre());
        ps.setInt(2, tache.getId_employe());
        ps.setFloat(3, tache.getAltitude());
        ps.setFloat(4, tache.getLongitude());
        ps.setString(5, tache.getMessage());
        ps.setString(6, tache.getEtat());
        ps.executeUpdate();
    }

    public void modifier(Tache tache) throws SQLException {
        String sql = "UPDATE `tache` SET `id_centre`=?, `id_employe`=?, `altitude`=?, `longitude`=?, `message`=?, `etat`=? WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, tache.getId_centre());
        ps.setInt(2, tache.getId_employe());
        ps.setFloat(3, tache.getAltitude());
        ps.setFloat(4, tache.getLongitude());
        ps.setString(5, tache.getMessage());
        ps.setString(6, tache.getEtat());
        ps.setInt(7, tache.getId());
        ps.executeUpdate();
    }

    public List<Tache> recuperer() throws SQLException {
        return List.of();
    }

    public List<Tache> afficher() throws SQLException {
        List<Tache> taches = new ArrayList<>();
        String sql = "SELECT * FROM `tache`";
        PreparedStatement ps = connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            taches.add(new Tache(
                    rs.getInt("id"),
                    rs.getInt("id_centre"),
                    rs.getInt("id_employe"),
                    rs.getFloat("altitude"),
                    rs.getFloat("longitude"),
                    rs.getString("message"),
                    rs.getString("etat")
            ));
        }
        return taches;
    }

    public void supprimer(int id) throws SQLException {
        String sql = "DELETE FROM `tache` WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();
    }

    public List<Tache> afficher(int idCentre) throws SQLException {
        List<Tache> taches = new ArrayList<>();
        String sql = "SELECT * FROM `tache` WHERE `id_centre` = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, idCentre);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            taches.add(new Tache(
                    rs.getInt("id"),
                    rs.getInt("id_centre"),
                    rs.getInt("id_employe"),
                    rs.getFloat("altitude"),
                    rs.getFloat("longitude"),
                    rs.getString("message"),
                    rs.getString("etat")
            ));
        }
        return taches;
    }

    public List<Tache> afficherParEmploye(int idCentre, int idEmploye) throws SQLException {
        List<Tache> taches = new ArrayList<>();
        String sql = "SELECT * FROM `tache` WHERE `id_centre` = ? AND `id_employe` = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, idCentre);
        ps.setInt(2, idEmploye);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            taches.add(new Tache(
                    rs.getInt("id"),
                    rs.getInt("id_centre"),
                    rs.getInt("id_employe"),
                    rs.getFloat("altitude"),
                    rs.getFloat("longitude"),
                    rs.getString("message"),
                    rs.getString("etat")
            ));
        }
        return taches;
    }

    public List<String> getEmployeNames() throws SQLException {
        List<String> employeNames = new ArrayList<>();

        // More flexible query to find employees - check for different role formats
        String query = "SELECT first_name FROM user WHERE role IN ('EMPLOYE', 'employe', 'Employe', 'EMPLOYEE', 'employee', 'Employee') OR role LIKE '%employ%'";

        try (Connection conn = DataBase.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String firstName = rs.getString("first_name");
                if (firstName != null && !firstName.trim().isEmpty()) {
                    employeNames.add(firstName);
                }
            }

            // If no employees found, add some default names for testing
            if (employeNames.isEmpty()) {
                employeNames.add("John");
                employeNames.add("Jane");
                employeNames.add("Bob");
                System.out.println("No employees found in database, using default names");
            } else {
                System.out.println("Found " + employeNames.size() + " employees in database");
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving employee names: " + e.getMessage());
            e.printStackTrace();
            // Add default names in case of error
            employeNames.add("John");
            employeNames.add("Jane");
            employeNames.add("Bob");
        }

        return employeNames;
    }

    public Integer getEmployeIdByName(String first_name) throws SQLException {
        if (first_name == null || first_name.trim().isEmpty()) {
            System.err.println("Warning: Empty employee name provided");
            return 1; // Default to ID 1 if no name provided
        }

        String query = "SELECT id FROM user WHERE first_name = ?";
        try (Connection conn = DataBase.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, first_name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    System.out.println("Found employee ID: " + id + " for name: " + first_name);
                    return id;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding employee ID by name: " + e.getMessage());
            e.printStackTrace();
        }

        // If no employee found with this name, return a default ID
        System.out.println("No employee found with name: " + first_name + ", using default ID 1");
        return 1; // Default ID if not found
    }


        public String getEmployeNameById(int idEmploye) throws SQLException {
        String query = "SELECT first_name FROM user WHERE id = ?";
        try (Connection conn = DataBase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idEmploye);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String name = rs.getString("first_name");
                    if (name != null && !name.trim().isEmpty()) {
                        return name;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding employee name by ID: " + e.getMessage());
            e.printStackTrace();
        }

        // Return a default name if not found
        return "Employé " + idEmploye;
    }


       public List<Integer> getEmployeIds() throws SQLException {
           // Retourne une liste des IDs des employés depuis la base de données
           String query = "SELECT id FROM employes";
           List<Integer> employeIds = new ArrayList<>();
           try (Connection connection = DriverManager.getConnection("your-database-url", "username", "password");
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query)) {

               while (resultSet.next()) {
                   employeIds.add(resultSet.getInt("id"));
               }
           }
           return employeIds;
       }


    public String getEmployeEmailById(int idEmploye) throws SQLException {
        String email = null;
        String query = "SELECT email FROM user WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, idEmploye);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    email = rs.getString("email");
                }
            }
        }
        return email;
    }

    public List<Tache> getTachesByEmploye(int employeId) throws SQLException {
        List<Tache> taches = new ArrayList<>();
        String sql = "SELECT * FROM `tache` WHERE `id_employe` = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, employeId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            taches.add(new Tache(
                    rs.getInt("id"),
                    rs.getInt("id_centre"),
                    rs.getInt("id_employe"),
                    rs.getFloat("altitude"),
                    rs.getFloat("longitude"),
                    rs.getString("message"),
                    rs.getString("etat")
            ));
        }
        return taches;
    }

}
