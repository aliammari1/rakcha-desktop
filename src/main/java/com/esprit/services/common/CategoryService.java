package com.esprit.services.common;

import com.esprit.enums.CategoryType;
import com.esprit.models.common.Category;
import com.esprit.services.IService;
import com.esprit.utils.DataSource;
import com.esprit.utils.Page;
import com.esprit.utils.PageRequest;
import com.esprit.utils.PaginationQueryBuilder;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class CategoryService implements IService<Category> {

    // Allowed columns for sorting to prevent SQL injection
    private static final String[] ALLOWED_SORT_COLUMNS = {
        "id", "name", "description", "type"
    };
    Connection connection;

    /**
     * Constructs a new CategoryService and initializes the database connection.
     * Creates tables if they don't exist.
     */
    public CategoryService() {
        this.connection = DataSource.getInstance().getConnection();
    }


    /**
     * Persist a new Category record with its name and description into the database.
     *
     * @param category the Category containing the name and description to insert
     * @throws RuntimeException if a database error occurs while inserting the category
     */
    @Override
    public void create(final Category category) {
        final String req = "INSERT INTO categories (name, type, description) VALUES (?, ?, ?)";
        try {
            final PreparedStatement statement = this.connection.prepareStatement(req);
            statement.setString(1, category.getName());
            statement.setString(2, category.getType().toString());
            statement.setString(3, category.getDescription());
            statement.executeUpdate();
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }

    }


    /**
     * Retrieve paginated categories from the database.
     * <p>
     * Validates the requested sort column and falls back to default sorting if the column is not allowed.
     * Returns a Page containing the categories for the requested page, the page index, the page size,
     * and the total number of matching elements. On database errors the method logs the error and
     * returns an empty page with totalElements set to 0.
     *
     * @param pageRequest pagination and sorting parameters
     * @return a Page of Category containing page content, current page index, page size, and total elements
     */
    @Override
    public Page<Category> read(PageRequest pageRequest) {
        final List<Category> content = new ArrayList<>();
        final String baseQuery = "SELECT * FROM categories";

        // Validate sort column to prevent SQL injection
        if (pageRequest.hasSorting() &&
            !PaginationQueryBuilder.isValidSortColumn(pageRequest.getSortBy(), ALLOWED_SORT_COLUMNS)) {
            log.warn("Invalid sort column: {}. Using default sorting.", pageRequest.getSortBy());
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
                        .description(rs.getString("description")).type(CategoryType.valueOf(rs.getString("type"))).build());
                }

            }


            return new Page<>(content, pageRequest.getPage(), pageRequest.getSize(), totalElements);

        } catch (final SQLException e) {
            log.error("Error retrieving paginated categories: {}", e.getMessage(), e);
            return new Page<>(content, pageRequest.getPage(), pageRequest.getSize(), 0);
        }

    }


    /**
     * Update an existing category's name and description in the database.
     *
     * @param category the category whose id identifies the row to update; its name and description will be persisted
     * @throws RuntimeException if a database error prevents the update
     */
    @Override
    public void update(final Category category) {
        final String req = "UPDATE categories SET name = ?, type = ?, description = ? WHERE id = ?";
        try {
            final PreparedStatement statement = this.connection.prepareStatement(req);
            statement.setString(1, category.getName());
            statement.setString(2, category.getType().toString());
            statement.setString(3, category.getDescription());
            statement.setLong(4, category.getId());
            statement.executeUpdate();
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }

    }


    /**
     * Delete the given category from the database.
     *
     * @param category the category whose id identifies the record to delete
     * @throws RuntimeException if a database error prevents the deletion
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
                    .description(rs.getString("description")).type(CategoryType.valueOf(rs.getString("type"))).build();
            }

        } catch (final SQLException e) {
            log.error("Error getting category by id: {}", id, e);
        }

        return category;
    }


    /**
     * Finds a category by its name or matching pattern.
     *
     * @param nom the category name or SQL LIKE pattern to match
     * @return the matching Category, or `null` if none was found
     */
    public Category getCategoryByNameAndType(final String nom, final CategoryType type) {
        Category category = null;
        final String req = "SELECT * FROM categories where name LIKE ? and type=?";
        try (final PreparedStatement statement = this.connection.prepareStatement(req)) {
            statement.setString(1, nom);
            statement.setString(2, type.toString());
            final ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                category = Category.builder().id(rs.getLong("id")).name(rs.getString("name"))
                    .description(rs.getString("description")).type(CategoryType.valueOf(rs.getString("type"))).build();
            }

        } catch (final SQLException e) {
            log.error("Error getting category by name: {}", nom, e);
        }

        return category;
    }

    /**
     * Retrieve paginated categories filtered by type from the database.
     *
     * @param type        the category type to filter by
     * @param pageRequest pagination and sorting parameters
     * @return a Page of Category containing filtered page content
     */
    public Page<Category> readByType(final CategoryType type, PageRequest pageRequest) {
        final List<Category> content = new ArrayList<>();
        final String baseQuery = "SELECT * FROM categories WHERE type = ?";

        // Validate sort column to prevent SQL injection
        if (pageRequest.hasSorting() &&
            !PaginationQueryBuilder.isValidSortColumn(pageRequest.getSortBy(), ALLOWED_SORT_COLUMNS)) {
            log.warn("Invalid sort column: {}. Using default sorting.", pageRequest.getSortBy());
            pageRequest = PageRequest.of(pageRequest.getPage(), pageRequest.getSize());
        }

        try {
            // Get total count for filtered results
            final String countQuery = "SELECT COUNT(*) FROM categories WHERE type = ?";
            long totalElements = 0;
            try (PreparedStatement countStmt = connection.prepareStatement(countQuery)) {
                countStmt.setString(1, type.toString());
                ResultSet countRs = countStmt.executeQuery();
                if (countRs.next()) {
                    totalElements = countRs.getLong(1);
                }
            }

            // Build paginated query with type filter
            StringBuilder queryBuilder = new StringBuilder(baseQuery);
            if (pageRequest.hasSorting()) {
                queryBuilder.append(" ORDER BY ").append(pageRequest.getSortBy());
                if (pageRequest.getSortDirection() != null) {
                    queryBuilder.append(" ").append(pageRequest.getSortDirection());
                }
            }
            queryBuilder.append(" LIMIT ").append(pageRequest.getSize());
            queryBuilder.append(" OFFSET ").append(pageRequest.getPage() * pageRequest.getSize());

            try (PreparedStatement stmt = connection.prepareStatement(queryBuilder.toString())) {
                stmt.setString(1, type.toString());
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    content.add(Category.builder()
                        .id(rs.getLong("id"))
                        .name(rs.getString("name"))
                        .description(rs.getString("description"))
                        .type(CategoryType.valueOf(rs.getString("type")))
                        .build());
                }
            }

            return new Page<>(content, pageRequest.getPage(), pageRequest.getSize(), totalElements);

        } catch (final SQLException e) {
            log.error("Error retrieving paginated categories by type: {}", e.getMessage(), e);
            return new Page<>(content, pageRequest.getPage(), pageRequest.getSize(), 0);
        }
    }

    /**
     * Get all categories of a specific type (non-paginated).
     *
     * @param type the category type to filter by
     * @return list of all categories of the specified type
     */
    public List<Category> getAllByType(final CategoryType type) {
        final List<Category> categories = new ArrayList<>();
        final String req = "SELECT * FROM categories WHERE type = ? ORDER BY name";
        try (final PreparedStatement statement = this.connection.prepareStatement(req)) {
            statement.setString(1, type.toString());
            final ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                categories.add(Category.builder()
                    .id(rs.getLong("id"))
                    .name(rs.getString("name"))
                    .description(rs.getString("description"))
                    .type(CategoryType.valueOf(rs.getString("type")))
                    .build());
            }
        } catch (final SQLException e) {
            log.error("Error getting categories by type: {}", type, e);
        }
        return categories;
    }

    @Override
    /**
     * Counts the total number of categories in the database.
     *
     * @return the total count of categories
     */
    public int count() {
        final String req = "SELECT COUNT(*) FROM categories";
        try (final PreparedStatement statement = this.connection.prepareStatement(req)) {
            final ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (final SQLException e) {
            log.error("Error counting categories", e);
        }
        return 0;
    }

    @Override
    /**
     * Retrieves a category by its ID.
     *
     * @param id the ID of the category to retrieve
     * @return the category with the specified ID, or null if not found
     */
    public Category getById(final Long id) {
        return getCategory(id);
    }

    @Override
    /**
     * Retrieves all categories from the database.
     *
     * @return a list of all categories
     */
    public List<Category> getAll() {
        final List<Category> categories = new ArrayList<>();
        final String req = "SELECT * FROM categories ORDER BY name";
        try (final PreparedStatement statement = this.connection.prepareStatement(req)) {
            final ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                categories.add(Category.builder()
                    .id(rs.getLong("id"))
                    .name(rs.getString("name"))
                    .description(rs.getString("description"))
                    .type(CategoryType.valueOf(rs.getString("type")))
                    .build());
            }
        } catch (final SQLException e) {
            log.error("Error getting all categories", e);
        }
        return categories;
    }

    @Override
    /**
     * Searches for categories by name.
     *
     * @param query the search query
     * @return a list of categories matching the search query
     */
    public List<Category> search(final String query) {
        final List<Category> categories = new ArrayList<>();
        final String req = "SELECT * FROM categories WHERE name LIKE ? OR description LIKE ? ORDER BY name";
        try (final PreparedStatement statement = this.connection.prepareStatement(req)) {
            final String searchPattern = "%" + query + "%";
            statement.setString(1, searchPattern);
            statement.setString(2, searchPattern);
            final ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                categories.add(Category.builder()
                    .id(rs.getLong("id"))
                    .name(rs.getString("name"))
                    .description(rs.getString("description"))
                    .type(CategoryType.valueOf(rs.getString("type")))
                    .build());
            }
        } catch (final SQLException e) {
            log.error("Error searching categories", e);
        }
        return categories;
    }

    @Override
    /**
     * Checks if a category exists by its ID.
     *
     * @param id the ID of the category to check
     * @return true if the category exists, false otherwise
     */
    public boolean exists(final Long id) {
        return getCategory(id) != null;
    }
}
