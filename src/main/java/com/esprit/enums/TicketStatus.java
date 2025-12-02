package com.esprit.enums;

public enum TicketStatus {
    PENDING, // Awaiting payment confirmation
    CONFIRMED, // Paid and confirmed
    EXPIRED, // Reservation timeout
    CANCELLED, // User cancelled
    REFUNDED // Cancelled with refund processed
}
