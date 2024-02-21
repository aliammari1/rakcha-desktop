package com.esprit.utils;

import java.sql.*;

public class DataSource {

    private Connection connection;
    private static DataSource instance;

    private final String URL = "jdbc:mysql://localhost:3306/rakcha.tn";
    private final String USER = "root";
    private  final String PASSWORD = "";

    private DataSource() {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connection a été établie");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static DataSource getInstance() {
        if(instance == null){
            instance = new DataSource();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}
