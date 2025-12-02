package com.esprit.exceptions;

/**
 * Exception thrown when there is insufficient stock to fulfill an order.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class InsufficientStockException extends Exception {

    private final Long productId;
    private final String productName;
    private final int requestedQuantity;
    private final int availableQuantity;

    /**
     * Constructs a new InsufficientStockException.
     *
     * @param productId         the ID of the product
     * @param productName       the name of the product
     * @param requestedQuantity the quantity requested
     * @param availableQuantity the quantity available
     */
    public InsufficientStockException(Long productId, String productName,
                                      int requestedQuantity, int availableQuantity) {
        super(String.format("Insufficient stock for product '%s' (ID: %d). Requested: %d, Available: %d",
            productName, productId, requestedQuantity, availableQuantity));
        this.productId = productId;
        this.productName = productName;
        this.requestedQuantity = requestedQuantity;
        this.availableQuantity = availableQuantity;
    }

    /**
     * Constructs a new InsufficientStockException with a simple message.
     *
     * @param message the error message
     */
    public InsufficientStockException(String message) {
        super(message);
        this.productId = null;
        this.productName = null;
        this.requestedQuantity = 0;
        this.availableQuantity = 0;
    }

    public Long getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public int getRequestedQuantity() {
        return requestedQuantity;
    }

    public int getAvailableQuantity() {
        return availableQuantity;
    }
}
