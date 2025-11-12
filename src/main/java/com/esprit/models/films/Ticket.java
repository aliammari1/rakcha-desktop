package com.esprit.models.films;

import java.util.ArrayList;
import java.util.List;

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

    private int numberOfSeats;

    private Client client;

    private MovieSession movieSession;

    private float price;

    @Builder.Default
    private List<Seat> reservedSeats = new ArrayList<>();

    /**
     * Create a ticket for a client and movie session with the specified number of seats and price, leaving the id unset.
     *
     * @param numberOfSeats the number of seats the ticket covers
     * @param client        the client associated with the ticket
     * @param movieSession  the movie session associated with the ticket
     * @param price         the price of the ticket
     */
    public Ticket(final int numberOfSeats, final Client client, final MovieSession movieSession, final float price) {
        this.numberOfSeats = numberOfSeats;
        this.client = client;
        this.movieSession = movieSession;
        this.price = price;
        this.reservedSeats = new ArrayList<>();
    }

}
