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
public class CategoryService implements IService<ProductCategory> {
    private static final Logger LOGGER = Logger.getLogger(CategoryService.class.getName());
    private final Connection connection;

    /**
     * Performs CategoryService operation.
     *
     * @return the result of the operation
     */
    public CategoryService() {
        this.connection = DataSource.getInstance().getConnection();
    }

    @Override
    /**
     * Creates a new entity in the database.
     *
     * @param entity
     *            the entity to create
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

    @Override
    /**
     * Performs read operation.
     *
     * @return the result of the operation
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

    @Override
    /**
     * Updates an existing entity in the database.
     *
     * @param entity
     *            the entity to update
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

    @Override
    /**
     * Deletes an entity from the database.
     *
     * @param id
     *            the ID of the entity to delete
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
     * Retrieves the Category value.
     *
     * @return the Category value
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
     * Retrieves the CategoryByName value.
     *
     * @return the CategoryByName value
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
     * Retrieves the AllCategoriesNames value.
     *
     * @return the AllCategoriesNames value
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
     * Retrieves the AllCategories value.
     *
     * @return the AllCategories value
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
     * Performs searchCategoriesByName operation.
     *
     * @return the result of the operation
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
     * Retrieves the CategoriesForProduct value.
     *
     * @return the CategoriesForProduct value
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
     * Performs addProductToCategory operation.
     *
     * @return the result of the operation
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
     * Performs removeProductFromCategory operation.
     *
     * @return the result of the operation
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
     * Retrieves the ProductCountForCategory value.
     *
     * @return the ProductCountForCategory value
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
}
