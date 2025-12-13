package com.esprit.services.films;

import com.esprit.enums.CategoryType;
import com.esprit.models.common.Category;
import com.esprit.models.films.Film;
import com.esprit.services.common.CategoryService;
import com.esprit.utils.DataSource;
import lombok.extern.log4j.Log4j2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Log4j2
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
    }

    /**
     * Creates associations between a film and multiple categories.
     *
     * @param film          the film to associate with categories
     * @param categoryNames the list of category names to associate with the film
     * @throws IllegalArgumentException if film ID is invalid or category doesn't
     *                                  exist
     */
    public void createFilmCategoryAssociation(Film film, List<String> categoryNames) {
        if (film.getId() == null || film.getId() <= 0) {
            throw new IllegalArgumentException("Film must have a valid ID before creating associations");
        }

        CategoryService categoryService = new CategoryService();
        final String req = "INSERT INTO movie_categories (movie_id, category_id) VALUES (?,?)";
        try (final PreparedStatement statement = this.connection.prepareStatement(req)) {
            for (final String categoryName : categoryNames) {
                Category category = categoryService.getCategoryByNameAndType(categoryName, CategoryType.MOVIE);
                if (category == null) {
                    throw new IllegalArgumentException("Category not found: " + categoryName);
                }
                statement.setLong(1, film.getId());
                statement.setLong(2, category.getId());
                statement.executeUpdate();
            }
        } catch (final SQLException e) {
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
        // Table is film_categories, not categories
        final String req = "SELECT c.* FROM categories c JOIN movie_categories fc ON c.id = fc.category_id WHERE fc.movie_id = ?";
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
     * Retrieves the FilmsForCategory value.
     *
     * @return the FilmsForCategory value
     */
    public List<Film> getFilmsForCategory(int categoryId) {
        final List<Film> films = new ArrayList<>();
        final String req = "SELECT f.* FROM movies f JOIN movie_categories fc ON f.id = fc.movie_id WHERE fc.category_id = ?";
        try (final PreparedStatement statement = this.connection.prepareStatement(req)) {
            statement.setInt(1, categoryId);
            final ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                films.add(Film.builder().id(rs.getLong("id")).title(rs.getString("title"))
                    .imageUrl(rs.getString("image_url"))
                    .durationMin(rs.getInt("duration_min")).description(rs.getString("description"))
                    .releaseYear(rs.getInt("release_year")).build());
            }
        } catch (final SQLException e) {
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
        final String reqDelete = "DELETE FROM movie_categories WHERE movie_id = ?";
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
     * Retrieves the CategoryNames value.
     *
     * @return the CategoryNames value
     */
    public String getCategoryNames(final Long filmId) {
        final String req = "SELECT c.name FROM categories c JOIN movie_categories mc ON c.id = mc.category_id WHERE mc.movie_id = ?";
        try (final PreparedStatement statement = this.connection.prepareStatement(req)) {
            statement.setLong(1, filmId);
            final ResultSet rs = statement.executeQuery();
            if (!rs.next())
                return "";
            final StringBuilder categoryNames = new StringBuilder(rs.getString("name"));
            while (rs.next()) {
                categoryNames.append(", ").append(rs.getString("name"));
            }
            return categoryNames.toString();
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
        final String req = "DELETE FROM movie_categories WHERE movie_id = ? AND category_id = ?";
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
