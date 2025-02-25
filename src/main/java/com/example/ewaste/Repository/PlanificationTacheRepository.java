package com.example.ewaste.Repository;

import com.example.ewaste.Interfaces.IService;
import com.example.ewaste.Entities.PlanificationTache;
import com.example.ewaste.Utils.DataBase;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class PlanificationTacheRepository implements IService<PlanificationTache> {
    private Connection connection;

    public PlanificationTacheRepository() {
        connection = DataBase.getInstance().getConnection();
    }

    @Override
    public void ajouter(PlanificationTache planificationTache) throws SQLException {
        String sql = "INSERT INTO planificationtache(id_tache, priorite, date_limite) VALUES (?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, planificationTache.getId_tache());
        ps.setString(2, planificationTache.getPriorite());

        // Conversion String en java.sql.Date avant insertion
        Date sqlDate = convertirStringEnDate(planificationTache.getDate_limite().toString());
        ps.setDate(3, sqlDate);

        ps.executeUpdate();
    }

    @Override
    public void modifier(PlanificationTache planificationTache) throws SQLException {
        String sql = "UPDATE planificationtache SET id_tache=?, priorite=?, date_limite=? WHERE id=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, planificationTache.getId_tache());
        ps.setString(2, planificationTache.getPriorite());

        // Conversion String en java.sql.Date avant mise à jour
        Date sqlDate = convertirStringEnDate(planificationTache.getDate_limite().toString());
        ps.setDate(3, sqlDate);

        ps.setInt(4, planificationTache.getId());
        ps.executeUpdate();
    }

    @Override
    public List<PlanificationTache> recuperer() throws SQLException {
        return List.of();
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String sql = "DELETE FROM planificationtache WHERE id=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();
    }

    @Override
    public List<PlanificationTache> afficher(int id_centre) throws SQLException {
        List<PlanificationTache> planifications = new ArrayList<>();
        String sql = "SELECT * FROM planificationtache";
        PreparedStatement ps = connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            planifications.add(new PlanificationTache(
                    rs.getInt("id"),
                    rs.getInt("id_tache"),
                    rs.getString("priorite"),
                    rs.getDate("date_limite")
            ));
        }
        return planifications;
    }

    private Date convertirStringEnDate(String dateString) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date utilDate = format.parse(dateString);
            return new Date(utilDate.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public PlanificationTache getByIdTache(int idTache) throws SQLException {
        String sql = "SELECT * FROM planificationtache WHERE id_tache = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, idTache);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return new PlanificationTache(
                    rs.getInt("id"),
                    rs.getInt("id_tache"),
                    rs.getString("priorite"),
                    rs.getDate("date_limite")
            );
        }
        return null;
    }

    public PlanificationTache afficherPlannification() throws SQLException {
        String sql = "SELECT * FROM planificationtache";
        PreparedStatement ps = connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return new PlanificationTache(
                    rs.getInt("id"),
                    rs.getInt("id_tache"),
                    rs.getString("priorite"),
                    rs.getDate("date_limite")
            );
        }
        return null;
    }
}
