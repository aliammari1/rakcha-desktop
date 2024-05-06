package com.esprit.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataSource {

    private static DataSource instance;
    private final String URL = "jdbc:mysql://localhost:3306/rakcha";
    private final String USER = "root";
    private final String PASSWORD = "";
    private Connection connection;

    private DataSource() {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connection a été établie");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static DataSource getInstance() {
        if (instance == null) {
            instance = new DataSource();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}
