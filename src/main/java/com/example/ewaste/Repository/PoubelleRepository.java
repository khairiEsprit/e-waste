package com.example.ewaste.Repository;

import com.example.ewaste.Controllers.DataBaseConn;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Repository for waste bin data
 */
public class PoubelleRepository {
    private final Connection connection;

    public PoubelleRepository() {
        this.connection = DataBaseConn.getInstance().getConnection();
    }

    /**
     * Get the last ten waste bins data formatted for OpenAI
     * @param limit Number of bins to retrieve
     * @return Formatted string with bin data
     */
    public String getLastTenPoubellesForOpenAI(int limit) {
        StringBuilder result = new StringBuilder();
        String query = "SELECT id, fill_level, status, latitude, longitude FROM poubelles ORDER BY id DESC LIMIT ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                int id = rs.getInt("id");
                int fillLevel = rs.getInt("fill_level");
                String status = rs.getString("status");
                double latitude = rs.getDouble("latitude");
                double longitude = rs.getDouble("longitude");
                
                // Convert coordinates to location name (simplified for this example)
                String location = getLocationFromCoordinates(latitude, longitude);
                
                result.append(String.format("Bin ID: %d, Fill Level: %d%%, Status: %s, Location: %s\n", 
                                           id, fillLevel, status, location));
            }
            
            return result.toString();
        } catch (SQLException e) {
            System.err.println("Error retrieving bin data: " + e.getMessage());
            return "Error retrieving bin data. Please check the database connection.";
        }
    }
    
    /**
     * Convert coordinates to location name (simplified)
     * In a real application, this would use a geocoding service
     */
    private String getLocationFromCoordinates(double latitude, double longitude) {
        // This is a simplified example
        // In a real application, you would use a geocoding service
        if (latitude > 36.7 && latitude < 36.9 && longitude > 10.1 && longitude < 10.3) {
            return "Ariana, Tunisia";
        } else if (latitude > 36.7 && latitude < 36.9 && longitude > 10.0 && longitude < 10.2) {
            return "Tunis, Tunisia";
        } else if (latitude > 35.7 && latitude < 35.9 && longitude > 10.5 && longitude < 10.7) {
            return "Sousse, Tunisia";
        } else {
            return "Unknown Location";
        }
    }
}
