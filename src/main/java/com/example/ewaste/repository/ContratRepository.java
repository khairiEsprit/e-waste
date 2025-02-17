package com.example.ewaste.repository;



import com.example.ewaste.entities.Centre;
import com.example.ewaste.entities.Contrat;
import com.example.ewaste.interfaces.IService;
import com.example.ewaste.utils.DataBase;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ContratRepository implements IService<Contrat> {
    private Connection connection;
    public ContratRepository() {
        connection = DataBase.getInstance().getConnection();
    }
    @Override
    public void ajouter(Contrat contrat) throws SQLException {
        String sql = "INSERT INTO `contrat`(`id_centre`, `id_employe`, `date_debut`, `date_fin`) VALUES ('"+contrat.getIdCentre()+"','"+contrat.getIdEmploye()+"','"+contrat.getDateDebut()+"','"+contrat.getDateFin()+"')";
        Statement statement = connection.createStatement();
        statement.executeUpdate(sql);
    }

    @Override
    public void modifier(Contrat contrat) throws SQLException {
        String sql = "UPDATE `contrat` SET `id_centre`=?,`id_employe`=?,`date_debut`=?,`date_fin`=?   WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, contrat.getIdCentre());
        ps.setInt(2, contrat.getIdEmploye());
        ps.setDate(3, Date.valueOf(contrat.getDateDebut()));
        ps.setDate(4, Date.valueOf(contrat.getDateFin()));
        ps.setInt(5, contrat.getId());

        ps.executeUpdate();

    }

    @Override
    public void supprimer(int id) throws SQLException {
        String sql = "DELETE FROM `contrat` WHERE id =?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();

    }

    @Override
    public  List<Contrat> afficher() throws SQLException {
        List<Contrat> contrats = new ArrayList<>();
        String sql = " SELECT * FROM `contrat`";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);
        while (rs.next()){
            contrats.add(new Contrat(rs.getInt("id"), rs.getInt("id_centre"), rs.getInt("id_employe"), rs.getDate("date_debut").toLocalDate(), rs.getDate("date_fin").toLocalDate()));
        }
        return contrats;
    }

    public List<Integer> getCentreIds() throws SQLException {
        List<Integer> centreIds = new ArrayList<>();
        String sql = "SELECT id FROM centre";  // Assurez-vous de remplacer "centre" par le nom correct de la table.

        try (Statement statement = connection.createStatement(); ResultSet rs = statement.executeQuery(sql)) {
            while (rs.next()) {
                centreIds.add(rs.getInt("id"));
            }
        }

        return centreIds;
    }

  /*  public List<Centre> getAllCentres() throws SQLException {
        List<Centre> centres = new ArrayList<>();
        String query = "SELECT id, nom, adresse, telephone, email FROM Centre";

        try (Connection conn = DataBase.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String nom = rs.getString("nom");
                String adresse = rs.getString("adresse");
                int telephone = rs.getInt("telephone");
                String email = rs.getString("email");

                centres.add(new Centre(id, nom, adresse, telephone, email));
            }
        }
        return centres;
    }*/

    public List<Integer> getEmployeIds() throws SQLException {
        List<Integer> employeIds = new ArrayList<>();
        String sql = "SELECT id FROM utilisateur";  // Assurez-vous de remplacer "employe" par le nom correct de la table.

        try (Statement statement = connection.createStatement(); ResultSet rs = statement.executeQuery(sql)) {
            while (rs.next()) {
                employeIds.add(rs.getInt("id"));
            }
        }
        return employeIds;

    }

    public boolean existeContrat(int idCentre, int idEmploye, LocalDate dateDebut, LocalDate dateFin) throws SQLException {
        String sql = "SELECT * FROM `contrat` WHERE `id_centre` = ? AND `id_employe` = ? AND `date_debut` = ? AND `date_fin` = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, idCentre);
        ps.setInt(2, idEmploye);
        ps.setDate(3, Date.valueOf(dateDebut));
        ps.setDate(4, Date.valueOf(dateFin));

        ResultSet rs = ps.executeQuery();

        return rs.next();
    }
    public Integer getCentreIdByName(String centreNom) throws SQLException {
        // Créez votre requête SQL pour récupérer l'ID en fonction du nom du centre
        String query = "SELECT id FROM centre WHERE nom = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, centreNom);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            } else {
                return null; // Si aucun centre n'est trouvé, retournez null
            }
        }
    }

    public List<Centre> getCentres() throws SQLException {
        List<Centre> centres = new ArrayList<>();
        String query = "SELECT id, nom FROM centre"; // Requête pour récupérer l'id et le nom des centres
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String nom = rs.getString("nom");
                centres.add(new Centre(id, nom)); // Créez un objet Centre et ajoutez-le à la liste
            }
        }
        return centres; // Retourne la liste des centres
    }

    public List<String> getEmployeNames() throws SQLException {
        List<String> employeNames = new ArrayList<>();
        String query = "SELECT nom FROM utilisateur WHERE role = 'EMPLOYE'"; // Récupère tous les noms des utilisateurs

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



    public String getCentreNameById(int centreId) throws SQLException {
        String query = "SELECT nom FROM centre WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, centreId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("nom");
            }
        }
        return null;
    }

    public String getEmployeNameById(int employeId) throws SQLException {
        String query = "SELECT nom FROM utilisateur WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, employeId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("nom");
            }
        }
        return null;
    }




}

