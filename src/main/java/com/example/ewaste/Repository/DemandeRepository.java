package com.example.ewaste.Repository;







import com.example.ewaste.Entities.Demande;
import com.example.ewaste.Entities.PlanificationTache;
import com.example.ewaste.Interfaces.IService;
import com.example.ewaste.Utils.DataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DemandeRepository implements IService<Demande> {
    //Déclaration et connexion à la base de données
    private Connection connection;

    public DemandeRepository() {
        connection = DataBase.getInstance().getConnection();
    }

    @Override
    public void ajouter(Demande demande) throws SQLException {
        String sql = "INSERT INTO `demande`(`utilisateur_id`, `centre_id`, `adresse`, `email_utilisateur`, `message`, `type`) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, 1);
        ps.setInt(2, 1);
        ps.setString(3, demande.getAdresse());
        ps.setString(4, demande.getEmailUtilisateur());
        ps.setString(5, demande.getMessage());
        ps.setString(6, demande.getType());
        ps.executeUpdate();
    }

    @Override
    public void modifier(Demande demande) throws SQLException {
        String sql = "UPDATE `demande` SET `utilisateur_id`=?, `centre_id`=?, `adresse`=?, `email_utilisateur`=?, `message`=?, `type`=? WHERE `id`=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, demande.getutilisateur_id());
        ps.setInt(2, demande.getcentre_id());
        ps.setString(3, demande.getAdresse());
        ps.setString(4, demande.getEmailUtilisateur());
        ps.setString(5, demande.getMessage());
        ps.setString(6, demande.getType());
        ps.setInt(7, demande.getId());
        ps.executeUpdate();
    }

    @Override
    public List<Demande> recuperer() throws SQLException {
        return afficher();
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String sql = "DELETE FROM `demande` WHERE `id`=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();
    }

    @Override
    public List<Demande> afficher() throws SQLException {
        List<Demande> demandes = new ArrayList<>();
        String sql = "SELECT * FROM `demande`";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);

        while (rs.next()) {
            demandes.add(new Demande(
                    rs.getInt("id"),
                    rs.getInt("utilisateur_id"),
                    rs.getInt("centre_id"),
                    rs.getString("adresse"),
                    rs.getString("email_utilisateur"),
                    rs.getString("message"),
                    rs.getString("type")
            ));
        }
        return demandes;
    }

    @Override
    public List<PlanificationTache> afficher(int centre_id) throws SQLException {
        return List.of();
    }

    public List<Demande> getDemandesByUserId(int userId) throws SQLException {
        List<Demande> demandes = new ArrayList<>();
        String sql = "SELECT * FROM `demande` WHERE `utilisateur_id`=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, 1);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            demandes.add(new Demande(
                    rs.getInt("id"),
                    rs.getInt("utilisateur_id"),
                    rs.getInt("centre_id"),
                    rs.getString("adresse"),
                    rs.getString("email_utilisateur"),
                    rs.getString("message"),
                    rs.getString("type")
            ));
        }
        return demandes;
    }
    public Demande getDemandeById(int id) throws SQLException {
        String sql = "SELECT * FROM `demande` WHERE `id`=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return new Demande(
                    rs.getInt("id"),
                    rs.getInt("utilisateur_id"),
                    rs.getInt("centre_id"),
                    rs.getString("adresse"),
                    rs.getString("email_utilisateur"),
                    rs.getString("message"),
                    rs.getString("type")
            );
        }
        return null;
    }

}
