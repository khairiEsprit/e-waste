package com.example.ewaste.Repository;

import com.example.ewaste.Entities.Event;
import com.example.ewaste.Utils.DataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EventRepository {
    private final Connection conn = DataBase.getInstance().getConnection();

    public List<Event> getEvents() {
        List<Event> events = new ArrayList<>();
        String query = "SELECT * FROM event";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Event event = new Event(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("imageUrl"),
                        rs.getInt("remainingPlaces"),
                        rs.getString("location"),
                        rs.getDate("date").toLocalDate() // Convertir java.sql.Date en LocalDate
                );
                events.add(event);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }

    public void addEvent(Event event) {
        String query = "INSERT INTO event (title, description, imageUrl, remainingPlaces, location, date) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, event.getTitle());
            pstmt.setString(2, event.getDescription());
            pstmt.setString(3, event.getImageUrl());
            pstmt.setInt(4, event.getRemainingPlaces());
            pstmt.setString(5, event.getLocation());
            pstmt.setDate(6, java.sql.Date.valueOf(event.getDate())); // Convertir LocalDate en java.sql.Date
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateEvent(Event event) {
        String query = "UPDATE event SET title=?, description=?, imageUrl=?, remainingPlaces=?, location=?, date=? WHERE id=?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, event.getTitle());
            pstmt.setString(2, event.getDescription());
            pstmt.setString(3, event.getImageUrl());
            pstmt.setInt(4, event.getRemainingPlaces());
            pstmt.setString(5, event.getLocation());
            pstmt.setDate(6, java.sql.Date.valueOf(event.getDate())); // Convertir LocalDate en java.sql.Date
            pstmt.setInt(7, event.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteEvent(int eventId) {
        String query = "DELETE FROM event WHERE id=?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, eventId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}