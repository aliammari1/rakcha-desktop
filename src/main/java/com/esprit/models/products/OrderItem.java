package com.esprit.models.products;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents an item in an order.
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
public class OrderItem {

    private Long id;

    private int quantity;

    private Product product;

    private Order order;

    /**
     * Create a new OrderItem with the specified quantity, product, and order; the `id` remains unset.
     *
     * @param quantity the number of product units for this item
     * @param product  the associated Product
     * @param order    the associated Order
     */
    public OrderItem(final int quantity, final Product product, final Order order) {
        this.quantity = quantity;
        this.product = product;
        this.order = order;
    }

}
