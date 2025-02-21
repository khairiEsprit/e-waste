package com.example.ewaste.repository;

import com.example.ewaste.entities.Poubelle;
import com.example.ewaste.utils.DataBase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PoubelleRepository {
    private final Connection conn = DataBase.getInstance().getConnection();

    public List<Poubelle> getPoubellesByCenter(int centerId) {
        List<Poubelle> trashBins = new ArrayList<>();
        String sql = "SELECT id, latitude, longitude, fillLevel, isWorking, centerId FROM poubelle WHERE centerId = ?";

        try (conn;
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, centerId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Poubelle trash = new Poubelle(
                        rs.getInt("id"),
                        rs.getDouble("latitude"),
                        rs.getDouble("longitude"),
                        rs.getInt("fillLevel"),
                        rs.getBoolean("isWorking"),
                        rs.getInt("centerId")
                );
                trashBins.add(trash);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return trashBins;
    }
}
