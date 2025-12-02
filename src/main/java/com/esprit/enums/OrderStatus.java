package com.esprit.enums;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Enum representing the possible statuses of an order with state machine
 * transitions.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
public enum OrderStatus {

    PENDING("pending", "Pending Payment"),
    PAID("paid", "Payment Confirmed"),
    PROCESSING("processing", "Processing Order"),
    SHIPPED("shipped", "Shipped"),
    DELIVERED("delivered", "Delivered"),
    CANCELLED("cancelled", "Cancelled");

    private final String value;
    private final String displayName;

    OrderStatus(String value, String displayName) {
        this.value = value;
        this.displayName = displayName;
    }

    /**
     * Converts a string value to OrderStatus enum.
     *
     * @param value the status value
     * @return the corresponding OrderStatus, or PENDING if not found
     */
    public static OrderStatus fromValue(String value) {
        if (value == null) {
            return PENDING;
        }

        for (OrderStatus status : OrderStatus.values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }

        // Default to PENDING if unknown value
        return PENDING;
    }

    /**
     * Gets the database value of this status.
     *
     * @return the status value
     */
    public String getValue() {
        return value;
    }

    /**
     * Gets the user-friendly display name.
     *
     * @return the display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Checks if a transition to the new status is valid.
     *
     * @param newStatus the target status
     * @return true if transition is allowed, false otherwise
     */
    public boolean canTransitionTo(OrderStatus newStatus) {
        if (this == newStatus) {
            return true; // Same status is always allowed
        }

        Set<OrderStatus> allowedTransitions = getAllowedTransitions();
        return allowedTransitions.contains(newStatus);
    }

    /**
     * Gets the set of allowed transitions from this status.
     *
     * @return set of allowed target statuses
     */
    public Set<OrderStatus> getAllowedTransitions() {
        switch (this) {
            case PENDING:
                return new HashSet<>(Arrays.asList(PAID, CANCELLED));
            case PAID:
                return new HashSet<>(Arrays.asList(PROCESSING, CANCELLED));
            case PROCESSING:
                return new HashSet<>(Arrays.asList(SHIPPED, CANCELLED));
            case SHIPPED:
                return new HashSet<>(Arrays.asList(DELIVERED));
            case DELIVERED:
            case CANCELLED:
                return new HashSet<>(); // Final states - no transitions allowed
            default:
                return new HashSet<>();
        }
    }

    /**
     * Checks if this status is a final state (no further transitions possible).
     *
     * @return true if final state, false otherwise
     */
    public boolean isFinalState() {
        return this == DELIVERED || this == CANCELLED;
    }

    /**
     * Checks if this status requires stock to be deducted.
     * Stock is deducted when order transitions TO paid status.
     *
     * @return true if stock should be deducted, false otherwise
     */
    public boolean requiresStockDeduction() {
        return this == PAID;
    }

    @Override
    public String toString() {
        return value;
    }
}
