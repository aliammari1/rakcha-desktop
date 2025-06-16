package com.esprit.models.films;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

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
@Entity
@Table(name = "tickets")
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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "number_of_seats", nullable = false)
    private int numberOfSeats;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_session_id", nullable = false)
    private MovieSession movieSession;

    @Column(name = "price", nullable = false)
    private float price;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "ticket_seats", joinColumns = @JoinColumn(name = "ticket_id"), inverseJoinColumns = @JoinColumn(name = "seat_id"))
    @Builder.Default
    private List<Seat> reservedSeats = new ArrayList<>();

    /**
     * Constructor without id for creating new ticket instances.
     *
     * @param numberOfSeats
     *            The number of seats for the ticket.
     * @param client
     *            The client associated with the ticket.
     * @param movieSession
     *            The movie session associated with the ticket.
     * @param price
     *            The price of the ticket.
     */
    public Ticket(final int numberOfSeats, final Client client, final MovieSession movieSession, final float price) {
        this.numberOfSeats = numberOfSeats;
        this.client = client;
        this.movieSession = movieSession;
        this.price = price;
        this.reservedSeats = new ArrayList<>();
    }
}
