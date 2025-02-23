package com.example.ewaste.Repository;

import com.example.ewaste.Entities.Poubelle;
import com.example.ewaste.Utils.DataBase;

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

    public String getLastTenPoubellesForOpenAI(int centerId) {
        List<Poubelle> trashBins = new ArrayList<>();
        String sql = "SELECT id, latitude, longitude, fillLevel, isWorking, centerId " +
                "FROM poubelle " +
                "WHERE centerId = ? " +
                "ORDER BY id DESC LIMIT 10"; // Ensure we get the last 10 records

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
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

        // Format the data in a clear structure acceptable for OpenAI
        StringBuilder sb = new StringBuilder();
        for (Poubelle p : trashBins) {
            sb.append(String.format("Bin ID %d: fillLevel=%d%%, isWorking=%s, position=(%.6f, %.6f)%n",
                    p.getId(),
                    p.getFillLevel(),
                    p.isWorking() ? "true" : "false",
                    p.getLatitude(),
                    p.getLongitude()));
        }
        return sb.toString();
    }
}
