package com.esprit.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hibernate Configuration Manager Manages database connection and session
 * factory configuration
 */
public class HibernateConfig {

    private static final Logger log = LoggerFactory.getLogger(HibernateConfig.class);
    private static SessionFactory sessionFactory;
    private static final String HIBERNATE_CONFIG_FILE = "hibernate.properties";
    private static final String HIBERNATE_PRODUCTION_CONFIG_FILE = "hibernate-production.properties";
    private static final String HIBERNATE_SQLITE_CONFIG_FILE = "hibernate-sqlite.properties";
    private static final String HIBERNATE_MYSQL_CONFIG_FILE = "hibernate-mysql.properties";

    public enum DatabaseType {
        H2("h2", HIBERNATE_CONFIG_FILE),
        POSTGRESQL("postgresql", HIBERNATE_PRODUCTION_CONFIG_FILE),
        SQLITE("sqlite", HIBERNATE_SQLITE_CONFIG_FILE),
        MYSQL("mysql", HIBERNATE_MYSQL_CONFIG_FILE);

        private final String name;
        private final String configFile;

        DatabaseType(String name, String configFile) {
            this.name = name;
            this.configFile = configFile;
        }

        public String getName() {
            return name;
        }

        public String getConfigFile() {
            return configFile;
        }

        public static DatabaseType fromString(String dbType) {
            if (dbType == null)
                return H2; // Default

            for (DatabaseType type : values()) {
                if (type.name.equalsIgnoreCase(dbType)) {
                    return type;
                }
            }
            return H2; // Default fallback
        }
    }

    static {
        try {
            buildSessionFactory();
        } catch (Exception e) {
            log.error("Failed to create SessionFactory", e);
            throw new ExceptionInInitializerError("Failed to create SessionFactory: " + e.getMessage());
        }
    }

