package com.esprit.examples;

import java.util.logging.Logger;

import com.esprit.utils.DatabaseManager;

/**
 * Example class demonstrating how to use MySQL and SQLite databases in the
 * RAKCHA application.
 * Shows how to switch between database types and perform basic operations.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class DatabaseExample {
    private static final Logger LOGGER = Logger.getLogger(DatabaseExample.class.getName());

    /**
     * Main method to demonstrate database switching functionality
     */
    public static void main(String[] args) {
        LOGGER.info("=== RAKCHA Database Example ===");

        // Example 1: Check current database type
        demonstrateCurrentDatabase();

        // Example 2: Switch to SQLite
        demonstrateSQLiteUsage();

        // Example 3: Switch to MySQL
        demonstrateMySQLUsage();

        // Example 4: Database type detection
        demonstrateDatabaseDetection();
    }

    /**
     * Show current database configuration
     */
    private static void demonstrateCurrentDatabase() {
        LOGGER.info("\n--- Current Database Configuration ---");
        LOGGER.info("Database Type: " + DatabaseManager.getCurrentDatabaseType().getName());
        LOGGER.info("Database URL: " + DatabaseManager.getCurrentDatabaseUrl());
        LOGGER.info("Is MySQL: " + DatabaseManager.isCurrentlyMySQL());
        LOGGER.info("Is SQLite: " + DatabaseManager.isCurrentlySQLite());
        LOGGER.info("Is PostgreSQL: " + DatabaseManager.isCurrentlyPostgreSQL());
        LOGGER.info("Is H2: " + DatabaseManager.isCurrentlyH2());
    }

    /**
     * Demonstrate SQLite database usage
     */
    private static void demonstrateSQLiteUsage() {
        LOGGER.info("\n--- SQLite Database Demo ---");

        // Switch to SQLite with default settings
        DatabaseManager.switchToSQLite();

        // Or switch with custom path
        // DatabaseManager.switchToSQLite("data/my_custom_db.sqlite");

        // Test connection
        if (DatabaseManager.testConnection()) {
            LOGGER.info("SQLite connection successful!");

            // Initialize schema
            DatabaseManager.initializeSchema();

            // Your SQLite-specific operations here
            LOGGER.info("SQLite is perfect for:");
            LOGGER.info("- Development and testing");
            LOGGER.info("- Lightweight applications");
            LOGGER.info("- Single-user scenarios");
            LOGGER.info("- Embedded systems");
        }
    }

    /**
     * Demonstrate MySQL database usage
     */
    private static void demonstrateMySQLUsage() {
        LOGGER.info("\n--- MySQL Database Demo ---");

        // Switch to MySQL with default settings (localhost:3306)
        DatabaseManager.switchToMySQL();

        // Or switch with custom settings
        // DatabaseManager.switchToMySQL("localhost", "3306", "rakcha_db", "root",
        // "password");

        // Test connection
        if (DatabaseManager.testConnection()) {
            LOGGER.info("MySQL connection successful!");

            // Initialize schema
            DatabaseManager.initializeSchema();

            // Your MySQL-specific operations here
            LOGGER.info("MySQL is perfect for:");
            LOGGER.info("- Production environments");
            LOGGER.info("- Multi-user applications");
            LOGGER.info("- High-performance requirements");
            LOGGER.info("- Complex queries and transactions");
        } else {
            LOGGER.warning("MySQL connection failed. Make sure MySQL server is running.");
            LOGGER.info("To start MySQL, use the VS Code task 'Start MySQL' or run:");
            LOGGER.info("C:/xampp/mysql/bin/mysqld.exe --defaults-file=C:/xampp/mysql/bin/my.ini --standalone");
        }
    }

    /**
     * Demonstrate database type detection and switching
     */
    private static void demonstrateDatabaseDetection() {
        LOGGER.info("\n--- Database Type Detection ---");

        // Check environment variable DB_TYPE
        String dbType = System.getenv("DB_TYPE");
        if (dbType != null) {
            LOGGER.info("DB_TYPE environment variable: " + dbType);
        } else {
            LOGGER.info("DB_TYPE not set, using default (H2)");
        }

        // Switch based on system property
        String systemDbType = System.getProperty("db.type");
        if (systemDbType != null) {
            LOGGER.info("System property db.type: " + systemDbType);

            switch (systemDbType.toLowerCase()) {
                case "mysql":
                    DatabaseManager.switchToMySQL();
                    break;
                case "sqlite":
                    DatabaseManager.switchToSQLite();
                    break;
                case "postgresql":
                    DatabaseManager.switchToPostgreSQL();
                    break;
                case "h2":
                    DatabaseManager.switchToH2();
                    break;
                default:
                    LOGGER.warning("Unknown database type: " + systemDbType);
            }
        }

        // Show final configuration
        demonstrateCurrentDatabase();
    }

    /**
     * Example of how to set database type programmatically for different
     * environments
     */
    public static void configureForEnvironment(String environment) {
        LOGGER.info("Configuring database for environment: " + environment);

        switch (environment.toLowerCase()) {
            case "development":
                // Use SQLite for development
                DatabaseManager.switchToSQLite();
                LOGGER.info("Development environment: Using SQLite");
                break;

            case "testing":
                // Use H2 in-memory for testing
                DatabaseManager.switchToH2();
                LOGGER.info("Testing environment: Using H2 in-memory");
                break;

            case "production":
                // Use MySQL for production
                DatabaseManager.switchToMySQL("localhost", "3306", "rakcha_db", "root", "production_password");
                LOGGER.info("Production environment: Using MySQL");
                break;

            default:
                LOGGER.warning("Unknown environment: " + environment + ". Using default H2.");
                DatabaseManager.switchToH2();
        }
    }
}
