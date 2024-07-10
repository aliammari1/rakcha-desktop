package com.esprit.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.esprit.services.produits.AvisService;

public class DataSource {
    private static DataSource instance;
    private final String URL = "jdbc:mysql://mysql-rakcha.alwaysdata.net:3306/rakcha_db";
    private final String USER = "rakcha";
    private final String PASSWORD = "rakchaRootPass";
    private Connection connection;
    private static final Logger LOGGER = Logger.getLogger(DataSource.class.getName());

    private DataSource() {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            LOGGER.info("Connection a été établie");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
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
