package com.esprit.services.products;

import com.esprit.models.products.ShoppingCart;
import com.esprit.services.IService;
import com.esprit.services.users.UserService;
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
import java.util.logging.Level;
import java.util.logging.Logger;

@Slf4j
/**
 * Service class providing business logic for the RAKCHA application. Implements
 * CRUD operations and business rules for data management.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class ShoppingCartService implements IService<ShoppingCart> {

    private static final Logger LOGGER = Logger.getLogger(ShoppingCartService.class.getName());
    // Allowed columns for sorting to prevent SQL injection
    private static final String[] ALLOWED_SORT_COLUMNS = {
        "id", "product_id", "quantity", "user_id"
    };
    private final Connection connection;

    /**
     * Constructs a new ShoppingCartService instance.
     * Initializes database connection and creates tables if they don't exist.
     */
    public ShoppingCartService() {
        this.connection = DataSource.getInstance().getConnection();
    }

    @Override
    /**
     * Creates a new entity in the database.
     *
     * @param entity
     *               the entity to create
     */
    public void create(final ShoppingCart shoppingCart) {
        final String req = "INSERT into shopping_carts(product_id, quantity, user_id) values (?, ?, ?)";
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            pst.setLong(1, shoppingCart.getProduct().getId());
            pst.setInt(2, shoppingCart.getQuantity());
            pst.setLong(3, shoppingCart.getUser().getId());
            pst.executeUpdate();
            ShoppingCartService.LOGGER.info("ShoppingCart filled!");
        } catch (final SQLException e) {
            ShoppingCartService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Performs readUserShoppingCart operation.
     *
     * @return the result of the operation
     */
    public List<ShoppingCart> readUserShoppingCart(final Long userId) throws SQLException {
        final UserService usersService = new UserService();
        final ProductService productService = new ProductService();
        final List<ShoppingCart> shoppingCarts = new ArrayList<>();
        final String req = "SELECT * from shopping_carts WHERE user_id=?";
        final PreparedStatement ps = this.connection.prepareStatement(req);
        ps.setLong(1, userId);
        final ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            final ShoppingCart shoppingCart = ShoppingCart.builder().id(rs.getLong("id"))
                .user(usersService.getUserById(userId))
                .product(productService.getProductById(rs.getLong("product_id"))).quantity(rs.getInt("quantity"))
                .build();
            shoppingCarts.add(shoppingCart);
        }
        return shoppingCarts;
    }

    @Override
    /**
     * Updates an existing entity in the database.
     *
     * @param entity
     *               the entity to update
     */
    public void update(final ShoppingCart shoppingCart) {
        final String req = "UPDATE shopping_carts SET product_id = ?, quantity = ?, user_id = ? WHERE id = ?";
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            pst.setLong(1, shoppingCart.getProduct().getId());
            pst.setInt(2, shoppingCart.getQuantity());
            pst.setLong(3, shoppingCart.getUser().getId());
            pst.setLong(4, shoppingCart.getId());
            pst.executeUpdate();
            ShoppingCartService.LOGGER.info("ShoppingCart updated!");
        } catch (final SQLException e) {
            ShoppingCartService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @Override
    /**
     * Deletes an entity from the database.
     *
     * @param id
     *           the ID of the entity to delete
     */
    public void delete(final ShoppingCart shoppingCart) {
        final String req = "DELETE from shopping_carts where product_id = ? and user_id = ?";
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            pst.setLong(1, shoppingCart.getProduct().getId());
            pst.setLong(2, shoppingCart.getUser().getId());
            pst.executeUpdate();
            ShoppingCartService.LOGGER.info("ShoppingCart deleted!");
        } catch (final SQLException e) {
            ShoppingCartService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @Override
    public Page<ShoppingCart> read(PageRequest pageRequest) {
        final List<ShoppingCart> content = new ArrayList<>();
        final String baseQuery = "SELECT * FROM shopping_carts";

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
                final UserService usersService = new UserService();
                final ProductService productService = new ProductService();

                while (rs.next()) {
                    try {
                        content.add(ShoppingCart.builder()
                            .id(rs.getLong("id"))
                            .user(usersService.getUserById(rs.getLong("user_id")))
                            .product(productService.getProductById(rs.getLong("product_id")))
                            .quantity(rs.getInt("quantity"))
                            .build());
                    } catch (Exception e) {
                        log.warn("Error loading shopping cart item for ID: " + rs.getLong("id"), e);
                    }
                }
            }

            return new Page<>(content, pageRequest.getPage(), pageRequest.getSize(), totalElements);

        } catch (final SQLException e) {
            log.error("Error retrieving paginated shopping carts: {}", e.getMessage(), e);
            return new Page<>(content, pageRequest.getPage(), pageRequest.getSize(), 0);
        }
    }

    /**
     * Counts the total number of shopping cart items in the database.
     *
     * @return the total count of shopping cart items
     */
    @Override
    public int count() {
        String query = "SELECT COUNT(*) FROM shopping_carts";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting shopping carts", e);
        }
        return 0;
    }

    /**
     * Retrieves a shopping cart item by its ID.
     *
     * @param id the ID of the shopping cart item to retrieve
     * @return the shopping cart item with the specified ID, or null if not found
     */
    @Override
    public ShoppingCart getById(final Long id) {
        String query = "SELECT * FROM shopping_carts WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                UserService userService = new UserService();
                ProductService productService = new ProductService();
                return ShoppingCart.builder()
                    .id(rs.getLong("id"))
                    .user(userService.getUserById(rs.getLong("user_id")))
                    .product(productService.getProductById(rs.getLong("product_id")))
                    .quantity(rs.getInt("quantity"))
                    .build();
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving shopping cart by id: " + id, e);
        }
        return null;
    }

    /**
     * Retrieves all shopping cart items from the database.
     *
     * @return a list of all shopping cart items
     */
    @Override
    public List<ShoppingCart> getAll() {
        List<ShoppingCart> carts = new ArrayList<>();
        String query = "SELECT * FROM shopping_carts";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            UserService userService = new UserService();
            ProductService productService = new ProductService();
            while (rs.next()) {
                try {
                    carts.add(ShoppingCart.builder()
                        .id(rs.getLong("id"))
                        .user(userService.getUserById(rs.getLong("user_id")))
                        .product(productService.getProductById(rs.getLong("product_id")))
                        .quantity(rs.getInt("quantity"))
                        .build());
                } catch (Exception e) {
                    log.warn("Error loading shopping cart item for ID: " + rs.getLong("id"), e);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving all shopping carts", e);
        }
        return carts;
    }

    /**
     * Searches for shopping cart items by product or user.
     *
     * @param query the search query
     * @return a list of shopping cart items matching the search query
     */
    @Override
    public List<ShoppingCart> search(final String query) {
        List<ShoppingCart> carts = new ArrayList<>();
        final String req = "SELECT * FROM shopping_carts WHERE user_id LIKE ? OR product_id LIKE ? ORDER BY id DESC";
        try (final PreparedStatement pst = this.connection.prepareStatement(req)) {
            final String searchPattern = "%" + query + "%";
            pst.setString(1, searchPattern);
            pst.setString(2, searchPattern);
            final ResultSet rs = pst.executeQuery();
            UserService userService = new UserService();
            ProductService productService = new ProductService();
            while (rs.next()) {
                try {
                    carts.add(ShoppingCart.builder()
                        .id(rs.getLong("id"))
                        .user(userService.getUserById(rs.getLong("user_id")))
                        .product(productService.getProductById(rs.getLong("product_id")))
                        .quantity(rs.getInt("quantity"))
                        .build());
                } catch (Exception e) {
                    log.warn("Error loading shopping cart item for ID: " + rs.getLong("id"), e);
                }
            }
        } catch (final SQLException e) {
            LOGGER.log(Level.SEVERE, "Error searching shopping carts", e);
        }
        return carts;
    }

    /**
     * Checks if a shopping cart item exists by its ID.
     *
     * @param id the ID of the shopping cart item to check
     * @return true if the shopping cart item exists, false otherwise
     */
    @Override
    public boolean exists(final Long id) {
        return getById(id) != null;
    }
}
