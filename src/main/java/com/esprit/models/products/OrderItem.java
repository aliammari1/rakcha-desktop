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

    private Order order;

    private Product product;

    private int quantity;

    private Double unitPrice;

    /**
     * Create a new OrderItem with the specified quantity, product, and order; the
     * `id` remains unset.
     *
     * @param order     the associated Order
     * @param product   the associated Product
     * @param quantity  the number of product units for this item
     * @param unitPrice the price per unit of the product
     */
    public OrderItem(final Order order, final Product product, final int quantity, final Double unitPrice) {
        this.order = order;
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    /**
     * Legacy constructor with float unitPrice.
     *
     * @deprecated Use Double unitPrice constructor instead
     */
    @Deprecated(forRemoval = true)
    public OrderItem(final int quantity, final Product product, final Order order, final float unitPrice) {
        this.quantity = quantity;
        this.product = product;
        this.order = order;
        this.unitPrice = Double.valueOf(unitPrice);
    }

    /**
     * Convenience method to get product name.
     * @return the product name
     */
    public String getProductName() {
        return product != null ? product.getName() : "";
    }

    /**
     * Convenience method to get product ID.
     * @return the product ID
     */
    public Long getProductId() {
        return product != null ? product.getId() : null;
    }

    /**
     * Convenience method to calculate subtotal (quantity * unit price).
     * @return the subtotal for this item
     */
    public Double getSubtotal() {
        return quantity * (unitPrice != null ? unitPrice : 0.0);
    }

}


