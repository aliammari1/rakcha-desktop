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
     * Constructor without id for creating new shopping cart instances.
     *
     * @param quantity
     *                 The quantity of the product in the cart.
     * @param product
     *                 The product associated with the cart.
     * @param user
     *                 The user associated with the cart.
     */
    public ShoppingCart(final int quantity, final Product product, final User user) {
        this.quantity = quantity;
        this.product = product;
        this.user = user;
    }

}

