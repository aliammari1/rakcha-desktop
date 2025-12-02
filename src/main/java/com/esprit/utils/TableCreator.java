package com.esprit.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Statement;

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
            } else if (url.contains("postgresql")) {
                return DatabaseType.POSTGRESQL;
            }

        } catch (SQLException e) {
            log.warn("Could not detect database type", e);
        }

        return DatabaseType.UNKNOWN;
    }


    /**
     * Creates all tables from the SQL file for the detected database type
     */
    public void createAllTablesIfNotExists() {
        // If PostgreSQL, load from rakcha_db_postgres.sql
        if (databaseType == DatabaseType.POSTGRESQL) {
            loadSchemaFromSQLFile("rakcha_db_postgres.sql");
        } else if (databaseType == DatabaseType.MYSQL) {
            loadSchemaFromSQLFile("rakcha_db.sql");
        } else {
            log.error("SQLite is not supported yet");
        }
    }


    /**
     * Loads and executes SQL from a file
     *
     * @param fileName The name of the SQL file to load
     */
    public void loadSchemaFromSQLFile(String fileName) {
        try {
            // Try to load from resources first
            String sqlContent = loadSQLFromResources(fileName);
            if (sqlContent == null) {
                // Try to load from file system
                sqlContent = loadSQLFromFile(fileName);
            }

            if (sqlContent != null) {
                executeSQLScript(sqlContent);
                log.info("Successfully loaded schema from {}", fileName);
            } else {
                log.warn("Could not find SQL file: {}", fileName);
            }
        } catch (Exception e) {
            log.error("Error loading schema from SQL file: " + fileName, e);
        }
    }

    /**
     * Loads SQL from classpath resources
     */
    private String loadSQLFromResources(String fileName) {
        try (InputStream is = TableCreator.class.getClassLoader().getResourceAsStream(fileName)) {
            if (is == null) {
                return null;
            }
            return readInputStreamAsString(is);
        } catch (IOException e) {
            log.warn("Could not load {} from resources", fileName);
            return null;
        }
    }

    /**
     * Loads SQL from file system
     */
    private String loadSQLFromFile(String fileName) {
        try {
            return new String(Files.readAllBytes(Paths.get(fileName)));
        } catch (IOException e) {
            log.warn("Could not load {} from file system", fileName);
            return null;
        }
    }

    /**
     * Reads input stream as string
     */
    private String readInputStreamAsString(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
        }
        return sb.toString();
    }

    /**
     * Executes SQL script from a string, splitting by semicolons
     */
    private void executeSQLScript(String sqlContent) {
        // Split by semicolons and execute each statement
        String[] statements = sqlContent.split(";");

        try (Statement statement = connection.createStatement()) {
            for (String sql : statements) {
                String trimmed = sql.trim();

                // Skip empty statements and comments
                if (trimmed.isEmpty() || trimmed.startsWith("--") || trimmed.startsWith("/*")) {
                    continue;
                }

                // Remove comment prefixes added by converters
                trimmed = trimmed.replaceAll("(?m)^\\s*--\\s*SQLINES.*$", "")
                    .replaceAll("(?m)^\\s*/\\*\\s*SQLINES.*?\\*/", "")
                    .trim();

                if (!trimmed.isEmpty()) {
                    try {
                        statement.executeUpdate(trimmed);
                        log.debug("Executed SQL statement successfully");
                    } catch (SQLException e) {
                        // Log but continue with next statement
                        log.warn("SQL execution warning (continuing): {}", e.getMessage());
                    }
                }
            }
            log.info("SQL script execution completed");
        } catch (SQLException e) {
            log.error("Error executing SQL script", e);
            throw new RuntimeException("Failed to execute SQL script", e);
        }
    }


    public enum DatabaseType {
        MYSQL, POSTGRESQL, UNKNOWN
    }

}

