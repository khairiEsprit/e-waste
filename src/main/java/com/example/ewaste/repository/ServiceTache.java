package com.example.ewaste.repository;

import com.example.ewaste.interfaces.IService;
import com.example.ewaste.entities.Tache;
import com.example.ewaste.utils.DataBase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ServiceTache implements IService<Tache> {
    private Connection connection;

    public ServiceTache() {
        connection = DataBase.getInstance().getConnection();
    }

    @Override
    public void ajouter(Tache tache) throws SQLException {
        String sql = "INSERT INTO `tache`(`id_centre`, `id_employe`, `adresse_poubelle`, `message`, `etat`) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, tache.getId_centre());
        ps.setInt(2, tache.getId_employe());
        ps.setString(3, tache.getAdresse_poubelle());
        ps.setString(4, tache.getMessage());
        ps.setString(5, tache.getEtat());
        ps.executeUpdate();

    }

    @Override
    public void modifier(Tache tache) throws SQLException {
        String sql = "UPDATE `tache` SET `id_centre`=?, `id_employe`=?, `adresse_poubelle`=?, `message`=?, `etat`=? WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, tache.getId_centre());
        ps.setInt(2, tache.getId_employe());
        ps.setString(3, tache.getAdresse_poubelle());
        ps.setString(4, tache.getMessage());
        ps.setString(5, tache.getEtat());
        ps.setInt(6, tache.getId());
        ps.executeUpdate();
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String sql = "DELETE FROM `tache` WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();
    }

    @Override
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
                    rs.getString("adresse_poubelle"),
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
                    rs.getString("adresse_poubelle"),
                    rs.getString("message"),
                    rs.getString("etat")
            ));
        }
        return taches;
    }

}