package com.example.ewaste.Repository;






import com.example.ewaste.Entities.PlanificationTache;
import com.example.ewaste.Entities.Traitement;
import com.example.ewaste.Interfaces.IService;
import com.example.ewaste.Utils.DataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TraitementRepository implements IService<Traitement> {
    private Connection connection;

    public TraitementRepository() {
        connection = DataBase.getInstance().getConnection();
    }

    @Override
    public void ajouter(Traitement traitement) throws SQLException {
        String sql = "INSERT INTO `traitement`(`demande_id`, `status`, `date_traitement`, `commentaire`) VALUES (?, ?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, traitement.getIdDemande());
        ps.setString(2, traitement.getStatus());
        ps.setTimestamp(3, Timestamp.valueOf(traitement.getDateTraitement()));
        ps.setString(4, traitement.getCommentaire());
        ps.executeUpdate();
    }

    @Override
    public void modifier(Traitement traitement) throws SQLException {
        String sql = "UPDATE `traitement` SET `demande_id`=?, `status`=?, `date_traitement`=?, `commentaire`=? WHERE `id`=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, traitement.getIdDemande());
        ps.setString(2, traitement.getStatus());
        ps.setTimestamp(3, Timestamp.valueOf(traitement.getDateTraitement()));
        ps.setString(4, traitement.getCommentaire());
        ps.setInt(5, traitement.getId());
        ps.executeUpdate();
    }

    @Override
    public List<PlanificationTache> recuperer() throws SQLException {
        return List.of();
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String sql = "DELETE FROM `traitement` WHERE `id`=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();
    }

    @Override
    public List<Traitement> afficher() throws SQLException {
        List<Traitement> traitements = new ArrayList<>();
        // afficher()
        //Récupère et retourne tous les traitements de la table.
        String sql = "SELECT * FROM `traitement`";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);

        while (rs.next()) {
            traitements.add(new Traitement(
                    rs.getInt("id"),
                    rs.getInt("demande_id"),
                    rs.getString("status"),
                    rs.getTimestamp("date_traitement").toLocalDateTime(),
                    rs.getString("commentaire")
            ));
        }
        return traitements;
    }

    @Override
    public List<PlanificationTache> afficher(int id_centre) throws SQLException {
        return List.of();
    }

    public boolean traitementExistsForDemande(int idDemande) throws SQLException {
        String sql = "SELECT COUNT(*) FROM `traitement` WHERE demande_id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, idDemande);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt(1) > 0;
        }
        return false;
    }

    public List<Traitement> getTraitementByDemande(int idDemande) throws SQLException {
        List<Traitement> traitements = new ArrayList<>();
        //Récupère les traitements liés à une demande spécifique (demande_id).
        String sql = "SELECT * FROM `traitement` WHERE demande_id = ?";

        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, idDemande);
        ResultSet rs = preparedStatement.executeQuery();

        while (rs.next()) {
            traitements.add(new Traitement(
                    rs.getInt("id"),
                    rs.getInt("demande_id"),
                    rs.getString("status"),
                    rs.getTimestamp("date_traitement").toLocalDateTime(),// Conversion Timestamp → LocalDateTime
                    rs.getString("commentaire")
            ));
        }
        return traitements;
    }

}
