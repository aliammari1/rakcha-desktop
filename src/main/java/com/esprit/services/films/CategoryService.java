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

@Slf4j
/**
 * Service class providing business logic for the RAKCHA application. Implements
 * CRUD operations and business rules for data management.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class CategoryService implements IService<Category> {
    Connection connection;

    /**
     * Performs CategoryService operation.
     *
     * @return the result of the operation
     */
    public CategoryService() {
        this.connection = DataSource.getInstance().getConnection();
    }

    /**
     * @param category
     */
    @Override
    /**
     * Creates a new entity in the database.
     *
     * @param entity
     *               the entity to create
     */
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
     * @return List<Category>
     */
    @Override
    /**
     * Performs read operation.
     *
     * @return the result of the operation
     */
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

    @Override
    /**
     * Updates an existing entity in the database.
     *
     * @param entity
     *               the entity to update
     */
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

    @Override
    /**
     * Deletes an entity from the database.
     *
     * @param id
     *           the ID of the entity to delete
     */
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
     * Retrieves the Category value.
     *
     * @return the Category value
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
     * Retrieves the CategoryByNom value.
     *
     * @return the CategoryByNom value
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
