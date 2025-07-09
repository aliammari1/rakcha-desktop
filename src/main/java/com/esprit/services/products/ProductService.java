package com.esprit.services.products;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.esprit.models.products.Product;
import com.esprit.models.products.ProductCategory;
import com.esprit.services.IService;
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
public class ProductService implements IService<Product> {
    private static final Logger LOGGER = Logger.getLogger(ProductService.class.getName());
    private final Connection connection;

    /**
     * Constructs a new ProductService instance.
     * Initializes database connection and creates tables if they don't exist.
     */
    public ProductService() {
        this.connection = DataSource.getInstance().getConnection();

        // Create tables if they don't exist
        TableCreator tableCreator = new TableCreator(connection);

        // Create product categories table
        tableCreator.createTableIfNotExists("product_categories", """
                    CREATE TABLE product_categories (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        category_name VARCHAR(50) NOT NULL,
                        description VARCHAR(255) NOT NULL
                    )
                """);

        // Create products table
        tableCreator.createTableIfNotExists("products", """
                    CREATE TABLE products (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        name VARCHAR(50) NOT NULL,
                        price INT NOT NULL,
                        image VARCHAR(255) NOT NULL,
                        description VARCHAR(100) NOT NULL,
                        quantity INT NOT NULL
                    )
                """);

        // Create product-category junction table
        tableCreator.createTableIfNotExists("product_category", """
                    CREATE TABLE product_category (
                        product_id BIGINT NOT NULL,
                        category_id BIGINT NOT NULL,
                        PRIMARY KEY (product_id, category_id)
                    )
                """);
    }

