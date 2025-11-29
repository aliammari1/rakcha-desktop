package com.esprit.services.products;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.esprit.models.products.ShoppingCart;
import com.esprit.services.IService;
import com.esprit.services.users.UserService;
import com.esprit.utils.DataSource;
import com.esprit.utils.Page;
import com.esprit.utils.PageRequest;
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
public class ShoppingCartService implements IService<ShoppingCart> {
    private static final Logger LOGGER = Logger.getLogger(ShoppingCartService.class.getName());
    private final Connection connection;

    /**
     * Creates a ShoppingCartService and prepares required database resources.
     *
     * Ensures a JDBC connection is obtained and the `shopping_cart` table exists with columns:
     * `id` (BIGINT AUTO_INCREMENT PRIMARY KEY), `produit_id` (BIGINT), `quantity` (INT), and `user_id` (BIGINT).
     */
    public ShoppingCartService() {
        this.connection = DataSource.getInstance().getConnection();

        // Create tables if they don't exist
        TableCreator tableCreator = new TableCreator(connection);
        tableCreator.createTableIfNotExists("shopping_cart", """
                    CREATE TABLE shopping_cart (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        produit_id BIGINT,
                        quantity INT,
                        user_id BIGINT
                    )
                """);
    }


    /**
     * Inserts a shopping cart entry into the database mapping a product to a user with a quantity.
     *
     * Persists the shoppingCart's product id, quantity, and user id into the shopping_cart table. SQL errors are logged and not propagated.
     *
     * @param shoppingCart the shopping cart to persist; its product and user must have valid (non-null) IDs
     */
    @Override
    /**
     * Creates a new entity in the database.
     *
     * @param entity
     *               the entity to create
     */
    public void create(final ShoppingCart shoppingCart) {
        final String req = "INSERT into shopping_cart(produit_id, quantity, user_id) values (?, ?, ?)";
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
     * Retrieve all shopping cart entries for a given user.
     *
     * @param userId the identifier of the user whose shopping cart to retrieve
     * @return a list of ShoppingCart objects belonging to the specified user
     * @throws SQLException if a database access error occurs
     */
    public List<ShoppingCart> readUserShoppingCart(final Long userId) throws SQLException {
        final UserService usersService = new UserService();
        final ProductService productService = new ProductService();
        final List<ShoppingCart> shoppingCarts = new ArrayList<>();
        final String req = "SELECT * from shopping_cart WHERE user_id=?";
        final PreparedStatement ps = this.connection.prepareStatement(req);
        ps.setLong(1, userId);
        final ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            final ShoppingCart shoppingCart = ShoppingCart.builder().id(rs.getLong("id"))
                    .user(usersService.getUserById(userId))
                    .product(productService.getProductById(rs.getLong("produit_id"))).quantity(rs.getInt("quantity"))
                    .build();
            shoppingCarts.add(shoppingCart);
        }

        return shoppingCarts;
    }


    /**
     * Update the shopping cart record identified by its id with the provided product, quantity, and user.
     *
     * @param shoppingCart the ShoppingCart whose id identifies the row to update; its product, quantity, and user values replace the existing values in the database
     */
    @Override
    /**
     * Updates an existing entity in the database.
     *
     * @param entity
     *               the entity to update
     */
    public void update(final ShoppingCart shoppingCart) {
        final String req = "UPDATE shopping_cart SET produit_id = ?, quantity = ?, user_id = ? WHERE id = ?";
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


    /**
     * Remove the shopping_cart row identified by the given ShoppingCart's product and user.
     *
     * @param shoppingCart the ShoppingCart whose product ID and user ID are used to locate and delete the row
     */
    @Override
    /**
     * Deletes an entity from the database.
     *
     * @param id
     *           the ID of the entity to delete
     */
    public void delete(final ShoppingCart shoppingCart) {
        final String req = "DELETE from shopping_cart where produit_id = ? and user_id = ?";
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


    /**
     * Provide paginated access to shopping cart entries based on the given request.
     *
     * @param pageRequest pagination and sorting parameters
     * @return a page containing shopping cart entries matching the request
     * @throws UnsupportedOperationException always thrown because this method is not implemented
     */
    @Override
    public Page<ShoppingCart> read(PageRequest pageRequest) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'read'");
    }

}
