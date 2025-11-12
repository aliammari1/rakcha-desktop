package com.esprit.services.series;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.esprit.models.series.Category;
import com.esprit.services.IService;
import com.esprit.utils.DataSource;
import com.esprit.utils.Page;
import com.esprit.utils.PageRequest;
import com.esprit.utils.PaginationQueryBuilder;
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
public class IServiceCategorieImpl implements IService<Category> {
    private static final Logger LOGGER = Logger.getLogger(IServiceCategorieImpl.class.getName());
    private final Connection connection;

    // Allowed columns for sorting to prevent SQL injection
    private static final String[] ALLOWED_SORT_COLUMNS = { "id", "name", "description" }
;

    /**
     * Constructs a new IServiceCategorieImpl instance.
     * Initializes database connection and creates tables if they don't exist.
     */
    public IServiceCategorieImpl() {
        this.connection = DataSource.getInstance().getConnection();

        // Create tables if they don't exist
        try {
            TableCreator tableCreator = new TableCreator(this.connection);

            // Create category table for series
            String createCategoryTable = """
                    CREATE TABLE category (
                        id BIGINT PRIMARY KEY AUTO_INCREMENT,
                        name VARCHAR(255) NOT NULL UNIQUE,
                        description TEXT
                    )
                    """;
            tableCreator.createTableIfNotExists("category", createCategoryTable);

        }
 catch (Exception e) {
            log.error("Error creating tables for IServiceCategorieImpl", e);
        }

    }


    @Override
    /**
     * Creates a new entity in the database.
     *
     * @param entity
     *               the entity to create
     */
    public void create(final Category category) {
        final String req = "INSERT INTO category (name, description) VALUES (?, ?)";
        try (final PreparedStatement st = this.connection.prepareStatement(req)) {
            st.setString(1, category.getName());
            st.setString(2, category.getDescription());
            st.executeUpdate();
            LOGGER.info("Category added successfully");
        }
 catch (final SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating category: " + e.getMessage(), e);
            throw new RuntimeException("Failed to create category", e);
        }

    }


    @Override
    /**
     * Updates an existing entity in the database.
     *
     * @param entity
     *               the entity to update
     */
    public void update(final Category category) {
        final String req = "UPDATE category SET name = ?, description = ? WHERE id = ?";
        try (final PreparedStatement os = this.connection.prepareStatement(req)) {
            os.setString(1, category.getName());
            os.setString(2, category.getDescription());
            os.setLong(3, category.getId());
            os.executeUpdate();
            LOGGER.info("Category updated successfully");
        }
 catch (final SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating category: " + e.getMessage(), e);
            throw new RuntimeException("Failed to update category", e);
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
        final String req = "DELETE FROM category WHERE id = ?";
        try (final PreparedStatement os = this.connection.prepareStatement(req)) {
            os.setLong(1, category.getId());
            os.executeUpdate();
            LOGGER.info("Category deleted successfully");
        }
 catch (final SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting category: " + e.getMessage(), e);
            throw new RuntimeException("Failed to delete category", e);
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
        final String baseQuery = "SELECT * FROM category";

        // Validate sort column to prevent SQL injection
        if (pageRequest.hasSorting() &&
                !PaginationQueryBuilder.isValidSortColumn(pageRequest.getSortBy(), ALLOWED_SORT_COLUMNS)) {
            LOGGER.warning("Invalid sort column: " + pageRequest.getSortBy() + ". Using default sorting.");
            pageRequest = PageRequest.of(pageRequest.getPage(), pageRequest.getSize());
        }


        try {
            // Get total count
            final String countQuery = PaginationQueryBuilder.buildCountQuery(baseQuery);
            final long totalElements = PaginationQueryBuilder.executeCountQuery(connection, countQuery);

            // Get paginated results
            final String paginatedQuery = PaginationQueryBuilder.buildPaginatedQuery(baseQuery, pageRequest);

            try (final Statement st = this.connection.createStatement();
                    final ResultSet rs = st.executeQuery(paginatedQuery)) {
                while (rs.next()) {
                    final Category category = buildCategoryFromResultSet(rs);
                    content.add(category);
                }

            }


            return new Page<>(content, pageRequest.getPage(), pageRequest.getSize(), totalElements);

        }
 catch (final SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving paginated categories: " + e.getMessage(), e);
            return new Page<>(content, pageRequest.getPage(), pageRequest.getSize(), 0);
        }

    }


    /**
     * Helper method to build Category object from ResultSet.
     *
     * @param rs the ResultSet containing category data
     * @return the Category object
     * @throws SQLException if there's an error reading from ResultSet
     */
    private Category buildCategoryFromResultSet(ResultSet rs) throws SQLException {
        return Category.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .build();
    }


    /**
     * Retrieves the CategoriesStatistics value.
     *
     * @return the CategoriesStatistics value
     */
    public Map<Category, Long> getCategoriesStatistics() {
        final Map<Category, Long> statistics = new HashMap<>();
        final String query = """
                SELECT c.*, COUNT(sc.series_id) as series_count
                FROM category c
                LEFT JOIN series_categories sc ON c.id = sc.category_id
                GROUP BY c.id
                """;
        try (final PreparedStatement statement = this.connection.prepareStatement(query);
                final ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                final Category category = Category.builder().id(resultSet.getLong("id"))
                        .name(resultSet.getString("name")).description(resultSet.getString("description")).build();
                final long seriesCount = resultSet.getLong("series_count");
                statistics.put(category, seriesCount);
            }

        }
 catch (final SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

        return statistics;
    }

}