    @Override
    /**
     * Creates a new entity in the database.
     *
     * @param entity
     *               the entity to create
     */
    public void create(final Product product) {
        final String req = "INSERT into products(name, price, image, description, quantity) values (?, ?, ?, ?, ?)";
        try {
            ProductService.LOGGER.info("product: " + product);
            final PreparedStatement pst = this.connection.prepareStatement(req,
                    PreparedStatement.RETURN_GENERATED_KEYS);
            pst.setString(1, product.getName());
            pst.setInt(2, product.getPrice());
            pst.setString(3, product.getImage());
            pst.setString(4, product.getDescription());
            pst.setInt(5, product.getQuantity());
            pst.executeUpdate();

            // Get the generated product ID
            final ResultSet generatedKeys = pst.getGeneratedKeys();
            if (generatedKeys.next()) {
                final long productId = generatedKeys.getLong(1);
                // Create product-category relationships
                if (product.getCategories() != null && !product.getCategories().isEmpty()) {
                    createProductCategoryRelations(productId, product.getCategories());
                }
            }
            ProductService.LOGGER.info("Product added!");
        } catch (final SQLException e) {
            ProductService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * @param productId
     * @param categories
     * @throws SQLException
     */
    private void createProductCategoryRelations(final long productId, final List<ProductCategory> categories)
            throws SQLException {
        final String req = "INSERT INTO product_category(product_id, category_id) VALUES (?, ?)";
        try (final PreparedStatement pst = this.connection.prepareStatement(req)) {
            for (final ProductCategory category : categories) {
                pst.setLong(1, productId);
                pst.setLong(2, category.getId());
                pst.addBatch();
            }
            pst.executeBatch();
        }
    }

    @Override
    /**
     * Performs read operation.
     *
     * @return the result of the operation
     */
    public List<Product> read() {
        final List<Product> products = new ArrayList<>();
        final String req = "SELECT DISTINCT p.* FROM products p";
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            final ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                final Product product = Product.builder().id(rs.getLong("id")).name(rs.getString("name"))
                        .price(rs.getInt("price")).image(rs.getString("image")).description(rs.getString("description"))
                        .quantity(rs.getInt("quantity")).categories(getCategoriesForProduct(rs.getLong("id"))).build();
                products.add(product);
                ProductService.LOGGER.info(product.toString());
            }
        } catch (final SQLException e) {
            ProductService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return products;
    }

    /**
     * @param productId
     * @return List<ProductCategory>
     */
    private List<ProductCategory> getCategoriesForProduct(final Long productId) {
        final List<ProductCategory> categories = new ArrayList<>();
        final String req = "SELECT c.* FROM product_categories c "
                + "JOIN product_category pc ON c.id = pc.category_id " + "WHERE pc.product_id = ?";
        try (final PreparedStatement pst = this.connection.prepareStatement(req)) {
            pst.setLong(1, productId);
            final ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                final ProductCategory category = ProductCategory.builder().id(rs.getLong("id"))
                        .categoryName(rs.getString("category_name")).description(rs.getString("description")).build();
                categories.add(category);
            }
        } catch (final SQLException e) {
            ProductService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return categories;
    }

    /**
     * Performs sort operation.
     *
     * @return the result of the operation
     */
    public List<Product> sort(final String sortBy) {
        final List<String> validColumns = Arrays.asList("id", "name", "price", "description", "quantity");
        final List<Product> products = new ArrayList<>();
        if (!validColumns.contains(sortBy)) {
            throw new IllegalArgumentException("Invalid sort parameter");
        }
        final String req = "SELECT * FROM products ORDER BY %s".formatted(sortBy);
        try (final PreparedStatement pst = this.connection.prepareStatement(req)) {
            final ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                final Product product = Product.builder().id(rs.getLong("id")).name(rs.getString("name"))
                        .price(rs.getInt("price")).image(rs.getString("image")).description(rs.getString("description"))
                        .quantity(rs.getInt("quantity")).categories(getCategoriesForProduct(rs.getLong("id"))).build();
                products.add(product);
            }
        } catch (final SQLException e) {
            ProductService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return products;
    }

    @Override
    /**
     * Updates an existing entity in the database.
     *
     * @param entity
     *               the entity to update
     */
    public void update(final Product product) {
        final String req = "UPDATE products SET name = ?, price = ?, description = ?, image = ?, quantity = ? WHERE id = ?";
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            pst.setString(1, product.getName());
            pst.setInt(2, product.getPrice());
            pst.setString(3, product.getDescription());
            pst.setString(4, product.getImage());
            pst.setInt(5, product.getQuantity());
            pst.setLong(6, product.getId());
            pst.executeUpdate();

            // Update category relationships
            updateProductCategoryRelations(product.getId(), product.getCategories());

            ProductService.LOGGER.info("product updated!");
        } catch (final SQLException e) {
            ProductService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * @param productId
     * @param categories
     * @throws SQLException
     */
    private void updateProductCategoryRelations(final Long productId, final List<ProductCategory> categories)
            throws SQLException {
        // First, delete existing relationships
        final String deleteReq = "DELETE FROM produit_categorie WHERE id_produit = ?";
        try (final PreparedStatement deletePst = this.connection.prepareStatement(deleteReq)) {
            deletePst.setLong(1, productId);
            deletePst.executeUpdate();
        }

        // Then, create new relationships
        if (categories != null && !categories.isEmpty()) {
            createProductCategoryRelations(productId, categories);
        }
    }

    @Override
    /**
     * Deletes an entity from the database.
     *
     * @param id
     *           the ID of the entity to delete
     */
    public void delete(final Product product) {
        try {
            final String deleteCategoriesReq = "DELETE FROM product_category WHERE product_id = ?";
            try (final PreparedStatement deleteCatPst = this.connection.prepareStatement(deleteCategoriesReq)) {
                deleteCatPst.setLong(1, product.getId());
                deleteCatPst.executeUpdate();
            }

            final String req = "DELETE FROM products WHERE id = ?";
            try (final PreparedStatement pst = this.connection.prepareStatement(req)) {
                pst.setLong(1, product.getId());
                pst.executeUpdate();
            }

            ProductService.LOGGER.info("product deleted!");
        } catch (final SQLException e) {
            ProductService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Retrieves the ProductById value.
     *
     * @return the ProductById value
     */
    public Product getProductById(final Long productId) {
        Product product = null;
        final String req = "SELECT * FROM products WHERE id = ?";
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            pst.setLong(1, productId);
            final ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                product = Product.builder().id(rs.getLong("id")).name(rs.getString("name")).price(rs.getInt("price"))
                        .image(rs.getString("image")).description(rs.getString("description"))
                        .quantity(rs.getInt("quantity")).categories(getCategoriesForProduct(productId)).build();
            }
        } catch (final SQLException e) {
            ProductService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return product;
    }

    /**
     * Performs checkAvailableStock operation.
     *
     * @return the result of the operation
     */
    public boolean checkAvailableStock(final Long productId, final int requestedQuantity) {
        final Product product = this.getProductById(productId);
        if (null != product) {
            return product.getQuantity() >= requestedQuantity;
        } else {
            return false; // The product doesn't exist
        }
    }

    /**
     * Retrieves the ProductPrice value.
     *
     * @return the ProductPrice value
     */
    public double getProductPrice(final Long productId) {
        final String req = "SELECT price FROM products WHERE id = ?";
        double productPrice = 0;
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            pst.setLong(1, productId);
            final ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                productPrice = rs.getDouble("prix");
            }
        } catch (final SQLException e) {
            ProductService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return productPrice;
    }

    /**
     * Retrieves the ProductsByCategory value.
     *
     * @return the ProductsByCategory value
     */
    public List<Product> getProductsByCategory(final Long categoryId) {
        final List<Product> products = new ArrayList<>();
        final String req = "SELECT p.* FROM products p " + "JOIN product_category pc ON p.id = pc.product_id "
                + "WHERE pc.category_id = ?";
        try (final PreparedStatement pst = this.connection.prepareStatement(req)) {
            pst.setLong(1, categoryId);
            final ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                final Product product = Product.builder().id(rs.getLong("id")).name(rs.getString("name"))
                        .price(rs.getInt("price")).image(rs.getString("image")).description(rs.getString("description"))
                        .quantity(rs.getInt("quantity")).categories(getCategoriesForProduct(rs.getLong("id"))).build();
                products.add(product);
            }
        } catch (final SQLException e) {
            ProductService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return products;
    }

    /**
     * Retrieves the ProductsOrderByQuantityAndStatus value.
     *
     * @return the ProductsOrderByQuantityAndStatus value
     */
    public List<Product> getProductsOrderByQuantityAndStatus() {
        final List<Product> products = new ArrayList<>();
        final String req = """
                SELECT p.id_produit, p.nom, p.prix, p.image, p.description, SUM(ci.quantity) AS total_quantity \
                FROM produit p \
                JOIN orderitem ci ON p.id_produit = ci.id_produit \
                JOIN order c ON ci.idOrder = c.idOrder \
                WHERE c.statu = 'payee' \
                GROUP BY p.id_produit, p.nom, p.prix, p.image, p.description \
                ORDER BY total_quantity DESC\
                """;
        try (final PreparedStatement preparedStatement = this.connection.prepareStatement(req);
                final ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                final Product product = new Product(resultSet.getString("nom"), resultSet.getInt("prix"),
                        resultSet.getString("image"), resultSet.getString("description"), null,
                        resultSet.getInt("total_quantity"));
                product.setId(resultSet.getLong("id_produit"));
                product.setCategories(getCategoriesForProduct(product.getId()));
                products.add(product);
            }
        } catch (final SQLException e) {
            ProductService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return products;
    }
}
