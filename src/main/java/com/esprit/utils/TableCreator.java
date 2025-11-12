package com.esprit.utils;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

/**
 * Utility class for creating database tables with cross-database compatibility.
 * Supports PostgreSQL, MySQL, and SQLite with appropriate SQL syntax for each.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
public class TableCreator {

    private final Connection connection;
    private final DatabaseType databaseType;

    public enum DatabaseType {
        MYSQL, POSTGRESQL, SQLITE, UNKNOWN
    }


    public TableCreator(Connection connection) {
        this.connection = connection;
        this.databaseType = detectDatabaseType();
    }


    /**
     * Detects the database type based on the connection URL
     */
    private DatabaseType detectDatabaseType() {
        try {
            DatabaseMetaData metaData = connection.getMetaData();
            String url = metaData.getURL().toLowerCase();

            if (url.contains("mysql") || url.contains("mariadb")) {
                return DatabaseType.MYSQL;
            }
 else if (url.contains("postgresql")) {
                return DatabaseType.POSTGRESQL;
            }
 else if (url.contains("sqlite")) {
                return DatabaseType.SQLITE;
            }

        }
 catch (SQLException e) {
            log.warn("Could not detect database type", e);
        }

        return DatabaseType.UNKNOWN;
    }


    /**
     * Checks if a table exists in the database
     */
    public boolean tableExists(String tableName) {
        try {
            DatabaseMetaData metaData = connection.getMetaData();
            String tableNameToCheck = tableName;

            // PostgreSQL stores table names in lowercase by default
            if (databaseType == DatabaseType.POSTGRESQL) {
                tableNameToCheck = tableName.toLowerCase();
            }


            try (ResultSet tables = metaData.getTables(null, null, tableNameToCheck, new String[] { "TABLE" }
)) {
                return tables.next();
            }

        }
 catch (SQLException e) {
            log.error("Error checking if table exists: " + tableName, e);
            return false;
        }

    }


    /**
     * Creates a table if it doesn't exist using database-specific SQL
     */
    public void createTableIfNotExists(String tableName, String createTableSQL) {
        if (tableExists(tableName)) {
            log.info("Table {}
 already exists, skipping creation", tableName);
            return;
        }


        String sql = adaptSQLForDatabase(createTableSQL);

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
            log.info("Successfully created table: {}
", tableName);
        }
 catch (SQLException e) {
            log.error("Error creating table: " + tableName, e);
            throw new RuntimeException("Failed to create table: " + tableName, e);
        }

    }


    /**
     * Adapts SQL syntax for the specific database type
     */
    private String adaptSQLForDatabase(String sql) {
        switch (databaseType) {
            case POSTGRESQL:
                return adaptForPostgreSQL(sql);
            case MYSQL:
                return adaptForMySQL(sql);
            case SQLITE:
                return adaptForSQLite(sql);
            default:
                log.warn("Unknown database type, using original SQL");
                return sql;
        }

    }


    /**
     * Adapts SQL for PostgreSQL
     */
    private String adaptForPostgreSQL(String sql) {
        return sql
                // Replace MySQL AUTO_INCREMENT with PostgreSQL SERIAL
                .replaceAll("(?i)\\s+AUTO_INCREMENT\\s*", " ")
                .replaceAll("(?i)\\bINT\\s*\\(\\d+\\)\\s+AUTO_INCREMENT\\b", "SERIAL")
                .replaceAll("(?i)\\bINT\\s+AUTO_INCREMENT\\b", "SERIAL")
                .replaceAll("(?i)\\bBIGINT\\s*\\(\\d+\\)\\s+AUTO_INCREMENT\\b", "BIGSERIAL")
                .replaceAll("(?i)\\bBIGINT\\s+AUTO_INCREMENT\\b", "BIGSERIAL")

                // Remove MySQL-specific length specifiers
                .replaceAll("(?i)\\bINT\\s*\\(\\d+\\)", "INTEGER")
                .replaceAll("(?i)\\bBIGINT\\s*\\(\\d+\\)", "BIGINT")
                .replaceAll("(?i)\\bTINYINT\\s*\\(\\d+\\)", "SMALLINT")

                // Replace MySQL TEXT types with PostgreSQL equivalents
                .replaceAll("(?i)\\bLONGTEXT\\b", "TEXT")
                .replaceAll("(?i)\\bMEDIUMTEXT\\b", "TEXT")
                .replaceAll("(?i)\\bTINYTEXT\\b", "TEXT")

                // Replace MySQL DATETIME with PostgreSQL TIMESTAMP
                .replaceAll("(?i)\\bDATETIME\\b", "TIMESTAMP")

                // Remove MySQL engine specifications
                .replaceAll("(?i)\\s+ENGINE\\s*=\\s*\\w+", "")
                .replaceAll("(?i)\\s+DEFAULT\\s+CHARSET\\s*=\\s*\\w+", "")
                .replaceAll("(?i)\\s+COLLATE\\s*=\\s*\\w+", "")

                // Replace MySQL BOOLEAN with PostgreSQL BOOLEAN
                .replaceAll("(?i)\\bTINYINT\\s*\\(\\s*1\\s*\\)", "BOOLEAN");
    }


    /**
     * Adapts SQL for MySQL
     */
    private String adaptForMySQL(String sql) {
        // MySQL is often the base format, but we might need some adjustments
        return sql
                // Ensure proper MySQL syntax
                .replaceAll("(?i)\\bSERIAL\\b", "INT AUTO_INCREMENT")
                .replaceAll("(?i)\\bBIGSERIAL\\b", "BIGINT AUTO_INCREMENT")
                .replaceAll("(?i)\\bTIMESTAMP\\b", "DATETIME");
    }


    /**
     * Adapts SQL for SQLite
     */
    private String adaptForSQLite(String sql) {
        return sql
                // SQLite uses INTEGER PRIMARY KEY for auto-increment
                .replaceAll("(?i)\\bINT\\s*\\(\\d+\\)\\s+AUTO_INCREMENT\\b", "INTEGER")
                .replaceAll("(?i)\\bINT\\s+AUTO_INCREMENT\\b", "INTEGER")
                .replaceAll("(?i)\\bBIGINT\\s*\\(\\d+\\)\\s+AUTO_INCREMENT\\b", "INTEGER")
                .replaceAll("(?i)\\bBIGINT\\s+AUTO_INCREMENT\\b", "INTEGER")
                .replaceAll("(?i)\\bSERIAL\\b", "INTEGER")
                .replaceAll("(?i)\\bBIGSERIAL\\b", "INTEGER")

                // Remove length specifiers (SQLite ignores them anyway)
                .replaceAll("(?i)\\bINT\\s*\\(\\d+\\)", "INTEGER")
                .replaceAll("(?i)\\bBIGINT\\s*\\(\\d+\\)", "INTEGER")
                .replaceAll("(?i)\\bTINYINT\\s*\\(\\d+\\)", "INTEGER")
                .replaceAll("(?i)\\bSMALLINT\\s*\\(\\d+\\)", "INTEGER")

                // SQLite text types
                .replaceAll("(?i)\\bLONGTEXT\\b", "TEXT")
                .replaceAll("(?i)\\bMEDIUMTEXT\\b", "TEXT")
                .replaceAll("(?i)\\bTINYTEXT\\b", "TEXT")

                // SQLite doesn't support DATETIME, use TEXT or REAL
                .replaceAll("(?i)\\bDATETIME\\b", "TEXT")
                .replaceAll("(?i)\\bTIMESTAMP\\b", "TEXT")

                // Remove MySQL/PostgreSQL specific keywords
                .replaceAll("(?i)\\s+ENGINE\\s*=\\s*\\w+", "")
                .replaceAll("(?i)\\s+DEFAULT\\s+CHARSET\\s*=\\s*\\w+", "")
                .replaceAll("(?i)\\s+COLLATE\\s*=\\s*\\w+", "")

                // SQLite boolean is INTEGER
                .replaceAll("(?i)\\bBOOLEAN\\b", "INTEGER")
                .replaceAll("(?i)\\bTINYINT\\s*\\(\\s*1\\s*\\)", "INTEGER");
    }


    /**
     * Gets table creation SQL statements for all required tables
     */
    public Map<String, String> getAllTableCreationSQL() {
        Map<String, String> tables = new HashMap<>();

        // Users table
        tables.put("users", """
                    CREATE TABLE users (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        nom VARCHAR(50) NOT NULL,
                        prenom VARCHAR(50) NOT NULL,
                        num_telephone INT,
                        password VARCHAR(180) NOT NULL,
                        role VARCHAR(50) NOT NULL,
                        adresse VARCHAR(50),
                        date_de_naissance DATE,
                        email VARCHAR(180) NOT NULL UNIQUE,
                        photo_de_profil VARCHAR(255),
                        is_verified BOOLEAN NOT NULL DEFAULT TRUE,
                        roles TEXT NOT NULL,
                        totp_secret VARCHAR(255)
                    )
                """);

        // Films table
        tables.put("films", """
                    CREATE TABLE films (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        name VARCHAR(255) NOT NULL,
                        image TEXT,
                        duration TIME NOT NULL,
                        description TEXT NOT NULL,
                        release_year INT NOT NULL,
                        isBookmarked BOOLEAN NOT NULL DEFAULT FALSE
                    )
                """);

        // Products table
        tables.put("products", """
                    CREATE TABLE products (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        name VARCHAR(50) NOT NULL,
                        price INT NOT NULL,
                        image VARCHAR(255) NOT NULL,
                        description VARCHAR(100) NOT NULL,
                        quantity INT NOT NULL
                    )
                """);

        // Product categories table
        tables.put("product_categories", """
                    CREATE TABLE product_categories (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        category_name VARCHAR(50) NOT NULL,
                        description VARCHAR(255) NOT NULL
                    )
                """);

        // Product-Category junction table
        tables.put("product_category", """
                    CREATE TABLE product_category (
                        product_id BIGINT NOT NULL,
                        category_id BIGINT NOT NULL,
                        PRIMARY KEY (product_id, category_id)
                    )
                """);

        // Orders table
        tables.put("orders", """
                    CREATE TABLE orders (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        order_date DATE NOT NULL,
                        status VARCHAR(50) NOT NULL DEFAULT 'pending',
                        phone_number INT NOT NULL,
                        address VARCHAR(50) NOT NULL,
                        client_id BIGINT
                    )
                """);

        // Order items table
        tables.put("order_items", """
                    CREATE TABLE order_items (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        product_id BIGINT,
                        quantity INT NOT NULL,
                        order_id BIGINT
                    )
                """);

        // Shopping cart table
        tables.put("shopping_cart", """
                    CREATE TABLE shopping_cart (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        product_id BIGINT,
                        quantity INT,
                        client_id BIGINT
                    )
                """);

        // Product comments table
        tables.put("product_comments", """
                    CREATE TABLE product_comments (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        client_id BIGINT,
                        product_id BIGINT,
                        comment VARCHAR(255) NOT NULL
                    )
                """);

        // Product reviews table
        tables.put("product_reviews", """
                    CREATE TABLE product_reviews (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        user_id BIGINT,
                        product_id BIGINT,
                        rating INT NOT NULL,
                        review VARCHAR(255)
                    )
                """);

        // Series table
        tables.put("series", """
                    CREATE TABLE series (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        name VARCHAR(30) NOT NULL,
                        summary VARCHAR(50) NOT NULL,
                        director VARCHAR(50) NOT NULL,
                        country VARCHAR(50) NOT NULL,
                        image VARCHAR(255) NOT NULL,
                        liked INT DEFAULT NULL,
                        nb_likes INT DEFAULT NULL,
                        disliked INT DEFAULT NULL,
                        nb_dislikes INT DEFAULT NULL,
                        category_id BIGINT
                    )
                """);

        // Series categories table
        tables.put("series_categories", """
                    CREATE TABLE series_categories (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        name VARCHAR(50) NOT NULL,
                        description VARCHAR(50) NOT NULL
                    )
                """);

        // Episodes table
        tables.put("episodes", """
                    CREATE TABLE episodes (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        series_id BIGINT,
                        title VARCHAR(30) NOT NULL,
                        episode_number INT NOT NULL,
                        season INT NOT NULL,
                        image VARCHAR(255) NOT NULL,
                        video VARCHAR(255) NOT NULL
                    )
                """);

        // Favorites table
        tables.put("favorites", """
                    CREATE TABLE favorites (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        user_id BIGINT NOT NULL,
                        series_id BIGINT NOT NULL
                    )
                """);

        // Feedback table
        tables.put("feedback", """
                    CREATE TABLE feedback (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        user_id BIGINT NOT NULL,
                        description VARCHAR(255) NOT NULL,
                        date DATE NOT NULL,
                        episode_id BIGINT NOT NULL
                    )
                """);

        // Actors table
        tables.put("actors", """
                    CREATE TABLE actors (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        name VARCHAR(255) NOT NULL,
                        image TEXT NOT NULL,
                        biography TEXT NOT NULL
                    )
                """);

        // Film categories table
        tables.put("film_categories", """
                    CREATE TABLE film_categories (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        name VARCHAR(255) NOT NULL UNIQUE,
                        description TEXT NOT NULL
                    )
                """);

        // Film-Category junction table
        tables.put("film_category", """
                    CREATE TABLE film_category (
                        film_id BIGINT NOT NULL,
                        category_id BIGINT NOT NULL,
                        PRIMARY KEY (film_id, category_id)
                    )
                """);

        // Film-Actor junction table
        tables.put("film_actor", """
                    CREATE TABLE film_actor (
                        film_id BIGINT NOT NULL,
                        actor_id BIGINT NOT NULL,
                        PRIMARY KEY (film_id, actor_id)
                    )
                """);

        // Film comments table
        tables.put("film_comments", """
                    CREATE TABLE film_comments (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        comment VARCHAR(255) NOT NULL,
                        user_id BIGINT NOT NULL,
                        film_id BIGINT NOT NULL
                    )
                """);

        // Film ratings table
        tables.put("film_ratings", """
                    CREATE TABLE film_ratings (
                        film_id BIGINT NOT NULL,
                        user_id BIGINT NOT NULL,
                        rating INT,
                        PRIMARY KEY (film_id, user_id)
                    )
                """);

        // Cinemas table
        tables.put("cinemas", """
                    CREATE TABLE cinemas (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        name VARCHAR(50) NOT NULL,
                        address VARCHAR(100) NOT NULL,
                        manager_id BIGINT NOT NULL,
                        logo VARCHAR(1000) NOT NULL,
                        status VARCHAR(50) NOT NULL
                    )
                """);

        // Film-Cinema junction table
        tables.put("film_cinema", """
                    CREATE TABLE film_cinema (
                        film_id BIGINT NOT NULL,
                        cinema_id BIGINT NOT NULL,
                        PRIMARY KEY (film_id, cinema_id)
                    )
                """);

        return tables;
    }


    /**
     * Creates all tables required by the application
     */
    public void createAllTablesIfNotExists() {
        Map<String, String> tables = getAllTableCreationSQL();

        for (Map.Entry<String, String> entry : tables.entrySet()) {
            try {
                createTableIfNotExists(entry.getKey(), entry.getValue());
            }
 catch (Exception e) {
                log.error("Failed to create table: " + entry.getKey(), e);
                // Continue with other tables even if one fails
            }

        }

    }


    /**
     * Lists all existing tables in the database
     */
    public List<String> listExistingTables() {
        List<String> tables = new ArrayList<>();
        try {
            DatabaseMetaData metaData = connection.getMetaData();
            try (ResultSet rs = metaData.getTables(null, null, "%", new String[] { "TABLE" }
)) {
                while (rs.next()) {
                    tables.add(rs.getString("TABLE_NAME"));
                }

            }

        }
 catch (SQLException e) {
            log.error("Error listing existing tables", e);
        }

        return tables;
    }


    /**
     * Checks which required tables are missing from the database
     */
    public List<String> getMissingTables() {
        List<String> missingTables = new ArrayList<>();
        Map<String, String> requiredTables = getAllTableCreationSQL();

        for (String tableName : requiredTables.keySet()) {
            if (!tableExists(tableName)) {
                missingTables.add(tableName);
            }

        }

        return missingTables;
    }

}

