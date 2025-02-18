package com.example.ewaste.utils;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBase {

    private final String URL="jdbc:mysql://localhost:3306/e_waste";
    private final String USER="root";
    private final String PSW="";

    private Connection connection;
    private static DataBase instance;

    private DataBase(){
        try {
            connection = DriverManager.getConnection(URL,USER,PSW);
            System.out.println("Connected");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static DataBase getInstance(){
        if(instance == null)
            instance = new DataBase();
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}
