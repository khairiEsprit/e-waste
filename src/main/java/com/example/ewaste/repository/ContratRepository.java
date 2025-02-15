package com.example.ewaste.repository;



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
}

