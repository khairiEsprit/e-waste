package com.example.ewaste.Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBaseConn {

    private static final String URL = "jdbc:mysql://localhost:3306/e-waste";
    private static final String USER = "root";
    private static final String PSW = "";

    private Connection connection;
    private static DataBaseConn instance;

    private DataBaseConn() {
        try {
            connection = DriverManager.getConnection(URL, USER, PSW);
            System.out.println(" Connexion réussie à la base de données.");
        } catch (SQLException e) {
            System.err.println(" Erreur lors de la connexion à la base de données : " + e.getMessage());
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
            if (connection == null || connection.isClosed()) {
                System.out.println(" Connexion fermée, tentative de reconnexion...");
                connection = DriverManager.getConnection(URL, USER, PSW);
            }
        } catch (SQLException e) {
            System.err.println(" Impossible de rétablir la connexion : " + e.getMessage());
        }
        return connection;
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println(" Connexion à la base de données fermée.");
            }
        } catch (SQLException e) {
            System.err.println(" Erreur lors de la fermeture de la connexion : " + e.getMessage());
        }
    }
}
