package com.esprit.services.films;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.esprit.models.films.Category;
import com.esprit.models.films.Film;
import com.esprit.utils.DataSource;
import com.esprit.utils.TableCreator;

import lombok.extern.slf4j.Slf4j;

@Slf4j
/**
 * Service class providing business logic for the RAKCHA application. Implements
 * CRUD operations and business rules for data management.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class FilmCategoryService {
    private static final Logger LOGGER = Logger.getLogger(FilmCategoryService.class.getName());
    private final Connection connection;

    /**
     * Constructs a new FilmCategoryService instance.
     * Initializes database connection and creates tables if they don't exist.
     */
    public FilmCategoryService() {
        this.connection = DataSource.getInstance().getConnection();

        // Create tables if they don't exist
        try {
            TableCreator tableCreator = new TableCreator(this.connection);

            // Create film_category junction table
            String createFilmCategoryTable = """
                    CREATE TABLE film_category (
                        id BIGINT PRIMARY KEY AUTO_INCREMENT,
                        film_id BIGINT NOT NULL,
                        category_id BIGINT NOT NULL,
                        UNIQUE(film_id, category_id)
                    )
                    """;
            tableCreator.createTableIfNotExists("film_category", createFilmCategoryTable);

        }
 catch (Exception e) {
            log.error("Error creating tables for FilmCategoryService", e);
        }

    }


    /**
     * Creates associations between a film and multiple categories.
     *
     * @param film          the film to associate with categories
     * @param categoryNames the list of category names to associate with the film
     */
    public void createFilmCategoryAssociation(Film film, List<String> categoryNames) {
        final String req = "INSERT INTO film_category (film_id, category_id) VALUES (?,?)";
        try (final PreparedStatement statement = this.connection.prepareStatement(req)) {
            for (final String categoryName : categoryNames) {
                Category category = new CategoryService().getCategoryByNom(categoryName);
                if (category != null) {
                    statement.setLong(1, film.getId());
                    statement.setLong(2, category.getId());
                    statement.executeUpdate();
                }

            }

        }
 catch (final SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating film-category associations", e);
            throw new RuntimeException(e);
        }

    }


    /**
     * Retrieves the CategoriesForFilm value.
     *
     * @return the CategoriesForFilm value
     */
    public List<Category> getCategoriesForFilm(int filmId) {
        final List<Category> categories = new ArrayList<>();
        final String req = "SELECT c.* FROM categories c JOIN film_category fc ON c.id = fc.category_id WHERE fc.film_id = ?";
        try (final PreparedStatement statement = this.connection.prepareStatement(req)) {
            statement.setInt(1, filmId);
            final ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                categories.add(Category.builder().id(rs.getLong("id")).name(rs.getString("name"))
                        .description(rs.getString("description")).build());
            }

        }
 catch (final SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting categories for film: " + filmId, e);
        }

        return categories;
    }


    /**
     * Retrieves the FilmsForCategory value.
     *
     * @return the FilmsForCategory value
     */
    public List<Film> getFilmsForCategory(int categoryId) {
        final List<Film> films = new ArrayList<>();
        final String req = "SELECT f.* FROM films f JOIN film_category fc ON f.id = fc.film_id WHERE fc.category_id = ?";
        try (final PreparedStatement statement = this.connection.prepareStatement(req)) {
            statement.setInt(1, categoryId);
            final ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                films.add(Film.builder().id(rs.getLong("id")).name(rs.getString("name")).image(rs.getString("image"))
                        .duration(rs.getTime("duration")).description(rs.getString("description"))
                        .releaseYear(rs.getInt("release_year")).build());
            }

        }
 catch (final SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting films for category: " + categoryId, e);
        }

        return films;
    }


    /**
     * Updates the categories associated with a film.
     * Removes existing associations and creates new ones.
     *
     * @param film          the film whose categories to update
     * @param categoryNames the new list of category names to associate with the
     *                      film
     */
    public void updateCategories(final Film film, final List<String> categoryNames) {
        // Delete existing associations
        final String reqDelete = "DELETE FROM film_category WHERE film_id = ?";
        try (final PreparedStatement statement = this.connection.prepareStatement(reqDelete)) {
            statement.setLong(1, film.getId());
            statement.executeUpdate();
        }
 catch (final SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting existing category associations", e);
            throw new RuntimeException(e);
        }


        // Create new associations
        createFilmCategoryAssociation(film, categoryNames);
    }


    /**
     * Retrieves the CategoryNames value.
     *
     * @return the CategoryNames value
     */
    public String getCategoryNames(final Long filmId) {
        final String req = "SELECT GROUP_CONCAT(c.name SEPARATOR ', ') AS categoryNames FROM categories c JOIN film_category fc ON c.id = fc.category_id WHERE fc.film_id = ?";
        try (final PreparedStatement statement = this.connection.prepareStatement(req)) {
            statement.setLong(1, filmId);
            final ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getString("categoryNames");
            }

        }
 catch (final SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting category names for film: " + filmId, e);
        }

        return "";
    }


    /**
     * Deletes the association between a specific film and category.
     *
     * @param filmId     the ID of the film
     * @param categoryId the ID of the category
     */
    public void deleteFilmCategoryAssociation(int filmId, int categoryId) {
        final String req = "DELETE FROM film_category WHERE film_id = ? AND category_id = ?";
        try (final PreparedStatement statement = this.connection.prepareStatement(req)) {
            statement.setInt(1, filmId);
            statement.setInt(2, categoryId);
            statement.executeUpdate();
        }
 catch (final SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting film-category association", e);
            throw new RuntimeException(e);
        }

    }

}

