package com.esprit.services.products;

import com.esprit.exceptions.InsufficientStockException;
import com.esprit.models.common.Category;
import com.esprit.models.products.Product;
import com.esprit.services.IService;
import com.esprit.utils.DataSource;
import com.esprit.utils.Page;
import com.esprit.utils.PageRequest;
import com.esprit.utils.PaginationQueryBuilder;
import lombok.extern.log4j.Log4j2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
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
public class ProductService implements IService<Product> {

    private static final Logger LOGGER = Logger.getLogger(ProductService.class.getName());
    // Allowed columns for sorting to prevent SQL injection
    private static final String[] ALLOWED_SORT_COLUMNS = {
        "id", "name", "price", "image_url", "description", "stock_quantity"
    };
    private final Connection connection;

    /**
     * Constructs a new ProductService instance.
     * Initializes database connection and creates tables if they don't exist.
     */
    public ProductService() {
        this.connection = DataSource.getInstance().getConnection();
    }

    @Override
    /**
     * Creates a new entity in the database.
     *
     * @param entity
     *               the entity to create
     */
    public void create(final Product product) {
        final String req = "INSERT into products(name, price, image_url, description, stock_quantity, category_id) values (?, ?, ?, ?, ?, ?)";
        try {
            ProductService.LOGGER.info("product: " + product);
            final PreparedStatement pst = this.connection.prepareStatement(req,
                PreparedStatement.RETURN_GENERATED_KEYS);
            pst.setString(1, product.getName());
            pst.setDouble(2, product.getPrice());
            pst.setString(3, product.getImageUrl());
            pst.setString(4, product.getDescription());
            pst.setInt(5, product.getStockQuantity());

            // Set category_id if available
            if (product.getCategories() != null && !product.getCategories().isEmpty()) {
                pst.setLong(6, product.getCategories().get(0).getId());
            } else {
                pst.setNull(6, java.sql.Types.INTEGER);
            }
            pst.executeUpdate();
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
    private void createProductCategoryRelations(final long productId, final List<Category> categories)
        throws SQLException {
        // Products now have a direct category_id column, so this method is simplified
        // If you need to keep many-to-many relationships, you'd use a junction table
        if (categories != null && !categories.isEmpty()) {
            final String req = "UPDATE products SET category_id = ? WHERE id = ?";
            try (final PreparedStatement pst = this.connection.prepareStatement(req)) {
                pst.setLong(1, categories.get(0).getId());
                pst.setLong(2, productId);
                pst.executeUpdate();
            }
        }
    }

    @Override
    /**
     * Retrieves products with pagination support.
     *
     * @param pageRequest the pagination parameters
     * @return a page of products
     */
    public Page<Product> read(PageRequest pageRequest) {
        final List<Product> content = new ArrayList<>();
        final String baseQuery = "SELECT DISTINCT p.* FROM products p";

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
                    final Product product = Product.builder().id(rs.getLong("id")).name(rs.getString("name"))
                        .price(rs.getDouble("price")).imageUrl(rs.getString("image_url"))
                        .description(rs.getString("description"))
                        .stockQuantity(rs.getInt("stock_quantity"))
                        .categories(getCategoriesForProduct(rs.getLong("id")))
                        .build();
                    content.add(product);
                }
            }

            return new Page<>(content, pageRequest.getPage(), pageRequest.getSize(), totalElements);

        } catch (final SQLException e) {
            log.error("Error retrieving paginated products: {}", e.getMessage(), e);
            return new Page<>(content, pageRequest.getPage(), pageRequest.getSize(), 0);
        }
    }

    /**
     * @param productId
     * @return List<ProductCategory>
     */
    private List<Category> getCategoriesForProduct(final Long productId) {
        final List<Category> categories = new ArrayList<>();
        // Query the category directly from products table
        final String req = "SELECT c.* FROM categories c WHERE c.id = (SELECT category_id FROM products WHERE id = ?)";
        try (final PreparedStatement pst = this.connection.prepareStatement(req)) {
            pst.setLong(1, productId);
            final ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                final Category category = Category.builder().id(rs.getLong("id"))
                    .name(rs.getString("name")).description(rs.getString("description")).build();
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
                    .price(rs.getDouble("price")).imageUrl(rs.getString("image_url"))
                    .description(rs.getString("description"))
                    .stockQuantity(rs.getInt("stock_quantity"))
                    .categories(getCategoriesForProduct(rs.getLong("id"))).build();
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
        final String req = "UPDATE products SET name = ?, price = ?, description = ?, image_url = ?, stock_quantity = ? WHERE id = ?";
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            pst.setString(1, product.getName());
            pst.setDouble(2, product.getPrice());
            pst.setString(3, product.getDescription());
            pst.setString(4, product.getImageUrl());
            pst.setInt(5, product.getStockQuantity());
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
    private void updateProductCategoryRelations(final Long productId, final List<Category> categories)
        throws SQLException {
        // First, delete existing relationships
        /*
         * final String deleteReq =
         * "DELETE FROM produit_categorie WHERE id_produit = ?";
         * try (final PreparedStatement deletePst =
         * this.connection.prepareStatement(deleteReq)) {
         * deletePst.setLong(1, productId);
         * deletePst.executeUpdate();
         * }
         */

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
            /*
             * final String deleteCategoriesReq =
             * "DELETE FROM product_category WHERE product_id = ?";
             * try (final PreparedStatement deleteCatPst =
             * this.connection.prepareStatement(deleteCategoriesReq)) {
             * deleteCatPst.setLong(1, product.getId());
             * deleteCatPst.executeUpdate();
             * }
             */

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
                product = Product.builder().id(rs.getLong("id")).name(rs.getString("name")).price(rs.getDouble("price"))
                    .imageUrl(rs.getString("image_url")).description(rs.getString("description"))
                    .stockQuantity(rs.getInt("stock_quantity")).categories(getCategoriesForProduct(productId))
                    .build();
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
            return product.getStockQuantity() >= requestedQuantity;
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
                productPrice = rs.getDouble("price");
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
        // Updated query to match the actual database schema
        final String req = "SELECT DISTINCT p.* FROM products p WHERE p.category_id = ?";
        try (final PreparedStatement pst = this.connection.prepareStatement(req)) {
            pst.setLong(1, categoryId);
            final ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                final Product product = Product.builder().id(rs.getLong("id")).name(rs.getString("name"))
                    .price(rs.getDouble("price")).imageUrl(rs.getString("image_url"))
                    .description(rs.getString("description"))
                    .stockQuantity(rs.getInt("stock_quantity"))
                    .categories(getCategoriesForProduct(rs.getLong("id"))).build();
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
            SELECT p.id, p.name, p.price, p.image_url, p.description, SUM(oi.quantity) AS total_quantity \
            FROM products p \
            JOIN order_items oi ON p.id = oi.product_id \
            JOIN orders o ON oi.order_id = o.id \
            WHERE o.status = 'completed' \
            GROUP BY p.id, p.name, p.price, p.image_url, p.description \
            ORDER BY total_quantity DESC\
            """;
        try (final PreparedStatement preparedStatement = this.connection.prepareStatement(req);
             final ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                final Product product = new Product(null, resultSet.getString("name"), resultSet.getString("description"),
                    resultSet.getString("image_url"), resultSet.getDouble("price"),
                    resultSet.getInt("total_quantity"), new ArrayList<>(), new ArrayList<>());
                product.setId(resultSet.getLong("id"));
                product.setCategories(getCategoriesForProduct(product.getId()));
                products.add(product);
            }
        } catch (final SQLException e) {
            ProductService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return products;
    }

    /**
     * Checks if sufficient stock is available for the requested quantity.
     *
     * @param productId         the product ID
     * @param requestedQuantity the quantity needed
     * @return true if stock is available, false otherwise
     */
    public boolean hasStock(Long productId, int requestedQuantity) {
        return checkAvailableStock(productId, requestedQuantity);
    }

    /**
     * Reduces stock for a product by the specified quantity.
     * This is typically called when an order is paid.
     *
     * @param productId the product ID
     * @param quantity  the quantity to deduct
     * @throws InsufficientStockException if stock is insufficient
     */
    public void reduceStock(Long productId, int quantity) throws InsufficientStockException {
        Product product = getProductById(productId);

        if (product == null) {
            throw new InsufficientStockException("Product not found with ID: " + productId);
        }

        if (product.getStockQuantity() < quantity) {
            throw new InsufficientStockException(
                productId,
                product.getName(),
                quantity,
                product.getStockQuantity());
        }

        String req = "UPDATE products SET  stock_quantity = stock_quantity - ? WHERE id = ?";
        try (PreparedStatement pst = connection.prepareStatement(req)) {
            pst.setInt(1, quantity);
            pst.setLong(2, productId);
            int rowsAffected = pst.executeUpdate();

            if (rowsAffected > 0) {
                LOGGER.info(
                    String.format("Stock reduced for product %s (ID: %d). Quantity deducted: %d, New quantity: %d",
                        product.getName(), productId, quantity, product.getStockQuantity() - quantity));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error reducing stock for product ID: " + productId, e);
            throw new InsufficientStockException("Database error while reducing stock");
        }
    }

    /**
     * Restores stock for a product by the specified quantity.
     * This is typically called when an order is cancelled.
     *
     * @param productId the product ID
     * @param quantity  the quantity to restore
     */
    public void restoreStock(Long productId, int quantity) {
        Product product = getProductById(productId);

        if (product == null) {
            LOGGER.warning("Cannot restore stock: Product not found with ID: " + productId);
            return;
        }

        String req = "UPDATE products SET stock_quantity = stock_quantity + ? WHERE id = ?";
        try (PreparedStatement pst = connection.prepareStatement(req)) {
            pst.setInt(1, quantity);
            pst.setLong(2, productId);
            int rowsAffected = pst.executeUpdate();

            if (rowsAffected > 0) {
                LOGGER.info(
                    String.format("Stock restored for product %s (ID: %d). Quantity restored: %d, New quantity: %d",
                        product.getName(), productId, quantity, product.getStockQuantity() + quantity));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error restoring stock for product ID: " + productId, e);
        }
    }

    /**
     * Gets the current available stock for a product.
     *
     * @param productId the product ID
     * @return the available quantity, or 0 if product not found
     */
    public int getAvailableStock(Long productId) {
        Product product = getProductById(productId);
        return product != null ? product.getStockQuantity() : 0;
    }

    /**
     * Searches for products within a specific price range.
     *
     * @param minPrice the minimum price (inclusive)
     * @param maxPrice the maximum price (inclusive)
     * @return list of products within the price range
     */
    public List<Product> searchByPriceRange(int minPrice, int maxPrice) {
        List<Product> products = new ArrayList<>();
        String req = "SELECT * FROM products WHERE price >= ? AND price <= ? ORDER BY price ASC";

        try (PreparedStatement pst = connection.prepareStatement(req)) {
            pst.setInt(1, minPrice);
            pst.setInt(2, maxPrice);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Product product = Product.builder()
                    .id(rs.getLong("id"))
                    .name(rs.getString("name"))
                    .price(rs.getDouble("price"))
                    .imageUrl(rs.getString("image_url"))
                    .description(rs.getString("description"))
                    .stockQuantity(rs.getInt("stock_quantity"))
                    .categories(getCategoriesForProduct(rs.getLong("id")))
                    .build();
                products.add(product);
            }

            LOGGER.info(String.format("Found %d products in price range %d-%d",
                products.size(), minPrice, maxPrice));
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error searching products by price range", e);
        }

        return products;
    }

    /**
     * Counts the total number of products in the database.
     *
     * @return the total count of products
     */
    @Override
    public int count() {
        String query = "SELECT COUNT(*) FROM products";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting products", e);
        }
        return 0;
    }

    /**
     * Retrieves a product by its ID.
     *
     * @param id the ID of the product to retrieve
     * @return the product with the specified ID, or null if not found
     */
    @Override
    public Product getById(final Long id) {
        return getProductById(id);
    }

    /**
     * Retrieves all products from the database.
     *
     * @return a list of all products
     */
    @Override
    public List<Product> getAll() {
        List<Product> products = new ArrayList<>();
        String query = "SELECT * FROM products";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Product product = Product.builder()
                    .id(rs.getLong("id"))
                    .name(rs.getString("name"))
                    .price(rs.getDouble("price"))
                    .imageUrl(rs.getString("image_url"))
                    .description(rs.getString("description"))
                    .stockQuantity(rs.getInt("stock_quantity"))
                    .categories(getCategoriesForProduct(rs.getLong("id")))
                    .build();
                products.add(product);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving all products", e);
        }
        return products;
    }

    /**
     * Searches for products by name or description.
     *
     * @param query the search query
     * @return a list of products matching the search query
     */
    @Override
    public List<Product> search(final String query) {
        List<Product> products = new ArrayList<>();
        final String req = "SELECT * FROM products WHERE name LIKE ? OR description LIKE ? ORDER BY name";
        try (final PreparedStatement pst = this.connection.prepareStatement(req)) {
            final String searchPattern = "%" + query + "%";
            pst.setString(1, searchPattern);
            pst.setString(2, searchPattern);
            final ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Product product = Product.builder()
                    .id(rs.getLong("id"))
                    .name(rs.getString("name"))
                    .price(rs.getDouble("price"))
                    .imageUrl(rs.getString("image_url"))
                    .description(rs.getString("description"))
                    .stockQuantity(rs.getInt("stock_quantity"))
                    .categories(getCategoriesForProduct(rs.getLong("id")))
                    .build();
                products.add(product);
            }
        } catch (final SQLException e) {
            LOGGER.log(Level.SEVERE, "Error searching products", e);
        }
        return products;
    }

    /**
     * Checks if a product exists by its ID.
     *
     * @param id the ID of the product to check
     * @return true if the product exists, false otherwise
     */
    @Override
    public boolean exists(final Long id) {
        return getProductById(id) != null;
    }
}
