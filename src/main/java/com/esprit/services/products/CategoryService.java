package com.esprit.services.products;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.esprit.models.products.ProductCategory;
import com.esprit.services.IService;
import com.esprit.utils.DataSource;
import com.esprit.utils.Page;
import com.esprit.utils.PageRequest;
import com.esprit.utils.TableCreator;

import lombok.extern.slf4j.Slf4j;

/**
 * Service class providing business logic for product categories in the RAKCHA
 * application.
 * Implements CRUD operations and business rules for category data management.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
public class CategoryService implements IService<ProductCategory> {
    private static final Logger LOGGER = Logger.getLogger(CategoryService.class.getName());
    private final Connection connection;

    /**
     * Initialize a CategoryService by obtaining a JDBC connection and ensuring the product_categories table exists.
     *
     * The created table contains columns for id, category_name, and description.
     */
    public CategoryService() {
        this.connection = DataSource.getInstance().getConnection();

        // Create tables if they don't exist
        try {
            TableCreator tableCreator = new TableCreator(this.connection);

            // Create product_categories table
            String createProductCategoriesTable = """
                    CREATE TABLE product_categories (
                        id BIGINT PRIMARY KEY AUTO_INCREMENT,
                        category_name VARCHAR(255) NOT NULL UNIQUE,
                        description TEXT
                    )
                    """;
            tableCreator.createTableIfNotExists("product_categories", createProductCategoriesTable);

        } catch (Exception e) {
            log.error("Error creating tables for CategoryService", e);
        }

    }


    /**
     * Insert a new product category into the product_categories table.
     *
     * @param productCategory the ProductCategory whose `categoryName` and `description` will be persisted
     */
    @Override
    /**
     * Creates a new entity in the database.
     *
     * @param entity
     *               the entity to create
     */
    public void create(final ProductCategory productCategory) {
        final String req = "INSERT into product_categories(category_name, description) values (?, ?)";
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            pst.setString(1, productCategory.getCategoryName());
            pst.setString(2, productCategory.getDescription());
            pst.executeUpdate();
            CategoryService.LOGGER.info("Category added!");
        } catch (final SQLException e) {
            CategoryService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

    }


    /**
     * Retrieve all product categories from the database.
     *
     * @return a list of ProductCategory objects for every row in the product_categories table; returns an empty list if no categories are found or if a database error occurs
     */
    public List<ProductCategory> read() {
        final List<ProductCategory> categories = new ArrayList<>();
        final String req = "SELECT * from product_categories";
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            final ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                final ProductCategory category = ProductCategory.builder().id(rs.getLong("id"))
                        .categoryName(rs.getString("category_name")).description(rs.getString("description")).build();
                categories.add(category);
            }

        } catch (final SQLException e) {
            CategoryService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

        return categories;
    }


    /**
     * Updates the database record for the given ProductCategory using its id, setting its name and description.
     *
     * @param productCategory the category whose id identifies the row to update and whose fields provide the new values
     */
    @Override
    /**
     * Updates an existing entity in the database.
     *
     * @param entity
     *               the entity to update
     */
    public void update(final ProductCategory productCategory) {
        final String req = "UPDATE product_categories set category_name = ?, description = ? where id = ?";
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            pst.setString(1, productCategory.getCategoryName());
            pst.setString(2, productCategory.getDescription());
            pst.setLong(3, productCategory.getId());
            pst.executeUpdate();
            CategoryService.LOGGER.info("category updated!");
        } catch (final SQLException e) {
            CategoryService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

    }


    /**
     * Removes the product category identified by the given object's ID from the database.
     *
     * @param productCategory the ProductCategory whose ID will be used to delete the corresponding record
     */
    @Override
    /**
     * Deletes an entity from the database.
     *
     * @param id
     *           the ID of the entity to delete
     */
    public void delete(final ProductCategory productCategory) {
        final String req = "DELETE from product_categories where id = ?";
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            pst.setLong(1, productCategory.getId());
            pst.executeUpdate();
            CategoryService.LOGGER.info("category deleted!");
        } catch (final SQLException e) {
            CategoryService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

    }


    /**
     * Retrieves a product category by its ID.
     *
     * @param categoryId the ID of the category to retrieve
     * @return the ProductCategory with the specified ID, or null if not found
     */
    public ProductCategory getCategory(final long categoryId) {
        ProductCategory category = null;
        final String req = "SELECT * from product_categories where id = ?";
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            pst.setLong(1, categoryId);
            final ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                category = ProductCategory.builder().id(rs.getLong("id")).categoryName(rs.getString("category_name"))
                        .description(rs.getString("description")).build();
            }

        } catch (final SQLException e) {
            CategoryService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

        return category;
    }


    /**
     * Retrieves a product category by its name.
     *
     * @param categoryName the category name to look up
     * @return the ProductCategory with the specified name, or null if not found
     */
    public ProductCategory getCategoryByName(final String categoryName) {
        ProductCategory category = null;
        final String req = "SELECT * from product_categories where category_name = ?";
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            pst.setString(1, categoryName);
            final ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                category = ProductCategory.builder().id(rs.getLong("id")).categoryName(rs.getString("category_name"))
                        .description(rs.getString("description")).build();
            }

        } catch (final SQLException e) {
            CategoryService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

        return category;
    }


    /**
     * Gets all product category names.
     *
     * @return a list containing the category name for each product category; empty list if there are no categories
     */
    public List<String> getAllCategoriesNames() {
        final List<String> categoryNames = new ArrayList<>();
        final List<ProductCategory> categories = this.read();
        for (final ProductCategory productCategory : categories) {
            categoryNames.add(productCategory.getCategoryName());
        }

        return categoryNames;
    }


    /**
     * Retrieve all category names.
     *
     * @return a list of category name strings; empty list if there are no categories
     */
    public List<String> getAllCategories() {
        final List<String> categoryNames = new ArrayList<>();
        final List<ProductCategory> categories = this.read();
        for (final ProductCategory productCategory : categories) {
            categoryNames.add(productCategory.getCategoryName());
        }

        return categoryNames;
    }


    /**
     * Finds product categories whose names contain the specified keyword.
     *
     * @param searchKeyword the substring to match within category names
     * @return a list of ProductCategory objects whose categoryName contains the keyword; empty list if no matches
     */
    public List<ProductCategory> searchCategoriesByName(final String searchKeyword) {
        final List<ProductCategory> result = new ArrayList<>();
        try {
            final String query = "SELECT * FROM product_categories WHERE category_name LIKE ?";
            final PreparedStatement statement = this.connection.prepareStatement(query);
            statement.setString(1, "%" + searchKeyword + "%");
            final ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                final ProductCategory category = ProductCategory.builder().id(resultSet.getLong("id"))
                        .categoryName(resultSet.getString("category_name"))
                        .description(resultSet.getString("description")).build();
                result.add(category);
            }

            statement.close();
            resultSet.close();
        } catch (final SQLException e) {
            CategoryService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

        return result;
    }


    /**
     * Retrieve the ProductCategory objects linked to the given product.
     *
     * @param productId the product's id to look up categories for
     * @return a list of ProductCategory instances associated with the product; an empty list if none are found
     */
    public List<ProductCategory> getCategoriesForProduct(final int productId) {
        final List<ProductCategory> categories = new ArrayList<>();
        final String req = "SELECT c.* FROM product_categories c "
                + "JOIN product_category pc ON c.id = pc.category_id " + "WHERE pc.product_id = ?";
        try (final PreparedStatement pst = this.connection.prepareStatement(req)) {
            pst.setInt(1, productId);
            final ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                final ProductCategory category = ProductCategory.builder().id(rs.getLong("id"))
                        .categoryName(rs.getString("category_name")).description(rs.getString("description")).build();
                categories.add(category);
            }

        } catch (final SQLException e) {
            CategoryService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

        return categories;
    }


    /**
     * Adds a product to a category in the database.
     *
     * @param productId  the ID of the product to add to the category
     * @param categoryId the ID of the category to add the product to
     */
    public void addProductToCategory(final int productId, final int categoryId) {
        final String req = "INSERT INTO product_category(product_id, category_id) VALUES (?, ?)";
        try (final PreparedStatement pst = this.connection.prepareStatement(req)) {
            pst.setInt(1, productId);
            pst.setInt(2, categoryId);
            pst.executeUpdate();
            CategoryService.LOGGER.info("Product added to category!");
        } catch (final SQLException e) {
            CategoryService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

    }


    /**
     * Removes a product from a category in the database.
     *
     * @param productId  the ID of the product to remove from the category
     * @param categoryId the ID of the category to remove the product from
     */
    public void removeProductFromCategory(final int productId, final int categoryId) {
        final String req = "DELETE FROM product_category WHERE product_id = ? AND category_id = ?";
        try (final PreparedStatement pst = this.connection.prepareStatement(req)) {
            pst.setInt(1, productId);
            pst.setInt(2, categoryId);
            pst.executeUpdate();
            CategoryService.LOGGER.info("Product removed from category!");
        } catch (final SQLException e) {
            CategoryService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

    }


    /**
     * Gets the number of products in a specific category.
     *
     * @param categoryId the ID of the category
     * @return the number of products in the category
     */
    public int getProductCountForCategory(final int categoryId) {
        int count = 0;
        final String req = "SELECT COUNT(*) as product_count FROM product_category WHERE category_id = ?";
        try (final PreparedStatement pst = this.connection.prepareStatement(req)) {
            pst.setInt(1, categoryId);
            final ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                count = rs.getInt("product_count");
            }

        } catch (final SQLException e) {
            CategoryService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

        return count;
    }


    /**
     * Retrieves a paginated page of product categories according to the supplied page request.
     *
     * <p>The returned page contains the categories for the requested page number and size, and
     * may include pagination metadata such as total elements and total pages.</p>
     *
     * @param pageRequest pagination and sorting parameters for the query
     * @return a {@code Page<ProductCategory>} containing the categories and pagination metadata
     * @throws UnsupportedOperationException if pagination is not implemented by this service
     */
    @Override
    public Page<ProductCategory> read(PageRequest pageRequest) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'read'");
    }

}
