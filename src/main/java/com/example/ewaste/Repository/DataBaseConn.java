package com.example.ewaste.Repository;

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
            // Load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Get database configuration from environment variables
            String dbUrl = DotenvConfig.get("DB_URL", "jdbc:mysql://localhost:3306/ewaste");
            String dbUser = DotenvConfig.get("DB_USER", "root");
            String dbPassword = DotenvConfig.get("DB_PASSWORD", "");

            // Create the connection
            connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            System.out.println("Database connection established successfully");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found: " + e.getMessage());
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Error connecting to database: " + e.getMessage());
            e.printStackTrace();

            // Try with default values as fallback
            try {
                connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/ewaste",
                    "root",
                    ""
                );
                System.out.println("Database connection established with default values");
            } catch (SQLException ex) {
                System.err.println("Failed to connect with default values: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    /**
     * Get the singleton instance
     * @return The DataBaseConn instance
     */
    public static synchronized DataBaseConn getInstance() {
        if (instance == null) {
            instance = new DataBaseConn();
        }
        return instance;
    }

    /**
     * Get the database connection
     * @return The Connection object
     */
    public Connection getConnection() {
        try {
            // Check if connection is closed or invalid
            if (connection == null || connection.isClosed()) {
                // Try to reconnect
                String dbUrl = DotenvConfig.get("DB_URL", "jdbc:mysql://localhost:3306/symfonymaindatabase");
                String dbUser = DotenvConfig.get("DB_USER", "root");
                String dbPassword = DotenvConfig.get("DB_PASSWORD", "");

                connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
                System.out.println("Database connection re-established");
            }
        } catch (SQLException e) {
            System.err.println("Error checking database connection: " + e.getMessage());
            e.printStackTrace();
        }

        return connection;
    }

    /**
     * Close the database connection
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed");
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
