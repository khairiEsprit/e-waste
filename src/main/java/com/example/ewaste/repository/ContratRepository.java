package com.example.ewaste.repository;



import com.example.ewaste.entities.Contrat;
import com.example.ewaste.interfaces.IService;
import com.example.ewaste.utils.DataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ContratRepository implements IService<Contrat> {
    private Connection connection;
    public ContratRepository() {
        connection = DataBase.getInstance().getConnection();
    }
    @Override
    public void ajouter(Contrat contrat) throws SQLException {
        String sql = "INSERT INTO `contrat`(`idCentre`, `idCitoyen`, `dateDebut`, `dateFin`) VALUES ('"+contrat.getIdCentre()+"','"+contrat.getIdCitoyen()+"','"+contrat.getDateDebut()+"','"+contrat.getDateFin()+"')";
        Statement statement = connection.createStatement();
        statement.executeUpdate(sql);
    }

    @Override
    public void modifier(Contrat contrat) throws SQLException {
        String sql = "UPDATE `contrat` SET `idCentre`=?,`idCitoyen`=?,`dateDebut`=?,`dateFin`=?   WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, contrat.getIdCentre());
        ps.setInt(2, contrat.getIdCitoyen());
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
    public List<Contrat> afficher() throws SQLException {
        List<Contrat> centres = new ArrayList<>();
        String sql = " SELECT * FROM `contrat`";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);
        while (rs.next()){
            centres.add(new Contrat(rs.getInt("id"), rs.getInt("idCentre"), rs.getInt("idCitoyen"), rs.getDate("dateDebut").toLocalDate(), rs.getDate("dateFin").toLocalDate()));
        }
        return centres;
    }
    }

