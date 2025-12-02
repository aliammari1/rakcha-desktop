package com.esprit.exceptions;

import com.esprit.enums.OrderStatus;

/**
 * Exception thrown when an invalid order status transition is attempted.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class InvalidStatusTransitionException extends Exception {

    private final OrderStatus currentStatus;
    private final OrderStatus requestedStatus;

    /**
     * Constructs a new InvalidStatusTransitionException.
     *
     * @param currentStatus   the current order status
     * @param requestedStatus the requested new status
     */
    public InvalidStatusTransitionException(OrderStatus currentStatus, OrderStatus requestedStatus) {
        super(String.format("Cannot transition from %s to %s. Invalid status transition.",
            currentStatus.getDisplayName(), requestedStatus.getDisplayName()));
        this.currentStatus = currentStatus;
        this.requestedStatus = requestedStatus;
    }

    /**
     * Constructs a new InvalidStatusTransitionException with a custom message.
     *
     * @param message the error message
     */
    public InvalidStatusTransitionException(String message) {
        super(message);
        this.currentStatus = null;
        this.requestedStatus = null;
    }

    public OrderStatus getCurrentStatus() {
        return currentStatus;
    }

    public OrderStatus getRequestedStatus() {
        return requestedStatus;
    }
}
