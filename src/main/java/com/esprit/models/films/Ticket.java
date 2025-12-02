package com.esprit.models.films;

import com.esprit.enums.TicketStatus;
import com.esprit.models.cinemas.MovieSession;
import com.esprit.models.cinemas.Seat;
import com.esprit.models.users.Client;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The Ticket class represents a ticket for a movie session.
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
/**
 * Entity class for the RAKCHA application. Provides data persistence using
 * Hibernate/JPA annotations.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */

public class Ticket {

    private Long id;

    private Client client;

    private MovieSession movieSession;

    private Seat seat;

    private Long seatId;

    private java.time.LocalDateTime purchaseTime;

    private String qrCode;

    private double pricePaid;

    @Builder.Default
    private TicketStatus status = TicketStatus.CONFIRMED;

    private java.time.LocalDateTime reservedAt;
    private java.time.LocalDateTime expiresAt;
    private java.time.LocalDateTime cancelledAt;
    private String cancellationReason;
    @Builder.Default
    private int transferCount = 0;
    private float refundAmount;

    /**
     * Constructor without id for creating new ticket instances.
     *
     * @param client       The client associated with the ticket.
     * @param movieSession The movie session associated with the ticket.
     * @param seat         The seat for this ticket.
     * @param pricePaid    The price paid for the ticket.
     */
    public Ticket(final Client client, final MovieSession movieSession, final Seat seat,
                  final float pricePaid) {
        this.client = client;
        this.movieSession = movieSession;
        this.seat = seat;
        this.pricePaid = pricePaid;
        this.status = TicketStatus.CONFIRMED;
        this.purchaseTime = java.time.LocalDateTime.now();
    }

    /**
     * Get the ticket purchase date (convenience method).
     * @return the purchase date/time
     */
    public java.time.LocalDateTime getPurchaseDate() {
        return this.purchaseTime;
    }

    /**
     * Set the ticket purchase date (convenience method).
     * @param purchaseDate the purchase date/time to set
     */
    public void setPurchaseDate(java.time.LocalDateTime purchaseDate) {
        this.purchaseTime = purchaseDate;
    }

    /**
     * Get the ticket creation time (alias for purchaseTime).
     * @return the creation date/time
     */
    public java.time.LocalDateTime getCreatedAt() {
        return this.purchaseTime;
    }

    /**
     * Set the ticket creation time (alias for purchaseTime).
     * @param createdAt the creation date/time to set
     */
    public void setCreatedAt(java.time.LocalDateTime createdAt) {
        this.purchaseTime = createdAt;
    }

    /**
     * Get the ticket price (alias for pricePaid).
     * @return the price paid
     */
    public double getPrice() {
        return this.pricePaid;
    }

    /**
     * Set the ticket price (alias for pricePaid).
     * @param price the price to set
     */
    public void setPrice(double price) {
        this.pricePaid = price;
    }

    /**
     * Get the ticket user (alias for client).
     * @return the client associated with the ticket
     */
    public Client getUser() {
        return this.client;
    }

    /**
     * Set the ticket user (alias for client).
     * @param user the client to set
     */
    public void setUser(Client user) {
        this.client = user;
    }

}




