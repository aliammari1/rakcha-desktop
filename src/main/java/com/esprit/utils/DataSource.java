package com.esprit.utils;

import com.esprit.Config;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DataSource {
    private static final Logger LOGGER = Logger.getLogger(DataSource.class.getName());
    private static DataSource instance;
    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/rakcha";
    private final String url;
    private final String user;
    private final String password;
    private Connection connection;

    private DataSource() {
        Config config = Config.getInstance();
        this.url = config.getOrDefault("db.url", DEFAULT_URL);
        this.user = config.getOrDefault("db.user", "root");
        this.password = config.getOrDefault("db.password", "");

        try {
            this.connection = DriverManager.getConnection(this.url, this.user, this.password);
            DataSource.LOGGER.info("Database connection established");
        } catch (final SQLException e) {
            DataSource.LOGGER.log(Level.SEVERE, "Failed to establish database connection", e);
            throw new RuntimeException("Database connection failed", e);
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
        try {
            if (this.connection == null || this.connection.isClosed()) {
                this.connection = DriverManager.getConnection(this.url, this.user, this.password);
            }
        } catch (SQLException e) {
            DataSource.LOGGER.log(Level.SEVERE, "Failed to get database connection", e);
            throw new RuntimeException("Failed to get database connection", e);
        }
        return this.connection;
    }

    /**
     * Close the database connection
     */
    public void closeConnection() {
        if (this.connection != null) {
            try {
                this.connection.close();
            } catch (SQLException e) {
                DataSource.LOGGER.log(Level.WARNING, "Error closing database connection", e);
            }
        }
    }
}
