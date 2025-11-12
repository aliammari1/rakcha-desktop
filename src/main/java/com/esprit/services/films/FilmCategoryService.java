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
     * Initializes the FilmCategoryService and ensures the film_category junction table exists.
     *
     * The constructor obtains a database connection from the application's DataSource and
     * creates the film_category table with a unique constraint on (film_id, category_id)
     * if it does not already exist.
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

        } catch (Exception e) {
            log.error("Error creating tables for FilmCategoryService", e);
        }

    }


    /**
     * Create associations between a film and multiple categories.
     *
     * Only categories that exist (matching names) are associated; names with no matching category are ignored.
     *
     * @param film          the film to associate with categories
     * @param categoryNames the list of category names to associate with the film
     * @throws RuntimeException if a database error prevents creating the associations
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

        } catch (final SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating film-category associations", e);
            throw new RuntimeException(e);
        }

    }


    /**
     * Fetches all categories associated with the specified film.
     *
     * @param filmId the ID of the film whose categories to retrieve
     * @return a list of Category objects associated with the film; an empty list if none are found or an error occurs
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

        } catch (final SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting categories for film: " + filmId, e);
        }

        return categories;
    }


    /**
     * Retrieve all films associated with the given category.
     *
     * @param categoryId the category database id to fetch films for
     * @return a list of Film objects linked to the specified category; an empty list if none are found or on error
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

        } catch (final SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting films for category: " + categoryId, e);
        }

        return films;
    }


    /**
     * Replace a film's category associations with the provided list of category names.
     *
     * Deletes all existing associations for the film and creates new associations for each name in {@code categoryNames}.
     *
     * @param film          the film whose category associations will be replaced
     * @param categoryNames the new category names to associate with the film
     * @throws RuntimeException if a database error occurs while removing or creating associations
     */
    public void updateCategories(final Film film, final List<String> categoryNames) {
        // Delete existing associations
        final String reqDelete = "DELETE FROM film_category WHERE film_id = ?";
        try (final PreparedStatement statement = this.connection.prepareStatement(reqDelete)) {
            statement.setLong(1, film.getId());
            statement.executeUpdate();
        } catch (final SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting existing category associations", e);
            throw new RuntimeException(e);
        }


        // Create new associations
        createFilmCategoryAssociation(film, categoryNames);
    }


    /**
     * Fetches comma-separated category names associated with a film.
     *
     * @param filmId the ID of the film to retrieve categories for
     * @return the category names joined by ", " for the given film, or an empty string if none are found or on error
     */
    public String getCategoryNames(final Long filmId) {
        final String req = "SELECT GROUP_CONCAT(c.name SEPARATOR ', ') AS categoryNames FROM categories c JOIN film_category fc ON c.id = fc.category_id WHERE fc.film_id = ?";
        try (final PreparedStatement statement = this.connection.prepareStatement(req)) {
            statement.setLong(1, filmId);
            final ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getString("categoryNames");
            }

        } catch (final SQLException e) {
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
        } catch (final SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting film-category association", e);
            throw new RuntimeException(e);
        }

    }

}
