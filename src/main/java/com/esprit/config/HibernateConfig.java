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

        // Determine which config file to use based on environment
        String configFile = isProduction() ? HIBERNATE_PRODUCTION_CONFIG_FILE : HIBERNATE_CONFIG_FILE;

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

        return properties;
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
}
