package com.esprit.utils;

import java.util.logging.Logger;

import com.esprit.Config;
import com.esprit.config.HibernateConfig;

/**
 * Utility class for managing database connections and switching between
 * different database types.
 * Supports seamless switching between MySQL, SQLite, PostgreSQL, and H2.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class DatabaseManager {
    private static final Logger LOGGER = Logger.getLogger(DatabaseManager.class.getName());

    /**
     * Switch to MySQL database
     * 
     * @param host     MySQL host (default: localhost)
     * @param port     MySQL port (default: 3306)
     * @param database Database name (default: rakcha_db)
     * @param username MySQL username (default: root)
     * @param password MySQL password (default: empty)
     */
    public static void switchToMySQL(String host, String port, String database, String username, String password) {
        String url = String.format("jdbc:mysql://%s:%s/%s?serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true",
                host != null ? host : "localhost",
                port != null ? port : "3306",
                database != null ? database : "rakcha_db");

        updateDatabaseConfig(HibernateConfig.DatabaseType.MYSQL, url, username, password);
        LOGGER.info("Switched to MySQL database: " + url);
    }

    /**
     * Switch to MySQL database with default settings
     */
    public static void switchToMySQL() {
        switchToMySQL(null, null, null, "root", "");
    }

    /**
     * Switch to SQLite database
     * 
     * @param databasePath Path to SQLite database file (default:
     *                     data/rakcha.db)
     */
    public static void switchToSQLite(String databasePath) {
        String path = databasePath != null ? databasePath : "data/rakcha.db";
        String url = "jdbc:sqlite:" + path;

        updateDatabaseConfig(HibernateConfig.DatabaseType.SQLITE, url, "", "");
        LOGGER.info("Switched to SQLite database: " + url);
    }

    /**
     * Switch to SQLite database with default settings
     */
    public static void switchToSQLite() {
        switchToSQLite(null);
    }

    /**
     * Switch to PostgreSQL database
     * 
     * @param host     PostgreSQL host (default: localhost)
     * @param port     PostgreSQL port (default: 5432)
     * @param database Database name (default: rakcha_db)
     * @param username PostgreSQL username (default: postgres)
     * @param password PostgreSQL password (default: empty)
     */
    public static void switchToPostgreSQL(String host, String port, String database, String username, String password) {
        String url = String.format("jdbc:postgresql://%s:%s/%s",
                host != null ? host : "localhost",
                port != null ? port : "5432",
                database != null ? database : "rakcha_db");

        updateDatabaseConfig(HibernateConfig.DatabaseType.POSTGRESQL, url, username, password);
        LOGGER.info("Switched to PostgreSQL database: " + url);
    }

    /**
     * Switch to PostgreSQL database with default settings
     */
    public static void switchToPostgreSQL() {
        switchToPostgreSQL(null, null, null, "postgres", "");
    }

    /**
     * Switch to H2 in-memory database (for testing)
     */
    public static void switchToH2() {
        String url = "jdbc:h2:mem:rakcha_db;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE";
        updateDatabaseConfig(HibernateConfig.DatabaseType.H2, url, "", "");
        LOGGER.info("Switched to H2 in-memory database: " + url);
    }

    /**
     * Update database configuration
     */
    private static void updateDatabaseConfig(HibernateConfig.DatabaseType dbType, String url, String username,
            String password) {
        Config config = Config.getInstance();

        // Update configuration
        config.set("db.type", dbType.getName());
        config.set("db.url", url);
        config.set("db.user", username != null ? username : "");
        config.set("db.password", password != null ? password : "");

        // Set system property for Hibernate
        System.setProperty("db.type", dbType.getName());

        LOGGER.info("Database configuration updated. Application restart may be required for changes to take effect.");
    }

    /**
     * Get current database type
     */
    public static HibernateConfig.DatabaseType getCurrentDatabaseType() {
        return HibernateConfig.getCurrentDatabaseType();
    }

    /**
     * Get current database connection URL
     */
    public static String getCurrentDatabaseUrl() {
        Config config = Config.getInstance();
        return config.getOrDefault("db.url", "");
    }

    /**
     * Check if current database is MySQL
     */
    public static boolean isCurrentlyMySQL() {
        return getCurrentDatabaseType() == HibernateConfig.DatabaseType.MYSQL;
    }

    /**
     * Check if current database is SQLite
     */
    public static boolean isCurrentlySQLite() {
        return getCurrentDatabaseType() == HibernateConfig.DatabaseType.SQLITE;
    }

    /**
     * Check if current database is PostgreSQL
     */
    public static boolean isCurrentlyPostgreSQL() {
        return getCurrentDatabaseType() == HibernateConfig.DatabaseType.POSTGRESQL;
    }

    /**
     * Check if current database is H2
     */
    public static boolean isCurrentlyH2() {
        return getCurrentDatabaseType() == HibernateConfig.DatabaseType.H2;
    }

    /**
     * Test database connection with current settings
     */
    public static boolean testConnection() {
        try {
            DataSource.getInstance().getConnection();
            LOGGER.info("Database connection test successful");
            return true;
        } catch (Exception e) {
            LOGGER.severe("Database connection test failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Initialize database schema (create tables if they don't exist)
     */
    public static void initializeSchema() {
        HibernateConfig.initializeDatabase();
        LOGGER.info("Database schema initialization completed");
    }
}