    /**
     * Build SessionFactory from configuration
     */
    private static void buildSessionFactory() {
        try {
            Configuration configuration = new Configuration();

            // Load properties based on environment
            Properties hibernateProperties = loadHibernateProperties();
            configuration.setProperties(hibernateProperties);

            // Add annotated classes
            addAnnotatedClasses(configuration);

            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties()).build();

            sessionFactory = configuration.buildSessionFactory(serviceRegistry);

            log.info("Hibernate SessionFactory created successfully");

        } catch (Exception e) {
            log.error("Failed to build SessionFactory", e);
            throw new RuntimeException("Failed to build SessionFactory", e);
        }
    }

    /**
     * Load Hibernate properties from configuration file
     */
    private static Properties loadHibernateProperties() {
        Properties properties = new Properties();

        // Determine database type from environment variable or system property
        String dbType = System.getProperty("db.type", System.getenv("DB_TYPE"));
        DatabaseType databaseType = DatabaseType.fromString(dbType);

        // Override for production environment
        if (isProduction() && databaseType == DatabaseType.H2) {
            databaseType = DatabaseType.POSTGRESQL;
        }

        String configFile = databaseType.getConfigFile();
        log.info("Using database type: {} with config file: {}", databaseType.getName(), configFile);

        try (InputStream inputStream = HibernateConfig.class.getClassLoader().getResourceAsStream(configFile)) {
            if (inputStream == null) {
                throw new RuntimeException("Unable to find " + configFile);
            }
            properties.load(inputStream);
            log.info("Loaded Hibernate configuration from: {}", configFile);
        } catch (IOException e) {
            log.error("Failed to load Hibernate properties from: {}", configFile, e);
            throw new RuntimeException("Failed to load Hibernate properties", e);
        }

        // Override with environment variables if available
        overrideWithEnvironmentVariables(properties, databaseType);

        return properties;
    }

    /**
     * Override properties with environment variables for flexible configuration
     */
    private static void overrideWithEnvironmentVariables(Properties properties, DatabaseType databaseType) {
        String dbUrl = System.getenv("DB_URL");
        String dbUser = System.getenv("DB_USER");
        String dbPassword = System.getenv("DB_PASSWORD");

        if (dbUrl != null && !dbUrl.trim().isEmpty()) {
            properties.setProperty("hibernate.connection.url", dbUrl);
            log.info("Overriding database URL from environment variable");
        }

        if (dbUser != null && !dbUser.trim().isEmpty()) {
            properties.setProperty("hibernate.connection.username", dbUser);
            log.info("Overriding database username from environment variable");
        }

        if (dbPassword != null) {
            properties.setProperty("hibernate.connection.password", dbPassword);
            log.info("Overriding database password from environment variable");
        }

        // Set default SQLite path if using SQLite and no custom URL provided
        if (databaseType == DatabaseType.SQLITE && dbUrl == null) {
            String sqliteDbPath = System.getProperty("user.dir") + "/data/rakcha_db.sqlite";
            properties.setProperty("hibernate.connection.url", "jdbc:sqlite:" + sqliteDbPath);
            log.info("Using default SQLite database path: {}", sqliteDbPath);
        }
    }

    /**
     * Add all entity classes to the configuration
     */
    private static void addAnnotatedClasses(Configuration configuration) {
        // User entities
        configuration.addAnnotatedClass(com.esprit.models.users.User.class);
        configuration.addAnnotatedClass(com.esprit.models.users.Client.class);
        configuration.addAnnotatedClass(com.esprit.models.users.Admin.class);
        configuration.addAnnotatedClass(com.esprit.models.users.CinemaManager.class);

        // Product entities
        configuration.addAnnotatedClass(com.esprit.models.products.Product.class);
        configuration.addAnnotatedClass(com.esprit.models.products.ProductCategory.class);
        configuration.addAnnotatedClass(com.esprit.models.products.Order.class);
        configuration.addAnnotatedClass(com.esprit.models.products.OrderItem.class);
        configuration.addAnnotatedClass(com.esprit.models.products.ShoppingCart.class);
        configuration.addAnnotatedClass(com.esprit.models.products.Comment.class);
        configuration.addAnnotatedClass(com.esprit.models.products.Review.class);

        // Cinema entities
        configuration.addAnnotatedClass(com.esprit.models.cinemas.Cinema.class);
        configuration.addAnnotatedClass(com.esprit.models.cinemas.CinemaHall.class);
        configuration.addAnnotatedClass(com.esprit.models.cinemas.Seat.class);
        configuration.addAnnotatedClass(com.esprit.models.cinemas.MovieSession.class);
        configuration.addAnnotatedClass(com.esprit.models.cinemas.CinemaComment.class);
        configuration.addAnnotatedClass(com.esprit.models.cinemas.CinemaRating.class);

        // Film entities
        configuration.addAnnotatedClass(com.esprit.models.films.Film.class);
        configuration.addAnnotatedClass(com.esprit.models.films.Category.class);
        configuration.addAnnotatedClass(com.esprit.models.films.Actor.class);
        configuration.addAnnotatedClass(com.esprit.models.films.FilmComment.class);
        configuration.addAnnotatedClass(com.esprit.models.films.FilmRating.class);
        configuration.addAnnotatedClass(com.esprit.models.films.Ticket.class);

        // Series entities
        configuration.addAnnotatedClass(com.esprit.models.series.Series.class);
        configuration.addAnnotatedClass(com.esprit.models.series.Episode.class);
        configuration.addAnnotatedClass(com.esprit.models.series.Category.class);
        configuration.addAnnotatedClass(com.esprit.models.series.Feedback.class);
        configuration.addAnnotatedClass(com.esprit.models.series.Favorite.class);
    }

    /**
     * Check if running in production environment
     */
    private static boolean isProduction() {
        String env = System.getProperty("app.environment", "development");
        return "production".equalsIgnoreCase(env);
    }

    /**
     * Get the SessionFactory instance
     */
    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            buildSessionFactory();
        }
        return sessionFactory;
    }

    /**
     * Close the SessionFactory
     */
    public static void shutdown() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
            log.info("Hibernate SessionFactory closed");
        }
    }

    /**
     * Get current database type being used
     */
    public static DatabaseType getCurrentDatabaseType() {
        String dbType = System.getProperty("db.type", System.getenv("DB_TYPE"));
        DatabaseType databaseType = DatabaseType.fromString(dbType);

        if (isProduction() && databaseType == DatabaseType.H2) {
            return DatabaseType.POSTGRESQL;
        }

        return databaseType;
    }

    /**
     * Switch database type at runtime (requires application restart)
     */
    public static void setDatabaseType(DatabaseType databaseType) {
        System.setProperty("db.type", databaseType.getName());
        log.info("Database type set to: {}. Application restart required.", databaseType.getName());
    }

    /**
     * Create data directory for SQLite if it doesn't exist
     */
    private static void ensureDataDirectoryExists() {
        try {
            java.nio.file.Path dataDir = java.nio.file.Paths.get(System.getProperty("user.dir"), "data");
            if (!java.nio.file.Files.exists(dataDir)) {
                java.nio.file.Files.createDirectories(dataDir);
                log.info("Created data directory: {}", dataDir.toAbsolutePath());
            }
        } catch (Exception e) {
            log.warn("Failed to create data directory", e);
        }
    }

    /**
     * Initialize database - create tables and initial data if needed
     */
    public static void initializeDatabase() {
        DatabaseType currentType = getCurrentDatabaseType();

        if (currentType == DatabaseType.SQLITE) {
            ensureDataDirectoryExists();
        }

        log.info("Database initialization completed for: {}", currentType.getName());
    }
}
