package com.example.ewaste.Utils;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBase {

    // Local database connection
    private static final String URL = "jdbc:mysql://localhost:3306/symfonymaindatabase";
    private static final String USER = "root";
    private static final String PSW = ""; // Update this if your root user has a password

    // Remote database connection (based on user memory)
    // private static final String URL = "jdbc:mysql://172.18.5.39:3306/symfonymaindatabase";
    // private static final String USER = "khairi";
    // private static final String PSW = "root";

    private static DataBase instance;
    private static boolean isConnected = false;

    private DataBase() {
        try {
            // Test the connection during initialization
            Connection testConnection = DriverManager.getConnection(URL, USER, PSW);
            testConnection.close();
            isConnected = true;
            System.out.println("Connected to database successfully");
        } catch (Exception e) {
            isConnected = false;
            System.err.println("Database connection failed: " + e.getMessage());
        }
    }

    public static DataBase getInstance() {
        if (instance == null) {
            instance = new DataBase();
        }
        return instance;
    }

    public static Connection getConnection() {
        try {
            // Try to establish a connection
            Connection conn = DriverManager.getConnection(URL, USER, PSW);

            // Test if the connection is valid
            if (conn.isValid(5)) { // 5 second timeout
                return conn;
            } else {
                throw new SQLException("Database connection is invalid");
            }
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
            throw new RuntimeException("Error getting database connection", e);
        }
    }

    public static boolean isConnected() {
        return isConnected;
    }
}