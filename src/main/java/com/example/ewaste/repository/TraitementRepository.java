package com.example.ewaste.repository;






import com.example.ewaste.entities.Traitement;
import com.example.ewaste.interfaces.IService;
import com.example.ewaste.utils.DataBase;

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
        String sql = "INSERT INTO `traitement`(`id_demande`, `status`, `date_traitement`, `commentaire`) VALUES (?, ?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, traitement.getIdDemande());
        ps.setString(2, traitement.getStatus());
        ps.setTimestamp(3, Timestamp.valueOf(traitement.getDateTraitement()));
        ps.setString(4, traitement.getCommentaire());
        ps.executeUpdate();
    }

    @Override
    public void modifier(Traitement traitement) throws SQLException {
        String sql = "UPDATE `traitement` SET `id_demande`=?, `status`=?, `date_traitement`=?, `commentaire`=? WHERE `id`=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, traitement.getIdDemande());
        ps.setString(2, traitement.getStatus());
        ps.setTimestamp(3, Timestamp.valueOf(traitement.getDateTraitement()));
        ps.setString(4, traitement.getCommentaire());
        ps.setInt(5, traitement.getId());
        ps.executeUpdate();
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
                    rs.getInt("id_demande"),
                    rs.getString("status"),
                    rs.getTimestamp("date_traitement").toLocalDateTime(),
                    rs.getString("commentaire")
            ));
        }
        return traitements;
    }

    public boolean traitementExistsForDemande(int idDemande) throws SQLException {
        String sql = "SELECT COUNT(*) FROM `traitement` WHERE id_demande = ?";
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
        //Récupère les traitements liés à une demande spécifique (id_demande).
        String sql = "SELECT * FROM `traitement` WHERE id_demande = ?";

        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, idDemande);
        ResultSet rs = preparedStatement.executeQuery();

        while (rs.next()) {
            traitements.add(new Traitement(
                    rs.getInt("id"),
                    rs.getInt("id_demande"),
                    rs.getString("status"),
                    rs.getTimestamp("date_traitement").toLocalDateTime(),// Conversion Timestamp → LocalDateTime
                    rs.getString("commentaire")
            ));
        }
        return traitements;
    }

}
