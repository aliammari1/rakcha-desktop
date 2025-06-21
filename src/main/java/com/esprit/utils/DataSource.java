package com.esprit.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.esprit.Config;
import com.esprit.config.HibernateConfig;

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
public class DataSource {
    private static final Logger LOGGER = Logger.getLogger(DataSource.class.getName());
    private static DataSource instance;
    // Default URLs for different database types
    private static final String DEFAULT_MYSQL_URL = "jdbc:mysql://localhost:3306/rakcha_db?serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true";
    private static final String DEFAULT_SQLITE_URL = "jdbc:sqlite:data/rakcha_db.sqlite";
    private static final String DEFAULT_H2_URL = "jdbc:h2:mem:rakcha_db;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE";
    private static final String DEFAULT_POSTGRESQL_URL = "jdbc:postgresql://localhost:5432/rakcha_db";

    private final String url;
    private final String user;
    private final String password;
    private final HibernateConfig.DatabaseType databaseType;
    private Connection connection;

    private DataSource() {
        Config config = Config.getInstance();

        // Determine database type
        this.databaseType = HibernateConfig.getCurrentDatabaseType();

        // Set default URL based on database type
        String defaultUrl = getDefaultUrlForDatabaseType(this.databaseType);
        this.url = config.getOrDefault("db.url", defaultUrl);

        // Set credentials based on database type
        if (this.databaseType == HibernateConfig.DatabaseType.SQLITE) {
            this.user = ""; // SQLite doesn't use username/password
            this.password = "";
        } else {
            this.user = config.getOrDefault("db.user", "root");
            this.password = config.getOrDefault("db.password", "");
        }

        // Create data directory for SQLite if needed
        if (this.databaseType == HibernateConfig.DatabaseType.SQLITE) {
            createDataDirectoryIfNeeded();
        }

        try {
            this.connection = DriverManager.getConnection(this.url, this.user, this.password);
            DataSource.LOGGER.info("Database connection established successfully for: " + this.databaseType.getName());
        } catch (final SQLException e) {
            DataSource.LOGGER.log(Level.SEVERE,
                    "Failed to establish database connection for: " + this.databaseType.getName(), e);
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

    /**
     * Get default URL based on database type
     */
    private String getDefaultUrlForDatabaseType(HibernateConfig.DatabaseType databaseType) {
        switch (databaseType) {
            case MYSQL:
                return DEFAULT_MYSQL_URL;
            case SQLITE:
                return DEFAULT_SQLITE_URL;
            case POSTGRESQL:
                return DEFAULT_POSTGRESQL_URL;
            case H2:
                return DEFAULT_H2_URL;
            default:
                return DEFAULT_H2_URL; // Fallback to H2
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
                DataSource.LOGGER.info("Created data directory for SQLite: " + dataDir.toAbsolutePath());
            }
        } catch (Exception e) {
            DataSource.LOGGER.log(Level.WARNING, "Failed to create data directory for SQLite", e);
        }
    }

    /**
     * Get current database type being used
     */
    public HibernateConfig.DatabaseType getDatabaseType() {
        return this.databaseType;
    }

    /**
     * Get database URL being used
     */
    public String getDatabaseUrl() {
        return this.url;
    }

    /**
     * Check if the connection is using SQLite
     */
    public boolean isSQLite() {
        return this.databaseType == HibernateConfig.DatabaseType.SQLITE;
    }

    /**
     * Check if the connection is using MySQL
     */
    public boolean isMySQL() {
        return this.databaseType == HibernateConfig.DatabaseType.MYSQL;
    }
}
