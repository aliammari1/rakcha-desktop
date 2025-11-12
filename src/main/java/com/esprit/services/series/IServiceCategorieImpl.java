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
     * Initialize the service by obtaining a database connection and ensuring the category table exists.
     *
     * The constructor acquires a JDBC connection from the application's DataSource and creates the
     * "category" table if it does not already exist. The table schema created contains:
     * - id: BIGINT primary key with auto-increment
     * - name: VARCHAR(255) not null unique
     * - description: TEXT
     *
     * Any exception raised while creating tables is caught and logged; it is not propagated.
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


    /**
     * Inserts a new category row into the database with the category's name and description.
     *
     * @param category the Category to persist; its name must be non-null and unique according to the table constraint
     * @throws RuntimeException if the database insert fails (for example, due to a SQL error or constraint violation)
     */
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


    /**
     * Update the stored category identified by its id with the category's name and description.
     *
     * @param category the Category whose id selects the row to update and whose name and description will be persisted
     * @throws RuntimeException if a database error prevents completing the update
     */
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


    /**
     * Deletes the given category from the database.
     *
     * @param category the Category whose record (by id) will be removed
     * @throws RuntimeException if a database error prevents deletion
     */
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


    /**
     * Retrieve a paginated list of categories.
     *
     * <p>Validates the requested sort column and resets to default sorting if the column is not allowed.
     * Builds the result page using the total element count and the requested page parameters.
     *
     * @param pageRequest pagination parameters (page index, page size, and optional sorting)
     * @return a Page containing the categories for the requested page and the total number of elements;
     *         if a database error occurs, returns a Page with the retrieved content and total elements set to 0
     */
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
     * Constructs a Category from the current row of the given ResultSet.
     *
     * @param rs the ResultSet positioned at a row containing `id`, `name`, and `description` columns
     * @return a Category built from the current ResultSet row
     * @throws SQLException if reading any of the required columns from the ResultSet fails
     */
    private Category buildCategoryFromResultSet(ResultSet rs) throws SQLException {
        return Category.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .build();
    }


    /**
     * Returns a mapping of each Category to the number of series associated with it.
     *
     * @return a map where keys are Category objects and values are the corresponding series counts (0 if none)
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
