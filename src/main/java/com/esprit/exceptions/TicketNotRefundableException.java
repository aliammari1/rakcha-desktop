package com.esprit.exceptions;

/**
 * Exception thrown when a ticket cannot be refunded.
 */
public class TicketNotRefundableException extends RuntimeException {

    public TicketNotRefundableException(String message) {
        super(message);
    }
}
