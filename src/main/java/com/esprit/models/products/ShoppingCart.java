package com.esprit.models.products;

import com.esprit.models.users.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Is used to represent a shopping cart containing various products and users.
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
/**
 * Product management entity class for the RAKCHA application. Manages product
 * data and relationships with database persistence.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */

public class ShoppingCart {

    private Long id;

    private int quantity;

    private Product product;

    private User user;

    private LocalDateTime addedAt;

    /**
     * Create a ShoppingCart instance for a new (not yet persisted) cart without an id.
     *
     * @param user     the owner of the cart
     * @param product  the product added to the cart
     * @param quantity the quantity of the product in the cart
     */
    public ShoppingCart(final User user, final Product product, final int quantity) {
        this.user = user;
        this.product = product;
        this.quantity = quantity;
        this.addedAt = LocalDateTime.now();
    }

    /**
     * Legacy constructor with different parameter order.
     *
     * @deprecated Use User, Product, quantity constructor instead
     */
    @Deprecated(forRemoval = true)
    public ShoppingCart(final int quantity, final Product product, final User user) {
        this.quantity = quantity;
        this.product = product;
        this.user = user;
        this.addedAt = LocalDateTime.now();
    }

}


