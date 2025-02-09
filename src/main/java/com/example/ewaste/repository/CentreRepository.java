package com.example.ewaste.repository;

import com.example.ewaste.entities.Centre;
import com.example.ewaste.interfaces.IService;
import com.example.ewaste.utils.DataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class CentreRepository implements IService<Centre> {

    private Connection connection;
    public CentreRepository(){
        connection = DataBase.getInstance().getConnection();
    }
    @Override
    public void ajouter(Centre centre) throws SQLException {
        String sql = "INSERT INTO `centre`(`nom`, `adresse`, `telephone`, `email`) VALUES ('"+centre.getNom()+"','"+centre.getAdresse()+"','"+centre.getTelephone()+"','"+centre.getEmail()+"')";
        Statement statement = connection.createStatement();
        statement.executeUpdate(sql);
    }

    @Override
    public void modifier(Centre centre) throws SQLException {
        String sql = "UPDATE `centre` SET `nom`=?,`adresse`=?,`telephone`=?,`email`=?   WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, centre.getNom());
        ps.setString(2, centre.getAdresse());
        ps.setInt(3, centre.getTelephone());
        ps.setString(4, centre.getEmail());
        ps.setInt(5, centre.getId());

        ps.executeUpdate();

    }

    @Override
    public void supprimer(int id) throws SQLException {
        String sql = "DELETE FROM `centre` WHERE id =?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();

    }

    @Override
    public List<Centre> afficher() throws SQLException {
        List<Centre> centres = new ArrayList<>();
        String sql = " SELECT * FROM `centre`";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);
        while (rs.next()){
            centres.add(new Centre(rs.getInt("id"), rs.getString("nom"), rs.getString("adresse"), rs.getInt("telephone"), rs.getString("email")));
        }
        return centres;
    }
}
