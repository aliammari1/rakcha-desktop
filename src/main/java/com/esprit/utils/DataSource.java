package com.esprit.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DataSource {
    private static final Logger LOGGER = Logger.getLogger(DataSource.class.getName());
    private static DataSource instance;
    private final String URL = "jdbc:mysql://localhost:3306/rakcha";
    private final String USER = "root";
    private final String PASSWORD = "root";
    private Connection connection;

    private DataSource() {
        try {
            this.connection = DriverManager.getConnection(this.URL, this.USER, this.PASSWORD);
            DataSource.LOGGER.info("Connection has been established");
        } catch (final SQLException e) {
            DataSource.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * @return DataSource
     */
    public static DataSource getInstance() {
        if (null == DataSource.instance) {
            DataSource.instance = new DataSource();
        }
        return DataSource.instance;
    }

    /**
     * @return Connection
     */
    public Connection getConnection() {
        return this.connection;
    }
}
