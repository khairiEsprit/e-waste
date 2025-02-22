package com.example.ewaste.Repository;

import com.example.ewaste.Utils.DataBase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CentreRepository {
    private final Connection conn = DataBase.getInstance().getConnection();


    public float[] getLatitudeLongitude(int id) throws SQLException {
        String query = "SELECT latitude, langitude FROM centre WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    float latitude = rs.getFloat("latitude");
                    float longitude = rs.getFloat("langitude");
                    return new float[]{latitude, longitude};
                } else {
                    return null;
                }
            }
        }
    }


}
