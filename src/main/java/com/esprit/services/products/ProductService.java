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
public class ProductService implements IService<Product> {
    private static final Logger LOGGER = Logger.getLogger(ProductService.class.getName());
    private final Connection connection;

    // Allowed columns for sorting to prevent SQL injection
    private static final String[] ALLOWED_SORT_COLUMNS = {
            "id", "name", "price", "image", "description", "quantity"
    }
;

    /**
     * Initialize the ProductService by opening a database connection and ensuring required product-related tables exist.
     *
     * Ensures the presence of the "product_categories", "products", and "product_category" tables, creating them if they are missing.
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


    /**
     * Inserts the given Product into the database and establishes its category relations.
     *
     * This saves the product fields (name, price, image, description, quantity), retrieves
     * the generated product ID, and creates entries in the product_category join table when
     * the product has associated categories.
     *
     * @param product the product to persist; its assigned database ID is used to create category relations when categories are present
     */
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
     * Create associations between a product and the provided categories by inserting rows into the product_category table.
     *
     * @param productId the ID of the product to associate with categories
     * @param categories the list of categories to link to the product; each category's ID will be used
     * @throws SQLException if a database access error occurs while persisting the relations
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


    /**
     * Retrieve a paginated Page of products for the requested page index, size, and optional sort.
     *
     * @param pageRequest pagination parameters (page index, page size, and optional sort column/direction)
     * @return a Page<Product> containing the page content, the requested page index and size, and the total number of matching elements
     */
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
                            .price(rs.getInt("price")).image(rs.getString("image"))
                            .description(rs.getString("description"))
                            .quantity(rs.getInt("quantity")).categories(getCategoriesForProduct(rs.getLong("id")))
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
         * Retrieves the categories associated with a given product.
         *
         * @param productId the product's ID whose categories should be retrieved
         * @return a list of ProductCategory objects linked to the product; an empty list if no categories are found or if a SQL error occurs
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
     * Retrieve all products ordered by the specified column.
     *
     * @param sortBy the column name to sort by; allowed values: "id", "name", "price", "description", "quantity"
     * @return a list of products ordered by the specified column; empty if no products are found or a database error occurs
     * @throws IllegalArgumentException if `sortBy` is not one of the allowed column names
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


    /**
     * Updates the specified product's fields and refreshes its category associations in the database.
     *
     * @param product the Product with a non-null id whose name, price, description, image, quantity, and categories will replace the stored values
     */
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
     * Replaces all category associations for the given product with the supplied categories.
     *
     * Deletes existing product–category links for the product and, if the list is non-empty, inserts relations for each category in the provided list.
     *
     * @param productId the product id whose category relations will be replaced
     * @param categories list of categories to associate with the product; if null or empty no new relations are created
     * @throws SQLException if a database error occurs while deleting or creating relations
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


    /**
     * Removes the given product and its product–category associations from the database.
     *
     * @param product the product to delete; its `id` identifies the database record to remove
     */
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
     * Retrieves the product with the specified id.
     *
     * @param productId the id of the product to retrieve
     * @return the Product with the given id, or null if no matching product exists or a database error occurs
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
     * Determine whether a product has at least the requested quantity in stock.
     *
     * @param productId the ID of the product to check
     * @param requestedQuantity the quantity to verify is available
     * @return `true` if the product exists and its quantity is greater than or equal to the requested quantity, `false` otherwise
     */
    public boolean checkAvailableStock(final Long productId, final int requestedQuantity) {
        final Product product = this.getProductById(productId);
        if (null != product) {
            return product.getQuantity() >= requestedQuantity;
        }
 else {
            return false; // The product doesn't exist
        }

    }


    /**
     * Retrieve the price of the product identified by the given ID.
     *
     * @param productId the product's database identifier
     * @return the product's price, or 0 if the product is not found or an error occurs
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
     * Retrieve all products associated with the specified category ID.
     *
     * @param categoryId the ID of the category to fetch products for
     * @return a list of products that belong to the category; empty if none are found or an error occurs
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
     * Returns products sorted by total quantity sold in paid orders, highest first.
     *
     * Each returned Product has its id, name, price, image, description, associated categories,
     * and its quantity field set to the aggregated total quantity sold.
     *
     * @return a list of products ordered by total quantity sold (descending)
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
