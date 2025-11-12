package com.esprit.models.products;

import com.esprit.models.users.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    /**
     * Create a ShoppingCart instance for a new (not yet persisted) cart without an id.
     *
     * @param quantity the quantity of the product in the cart
     * @param product  the product added to the cart
     * @param user     the owner of the cart
     */
    public ShoppingCart(final int quantity, final Product product, final User user) {
        this.quantity = quantity;
        this.product = product;
        this.user = user;
    }

}
