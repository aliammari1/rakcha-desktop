package com.esprit.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataSource {
    private static DataSource instance;
    private final String URL = "jdbc:mysql://" + System.getenv("DATABASE_SERVER_NAME") + ":3306/"
            + System.getenv("DATABASE_NAME");
    private final String USER = System.getenv("DATABASE_USERNAME");
    private final String PASSWORD = System.getenv("DATABASE_PASSWORD");
    private Connection connection;

    private DataSource() {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connection a été établie");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return DataSource
     */
    public static DataSource getInstance() {
        if (instance == null) {
            instance = new DataSource();
        }
        return instance;
    }

    /**
     * @return Connection
     */
    public Connection getConnection() {
        return connection;
    }
}
