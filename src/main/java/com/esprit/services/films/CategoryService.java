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
import com.esprit.utils.Page;
import com.esprit.utils.PageRequest;
import com.esprit.utils.PaginationQueryBuilder;
import com.esprit.utils.TableCreator;

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

    // Allowed columns for sorting to prevent SQL injection
    private static final String[] ALLOWED_SORT_COLUMNS = {
            "id", "name", "description"
    }
;

    /**
     * Constructs a new CategoryService and initializes the database connection.
     * Creates tables if they don't exist.
     */
    public CategoryService() {
        this.connection = DataSource.getInstance().getConnection();

        // Create tables if they don't exist
        try {
            TableCreator tableCreator = new TableCreator(this.connection);

            // Create categories table for films
            String createCategoriesTable = """
                    CREATE TABLE categories (
                        id BIGINT PRIMARY KEY AUTO_INCREMENT,
                        name VARCHAR(255) NOT NULL UNIQUE,
                        description TEXT
                    )
                    """;
            tableCreator.createTableIfNotExists("categories", createCategoriesTable);

        }
 catch (Exception e) {
            log.error("Error creating tables for CategoryService", e);
        }

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
        }
 catch (final SQLException e) {
            throw new RuntimeException(e);
        }

    }


    @Override
    /**
     * Retrieves categories with pagination support.
     *
     * @param pageRequest the pagination parameters
     * @return a page of categories
     */
    public Page<Category> read(PageRequest pageRequest) {
        final List<Category> content = new ArrayList<>();
        final String baseQuery = "SELECT * FROM categories";

        // Validate sort column to prevent SQL injection
        if (pageRequest.hasSorting() &&
                !PaginationQueryBuilder.isValidSortColumn(pageRequest.getSortBy(), ALLOWED_SORT_COLUMNS)) {
            log.warn("Invalid sort column: {}
. Using default sorting.", pageRequest.getSortBy());
            pageRequest = PageRequest.of(pageRequest.getPage(), pageRequest.getSize());
        }


        try {
            // Get total count
            final String countQuery = PaginationQueryBuilder.buildCountQuery(baseQuery);
            final long totalElements = PaginationQueryBuilder.executeCountQuery(connection, countQuery);

            // Get paginated results
            final String paginatedQuery = PaginationQueryBuilder.buildPaginatedQuery(baseQuery, pageRequest);

            try (PreparedStatement stmt = connection.prepareStatement(paginatedQuery);
                    ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    content.add(Category.builder().id(rs.getLong("id")).name(rs.getString("name"))
                            .description(rs.getString("description")).build());
                }

            }


            return new Page<>(content, pageRequest.getPage(), pageRequest.getSize(), totalElements);

        }
 catch (final SQLException e) {
            log.error("Error retrieving paginated categories: {}
", e.getMessage(), e);
            return new Page<>(content, pageRequest.getPage(), pageRequest.getSize(), 0);
        }

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
        }
 catch (final SQLException e) {
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
        }
 catch (final SQLException e) {
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

        }
 catch (final SQLException e) {
            log.error("Error getting category by id: {}
", id, e);
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

        }
 catch (final SQLException e) {
            log.error("Error getting category by name: {}
", nom, e);
        }

        return category;
    }

}

