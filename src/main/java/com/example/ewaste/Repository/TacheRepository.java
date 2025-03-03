package com.example.ewaste.Repository;

import com.example.ewaste.Entities.PlanificationTache;
import com.example.ewaste.Interfaces.IService;
import com.example.ewaste.Entities.Tache;
import com.example.ewaste.Utils.DataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TacheRepository implements IService<Tache> {
    private static Connection connection;

    public TacheRepository() {
        connection = DataBase.getInstance().getConnection();
    }

    @Override
    public void ajouter(Tache tache) throws SQLException {
        String sql = "INSERT INTO `tache`(`id_centre`, `id_employe`, `latitude`, `longitude`, `message`, `etat`) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, tache.getId_centre());
        ps.setInt(2, tache.getId_employe());
        ps.setFloat(3, tache.getLatitude());
        ps.setFloat(4, tache.getLongitude());
        ps.setString(5, tache.getMessage());
        ps.setString(6, tache.getEtat());
        ps.executeUpdate();
    }

    @Override
    public void modifier(Tache tache) throws SQLException {
        String sql = "UPDATE `tache` SET `id_centre`=?, `id_employe`=?, `latitude`=?, `longitude`=?, `message`=?, `etat`=? WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, tache.getId_centre());
        ps.setInt(2, tache.getId_employe());
        ps.setFloat(3, tache.getLatitude());
        ps.setFloat(4, tache.getLongitude());
        ps.setString(5, tache.getMessage());
        ps.setString(6, tache.getEtat());
        ps.setInt(7, tache.getId());
        ps.executeUpdate();
    }

    @Override
    public List<PlanificationTache> recuperer() throws SQLException {
        return List.of();
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String sql = "DELETE FROM `tache` WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();
    }

    @Override
    public List<Tache> afficher() throws SQLException {
        return List.of();
    }

    @Override
    public List<PlanificationTache> afficher(int id_centre) throws SQLException {
        return List.of();
    }


    public List<Tache> afficherr(int idCentre) throws SQLException {
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
                    rs.getFloat("latitude"),
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
                    rs.getFloat("latitude"),
                    rs.getFloat("longitude"),
                    rs.getString("message"),
                    rs.getString("etat")
            ));
        }
        return taches;
    }

    public static List<String> getEmployeNames() throws SQLException {
        List<String> employeNames = new ArrayList<>();
        String query = "SELECT nom FROM utilisateur WHERE role = 'EMPLOYEE'";

        try (PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                employeNames.add(rs.getString("nom"));
            }
        }
        return employeNames;
    }

    public Integer getEmployeIdByName(String nom) throws SQLException {
        String query = "SELECT id FROM utilisateur WHERE nom = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, nom);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        }
        return null;
    }


        public String getEmployeNameById(int idEmploye) throws SQLException {
            String query = "SELECT nom FROM utilisateur WHERE id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, idEmploye);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getString("nom");
                }
            }
            return null;
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
        String query = "SELECT email FROM utilisateur WHERE id = ?";
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
                    rs.getFloat("latitude"),
                    rs.getFloat("longitude"),
                    rs.getString("message"),
                    rs.getString("etat")
            ));
        }
        return taches;
    }

}
