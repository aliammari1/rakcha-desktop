package com.esprit.services.products;

import com.esprit.models.products.OrderItem;
import com.esprit.models.products.Product;
import com.esprit.models.products.ShoppingCart;
import com.esprit.models.users.User;
import com.esprit.services.users.UserService;
import com.esprit.utils.DataSource;
import lombok.extern.log4j.Log4j2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service class for managing shopping cart operations.
 * Provides a convenient facade for cart-related operations used by controllers.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Log4j2
public class CartService {

    private static final Logger LOGGER = Logger.getLogger(CartService.class.getName());
    private final Connection connection;
    private final ShoppingCartService shoppingCartService;
    private final ProductService productService;
    private final UserService userService;

    /**
     * Constructs a new CartService instance.
     */
    public CartService() {
        this.connection = DataSource.getInstance().getConnection();
        this.shoppingCartService = new ShoppingCartService();
        this.productService = new ProductService();
        this.userService = new UserService();
    }

    /**
     * Gets the shopping cart for a user.
     *
     * @param user the user
     * @return list of ShoppingCart items
     */
    public List<ShoppingCart> getShoppingCartByUser(User user) {
        try {
            return shoppingCartService.readUserShoppingCart(user.getId());
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting shopping cart for user", e);
            return new ArrayList<>();
        }
    }

    /**
     * Gets the shopping cart for a user by user ID.
     *
     * @param userId the ID of the user
     * @return list of ShoppingCart items
     */
    public List<ShoppingCart> getShoppingCartByUserId(Long userId) {
        try {
            return shoppingCartService.readUserShoppingCart(userId);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting shopping cart for user", e);
            return new ArrayList<>();
        }
    }

    /**
     * Adds a product to the user's cart.
     *
     * @param user     the user
     * @param product  the product to add
     * @param quantity the quantity to add
     * @throws SQLException if a database error occurs
     */
    public void addToCart(User user, Product product, int quantity) throws SQLException {
        // Check if product already exists in cart
        ShoppingCart existingItem = getCartItem(user.getId(), product.getId());

        if (existingItem != null) {
            // Update quantity
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            shoppingCartService.update(existingItem);
            LOGGER.info("Updated cart item quantity");
        } else {
            // Add new item
            ShoppingCart cartItem = ShoppingCart.builder()
                .user(user)
                .product(product)
                .quantity(quantity)
                .build();
            shoppingCartService.create(cartItem);
            LOGGER.info("Added new item to cart");
        }
    }

    /**
     * Adds a product to the cart by IDs.
     *
     * @param userId    the ID of the user
     * @param productId the ID of the product
     * @param quantity  the quantity to add
     * @throws SQLException if a database error occurs
     */
    public void addToCart(Long userId, Long productId, int quantity) throws SQLException {
        User user = userService.getUserById(userId);
        Product product = productService.getProductById(productId);

        if (user != null && product != null) {
            addToCart(user, product, quantity);
        } else {
            throw new SQLException("User or Product not found");
        }
    }

    /**
     * Removes a product from the user's cart.
     *
     * @param user    the user
     * @param product the product to remove
     */
    public void removeFromShoppingCart(User user, Product product) {
        ShoppingCart cartItem = ShoppingCart.builder()
            .user(user)
            .product(product)
            .build();
        shoppingCartService.delete(cartItem);
        LOGGER.info("Removed item from cart");
    }

