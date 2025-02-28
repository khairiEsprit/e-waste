package com.example.ewaste.Utils;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBase {

    private static final String URL = "jdbc:mysql://localhost:3306/e-waste";
    private static final String USER = "root";
    private static final String PSW = "";

    private static DataBase instance;

    private DataBase() {
        try {
            System.out.println("Connected");
        } catch (Exception e) {
            System.out.println(e.getMessage());
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
            return DriverManager.getConnection(URL, USER, PSW);
        } catch (SQLException e) {
            throw new RuntimeException("Error getting database connection", e);
        }
    }
}