package com.example.ewaste.Controllers;

import com.example.ewaste.Utils.DotenvConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton class for database connection
 */
public class DataBaseConn {
    private static DataBaseConn instance;
    private Connection connection;

    private DataBaseConn() {
        try {
            // Load environment variables
            String dbUrl = DotenvConfig.get("DB_URL", "jdbc:mysql://localhost:3306/symfonymaindatabase");
            String dbUser = DotenvConfig.get("DB_USER", "root");
            String dbPassword = DotenvConfig.get("DB_PASSWORD", "");

            // Register JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Open a connection
            connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            System.out.println("Database connection established successfully");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }
    }

    public static synchronized DataBaseConn getInstance() {
        if (instance == null) {
            instance = new DataBaseConn();
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            // Check if connection is closed or invalid
            if (connection == null || connection.isClosed()) {
                // Reconnect
                String dbUrl = DotenvConfig.get("DB_URL", "jdbc:mysql://localhost:3306/symfonymaindatabase");
                String dbUser = DotenvConfig.get("DB_USER", "root");
                String dbPassword = DotenvConfig.get("DB_PASSWORD", "");
                connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
                System.out.println("Database connection re-established");
            }
        } catch (SQLException e) {
            System.err.println("Error checking database connection: " + e.getMessage());
        }
        return connection;
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed");
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
        }
    }
}
