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
     * Constructor without id for creating new order item instances.
     *
     * @param quantity
     *                 The quantity of the item.
     * @param product
     *                 The product associated with the item.
     * @param order
     *                 The order associated with the item.
     */
    public OrderItem(final int quantity, final Product product, final Order order) {
        this.quantity = quantity;
        this.product = product;
        this.order = order;
    }

}

