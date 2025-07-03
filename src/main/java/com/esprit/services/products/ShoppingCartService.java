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
     * Performs ShoppingCartService operation.
     *
     * @return the result of the operation
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

    @Override
    /**
     * Performs read operation.
     *
     * @return the result of the operation
     */
    public List<ShoppingCart> read() {
        return null;
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
}
