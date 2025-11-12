package com.esprit.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.slf4j.Slf4j;

/**
 * Utility class providing helper methods for the RAKCHA application. Contains
 * reusable functionality and common operations. Supports multiple database
 * types
 * including MySQL, SQLite, PostgreSQL, and H2.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
public class DataSource {
    private static final Logger LOGGER = Logger.getLogger(DataSource.class.getName());
    private static DataSource instance;
    private static final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

    private final String url;
    private final String user;
    private final String password;
    private Connection connection;

    private DataSource() {
        // Determine database type

        // Set default URL based on a database type
        this.url = System.getProperty("db.url", dotenv.get("DB_URL", ""));

        this.user = System.getProperty("db.user", dotenv.get("DB_USER", "root"));
        this.password = System.getProperty("db.password", dotenv.get("DB_PASSWORD", ""));

        // Create a data directory for SQLite if needed
        if (dotenv.get("DB_URL", "").toUpperCase().contains("SQLITE")) {
            createDataDirectoryIfNeeded();
        }


        try {
            this.connection = DriverManager.getConnection(this.url, this.user, this.password);
            log.info("Database connection established successfully");
        } catch (final SQLException e) {
            log.error(
                    "Failed to establish database connection", e);
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
            log.error("Failed to get database connection", e);
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
                log.warn("Error closing database connection", e);
            }

        }

    }


    /**
     * Create data directory for SQLite database if it doesn't exist
     */
    private void createDataDirectoryIfNeeded() {
        try {
            java.nio.file.Path dataDir = java.nio.file.Paths.get(System.getProperty("user.dir"), "data");
            if (!java.nio.file.Files.exists(dataDir)) {
                java.nio.file.Files.createDirectories(dataDir);
                log.info("Created data directory for SQLite: " + dataDir.toAbsolutePath());
            }

        } catch (Exception e) {
            log.warn("Failed to create data directory for SQLite", e);
        }

    }


    /**
     * Get database URL being used
     */
    public String getDatabaseUrl() {
        return this.url;
    }


    /**
     * Creates all required tables if they don't exist.
     * This method ensures database schema compatibility across different databases.
     */
    public void createTablesIfNotExists() {
        try {
            TableCreator tableCreator = new TableCreator(this.getConnection());
            tableCreator.createAllTablesIfNotExists();
            log.info("All tables created successfully or already exist");
        } catch (Exception e) {
            log.error("Error creating database tables", e);
            // Don't throw exception here to avoid breaking the application if tables
            // already exist
        }

    }

}

