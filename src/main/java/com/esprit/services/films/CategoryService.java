package com.esprit.services.films;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.esprit.models.films.Category;
import com.esprit.services.IService;
import com.esprit.utils.DataSource;

import lombok.extern.slf4j.Slf4j;

/**
 * Service class for managing film categories in the database.
 * 
 * <p>
 * This service provides CRUD operations for film categories, including
 * creating,
 * reading, updating, and deleting categories. It implements the IService
 * interface
 * for consistent service behavior across the application.
 * </p>
 * 
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
public class CategoryService implements IService<Category> {
    Connection connection;

    /**
     * Constructs a new CategoryService and initializes the database connection.
     */
    public CategoryService() {
        this.connection = DataSource.getInstance().getConnection();
    }

    /**
     * Creates a new category in the database.
     *
     * @param category The category object to be created
     */
    @Override
    public void create(final Category category) {
        final String req = "insert into categories (name,description) values (?,?) ";
        try {
            final PreparedStatement statement = this.connection.prepareStatement(req);
            statement.setString(1, category.getName());
            statement.setString(2, category.getDescription());
            statement.executeUpdate();
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieves all categories from the database.
     *
     * @return A list of all Category objects in the database
     */
    @Override
    public List<Category> read() {
        final List<Category> categoryArrayList = new ArrayList<>();
        final String req = "SELECT * FROM categories";
        try (final PreparedStatement statement = this.connection.prepareStatement(req)) {
            final ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                categoryArrayList.add(Category.builder().id(rs.getLong("id")).name(rs.getString("name"))
                        .description(rs.getString("description")).build());
            }
        } catch (final SQLException e) {
            log.error("Error reading categories", e);
            throw new RuntimeException(e);
        }
        return categoryArrayList;
    }

    /**
     * Updates an existing category in the database.
     *
     * @param category The category object to update
     */
    @Override
    public void update(final Category category) {
        final String req = "UPDATE categories set name=?,description=? where id=?";
        try {
            final PreparedStatement statement = this.connection.prepareStatement(req);
            statement.setString(1, category.getName());
            statement.setString(2, category.getDescription());
            statement.setLong(3, category.getId());
            statement.executeUpdate();
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Deletes a category from the database.
     *
     * @param category The category object to delete
     */
    @Override
    public void delete(final Category category) {
        final String req = "DELETE FROM categories where id = ?";
        try {
            final PreparedStatement statement = this.connection.prepareStatement(req);
            statement.setLong(1, category.getId());
            statement.executeUpdate();
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieves a category by its ID.
     *
     * @param id The unique identifier of the category to retrieve
     * @return The Category object if found, null otherwise
     */
    public Category getCategory(final Long id) {
        Category category = null;
        final String req = "SELECT * FROM categories where id = ?";
        try (final PreparedStatement statement = this.connection.prepareStatement(req)) {
            statement.setLong(1, id);
            final ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                category = Category.builder().id(rs.getLong("id")).name(rs.getString("name"))
                        .description(rs.getString("description")).build();
            }
        } catch (final SQLException e) {
            log.error("Error getting category by id: {}", id, e);
        }
        return category;
    }

    /**
     * Retrieves a category by its name.
     *
     * @param nom The name of the category to retrieve
     * @return The Category object if found, null otherwise
     */
    public Category getCategoryByNom(final String nom) {
        Category category = null;
        final String req = "SELECT * FROM categories where name LIKE ?";
        try (final PreparedStatement statement = this.connection.prepareStatement(req)) {
            statement.setString(1, nom);
            final ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                category = Category.builder().id(rs.getLong("id")).name(rs.getString("name"))
                        .description(rs.getString("description")).build();
            }
        } catch (final SQLException e) {
            log.error("Error getting category by name: {}", nom, e);
        }
        return category;
    }
}