    /**
     * Removes a product from the cart by IDs.
     *
     * @param userId    the ID of the user
     * @param productId the ID of the product
     */
    public void removeFromShoppingCart(Long userId, Long productId) {
        String query = "DELETE FROM shopping_carts WHERE user_id = ? AND product_id = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setLong(1, userId);
            pst.setLong(2, productId);
            pst.executeUpdate();
            LOGGER.info("Removed item from cart");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error removing item from cart", e);
        }
    }

    /**
     * Clears all items from a user's shopping cart.
     *
     * @param user the user
     */
    public void clearShoppingCart(User user) {
        clearShoppingCart(user.getId());
    }

    /**
     * Clears all items from a user's shopping cart by user ID.
     *
     * @param userId the ID of the user
     */
    public void clearShoppingCart(Long userId) {
        String query = "DELETE FROM shopping_carts WHERE user_id = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setLong(1, userId);
            pst.executeUpdate();
            LOGGER.info("Cleared shopping cart");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error clearing shopping cart", e);
        }
    }

    /**
     * Updates the quantity of an item in the cart.
     *
     * @param userId    the ID of the user
     * @param productId the ID of the product
     * @param quantity  the new quantity
     */
    public void updateQuantity(Long userId, Long productId, int quantity) {
        if (quantity <= 0) {
            removeFromShoppingCart(userId, productId);
            return;
        }

        String query = "UPDATE shopping_carts SET quantity = ? WHERE user_id = ? AND product_id = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, quantity);
            pst.setLong(2, userId);
            pst.setLong(3, productId);
            pst.executeUpdate();
            LOGGER.info("Updated cart item quantity");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating cart quantity", e);
        }
    }

    /**
     * Gets the total number of items in a user's cart.
     *
     * @param userId the ID of the user
     * @return the total number of items
     */
    public int getCartItemCount(Long userId) {
        String query = "SELECT COALESCE(SUM(quantity), 0) as total FROM shopping_carts WHERE user_id = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setLong(1, userId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting cart items", e);
        }
        return 0;
    }

    /**
     * Gets the total value of a user's cart.
     *
     * @param userId the ID of the user
     * @return the total cart value
     */
    public double getCartTotal(Long userId) {
        String query = "SELECT COALESCE(SUM(sc.quantity * p.price), 0) as total " +
            "FROM shopping_carts sc " +
            "JOIN products p ON sc.product_id = p.id " +
            "WHERE sc.user_id = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setLong(1, userId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error calculating cart total", e);
        }
        return 0.0;
    }

    /**
     * Checks if a product is in the user's cart.
     *
     * @param userId    the ID of the user
     * @param productId the ID of the product
     * @return true if the product is in the cart
     */
    public boolean isInCart(Long userId, Long productId) {
        String query = "SELECT id FROM shopping_carts WHERE user_id = ? AND product_id = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setLong(1, userId);
            pst.setLong(2, productId);
            ResultSet rs = pst.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking if product is in cart", e);
            return false;
        }
    }

    /**
     * Gets a specific cart item.
     *
     * @param userId    the ID of the user
     * @param productId the ID of the product
     * @return the ShoppingCart item or null
     */
    private ShoppingCart getCartItem(Long userId, Long productId) {
        String query = "SELECT * FROM shopping_carts WHERE user_id = ? AND product_id = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setLong(1, userId);
            pst.setLong(2, productId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                User user = userService.getUserById(userId);
                Product product = productService.getProductById(productId);
                return ShoppingCart.builder()
                    .id(rs.getLong("id"))
                    .user(user)
                    .product(product)
                    .quantity(rs.getInt("quantity"))
                    .build();
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting cart item", e);
        }
        return null;
    }

    /**
     * Update an order item in the cart.
     *
     * @param orderItem the order item to update
     */
    public void updateOrderItem(OrderItem orderItem) {
        if (orderItem != null && orderItem.getId() != null) {
            updateQuantity(orderItem.getId(), orderItem.getProductId(), orderItem.getQuantity());
        }
    }

    /**
     * Remove an item from the cart by ID.
     *
     * @param cartItemId the ID of the cart item to remove
     */
    public void removeFromCart(Long cartItemId) {
        String query = "DELETE FROM shopping_cart WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, cartItemId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error removing cart item", e);
        }
    }

    /**
     * Clear the entire cart for a user (alias).
     *
     * @param userId the user ID
     */
    public void clearCart(Long userId) {
        this.clearShoppingCart(userId);
    }
}

