package com.example.ewaste.Repository;

import com.example.ewaste.Entities.Event;
import com.example.ewaste.Utils.DataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EventRepository {
    private final Connection conn = DataBase.getConnection();

    public List<Event> getEvents() {
        List<Event> events = new ArrayList<>();

        // Try to determine which table to use
        String tableName = "event"; // Default table name

        try {
            // Check if the table exists
            DatabaseMetaData dbm = conn.getMetaData();
            ResultSet tables = dbm.getTables(null, null, tableName, null);

            if (!tables.next()) {
                // If event table doesn't exist, try evenements
                tableName = "evenements";
                tables = dbm.getTables(null, null, tableName, null);
                if (!tables.next()) {
                    throw new SQLException("Neither 'event' nor 'evenements' table exists");
                }
            }

            String query = "SELECT * FROM " + tableName;
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    Event event = mapResultSetToEvent(rs);
                    events.add(event);
                }
                System.out.println("Retrieved " + events.size() + " events from " + tableName + " table");
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving events: " + e.getMessage());
            e.printStackTrace();
        }

        return events;
    }

    // Helper method to map ResultSet to Event object
    private Event mapResultSetToEvent(ResultSet rs) throws SQLException {
        Event event = new Event();
        event.setId(rs.getInt("id"));
        event.setTitle(rs.getString("title"));
        event.setDescription(rs.getString("description"));

        // Try to get image_name first, if it fails, try imageUrl
        try {
            event.setImageName(rs.getString("image_name"));
        } catch (SQLException e) {
            try {
                event.setImageName(rs.getString("imageUrl"));
            } catch (SQLException e2) {
                // If both fail, set to null
                event.setImageName(null);
                System.err.println("Warning: Neither image_name nor imageUrl column found in database");
            }
        }

        event.setRemainingPlaces(rs.getInt("remaining_places"));
        event.setLocation(rs.getString("location"));

        // Convert Timestamp to LocalDateTime
        Timestamp dateTimestamp = rs.getTimestamp("date");
        if (dateTimestamp != null) {
            event.setDate(dateTimestamp.toLocalDateTime());
        }

        // Try to get participation_mode, if it fails, set default
        try {
            event.setParticipationMode(rs.getString("participation_mode"));
        } catch (SQLException e) {
            event.setParticipationMode("on-site");
        }

        // Try to get google_meet_link, if it fails, set to null
        try {
            event.setGoogleMeetLink(rs.getString("google_meet_link"));
        } catch (SQLException e) {
            event.setGoogleMeetLink(null);
        }

        return event;
    }

    public void addEvent(Event event) {
        // Try to determine if the table uses image_name or imageUrl column
        String imageColumnName = "image_name"; // Default for this table
        String tableName = "event"; // Default table name

        try {
            // Check if the table exists
            DatabaseMetaData dbm = conn.getMetaData();
            ResultSet tables = dbm.getTables(null, null, tableName, null);

            if (!tables.next()) {
                // If event table doesn't exist, try evenements
                tableName = "evenements";
                tables = dbm.getTables(null, null, tableName, null);
                if (!tables.next()) {
                    throw new SQLException("Neither 'event' nor 'evenements' table exists");
                }
            }

            // Now check for column names in the table
            ResultSet columns = dbm.getColumns(null, null, tableName, "image_name");
            if (!columns.next()) {
                // If image_name doesn't exist, try imageUrl
                columns = dbm.getColumns(null, null, tableName, "imageUrl");
                if (columns.next()) {
                    imageColumnName = "imageUrl";
                } else {
                    System.err.println("Warning: Neither image_name nor imageUrl column found in " + tableName);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking table structure: " + e.getMessage());
            // Continue with defaults
        }

        String query = "INSERT INTO " + tableName + " (title, description, " + imageColumnName + ", remaining_places, location, date, participation_mode, google_meet_link) " +
                       "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, event.getTitle());
            pstmt.setString(2, event.getDescription());
            pstmt.setString(3, event.getImageName());
            pstmt.setInt(4, event.getRemainingPlaces());
            pstmt.setString(5, event.getLocation());
            pstmt.setTimestamp(6, event.getDate() != null ?
                Timestamp.valueOf(event.getDate()) : Timestamp.valueOf(java.time.LocalDateTime.now()));
            pstmt.setString(7, event.getParticipationMode() != null ?
                event.getParticipationMode() : "on-site");
            pstmt.setString(8, event.getGoogleMeetLink());
            pstmt.executeUpdate();
            System.out.println("Event added successfully to " + tableName + " table");
        } catch (SQLException e) {
            System.err.println("Error adding event: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void updateEvent(Event event) {
        // Try to determine if the table uses image_name or imageUrl column
        String imageColumnName = "image_name"; // Default for this table
        String tableName = "event"; // Default table name

        try {
            // Check if the table exists
            DatabaseMetaData dbm = conn.getMetaData();
            ResultSet tables = dbm.getTables(null, null, tableName, null);

            if (!tables.next()) {
                // If event table doesn't exist, try evenements
                tableName = "evenements";
                tables = dbm.getTables(null, null, tableName, null);
                if (!tables.next()) {
                    throw new SQLException("Neither 'event' nor 'evenements' table exists");
                }
            }

            // Now check for column names in the table
            ResultSet columns = dbm.getColumns(null, null, tableName, "image_name");
            if (!columns.next()) {
                // If image_name doesn't exist, try imageUrl
                columns = dbm.getColumns(null, null, tableName, "imageUrl");
                if (columns.next()) {
                    imageColumnName = "imageUrl";
                } else {
                    System.err.println("Warning: Neither image_name nor imageUrl column found in " + tableName);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking table structure: " + e.getMessage());
            // Continue with defaults
        }

        String query = "UPDATE " + tableName + " SET title=?, description=?, " + imageColumnName + "=?, remaining_places=?, location=?, date=?, " +
                       "participation_mode=?, google_meet_link=? WHERE id=?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, event.getTitle());
            pstmt.setString(2, event.getDescription());
            pstmt.setString(3, event.getImageName());
            pstmt.setInt(4, event.getRemainingPlaces());
            pstmt.setString(5, event.getLocation());
            pstmt.setTimestamp(6, event.getDate() != null ?
                Timestamp.valueOf(event.getDate()) : Timestamp.valueOf(java.time.LocalDateTime.now()));
            pstmt.setString(7, event.getParticipationMode() != null ?
                event.getParticipationMode() : "on-site");
            pstmt.setString(8, event.getGoogleMeetLink());
            pstmt.setInt(9, event.getId());
            pstmt.executeUpdate();
            System.out.println("Event updated successfully in " + tableName + " table");
        } catch (SQLException e) {
            System.err.println("Error updating event: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Event getEventById(int id) {
        // Try to determine which table to use
        String tableName = "event"; // Default table name

        try {
            // Check if the table exists
            DatabaseMetaData dbm = conn.getMetaData();
            ResultSet tables = dbm.getTables(null, null, tableName, null);

            if (!tables.next()) {
                // If event table doesn't exist, try evenements
                tableName = "evenements";
                tables = dbm.getTables(null, null, tableName, null);
                if (!tables.next()) {
                    throw new SQLException("Neither 'event' nor 'evenements' table exists");
                }
            }

            String query = "SELECT * FROM " + tableName + " WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, id);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    Event event = mapResultSetToEvent(rs);
                    System.out.println("Retrieved event with ID " + id + " from " + tableName + " table");
                    return event;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving event by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public void deleteEvent(int eventId) {
        // Try to determine which table to use
        String tableName = "event"; // Default table name

        try {
            // Check if the table exists
            DatabaseMetaData dbm = conn.getMetaData();
            ResultSet tables = dbm.getTables(null, null, tableName, null);

            if (!tables.next()) {
                // If event table doesn't exist, try evenements
                tableName = "evenements";
                tables = dbm.getTables(null, null, tableName, null);
                if (!tables.next()) {
                    throw new SQLException("Neither 'event' nor 'evenements' table exists");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking table structure: " + e.getMessage());
            // Continue with default
        }

        String query = "DELETE FROM " + tableName + " WHERE id=?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, eventId);
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Deleted " + rowsAffected + " event(s) from " + tableName + " table");
        } catch (SQLException e) {
            System.err.println("Error deleting event: " + e.getMessage());
            e.printStackTrace();
        }
    }
}