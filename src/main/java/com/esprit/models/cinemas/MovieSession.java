package com.esprit.models.cinemas;

import com.esprit.models.films.Film;
import com.esprit.models.films.Ticket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a movie session in a cinema.
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
/**
 * Cinema management entity class for the RAKCHA application. Handles
 * cinema-related data with database persistence capabilities.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class MovieSession {

    private Long id;

    private CinemaHall cinemaHall;

    private Film film;

    private java.time.LocalDateTime startTime;

    private java.time.LocalDateTime endTime;

    private Double price;

    @Builder.Default
    private List<Ticket> tickets = new ArrayList<>();

    /**
     * Constructor without id for creating new movie session instances.
     */
    public MovieSession(final CinemaHall cinemaHall, final Film film, final java.time.LocalDateTime startTime, final java.time.LocalDateTime endTime,
                        final Double price) {
        this.cinemaHall = cinemaHall;
        this.film = film;
        this.startTime = startTime;
        this.endTime = endTime;
        this.price = price;
        this.tickets = new ArrayList<>();
    }

    /**
     * Legacy constructor using Date and Time objects.
     *
     * @deprecated Use LocalDateTime constructor instead
     */
    @Deprecated(forRemoval = true)
    public MovieSession(final CinemaHall cinemaHall, final Film film, final java.sql.Time startTime, final java.sql.Time endTime,
                        final java.sql.Date sessionDate, final Double price) {
        this.cinemaHall = cinemaHall;
        this.film = film;
        this.startTime = startTime != null ? startTime.toLocalTime().atDate(sessionDate.toLocalDate()) : null;
        this.endTime = endTime != null ? endTime.toLocalTime().atDate(sessionDate.toLocalDate()) : null;
        this.price = price;
        this.tickets = new ArrayList<>();
    }

    /**
     * Legacy constructor using Time objects and LocalDate.
     *
     * @deprecated Use LocalDateTime constructor instead
     */
    @Deprecated(forRemoval = true)
    public MovieSession(final CinemaHall cinemaHall, final Film film, final java.sql.Time startTime, final java.sql.Time endTime,
                        final LocalDate sessionDate, final Double price) {
        this.cinemaHall = cinemaHall;
        this.film = film;
        this.startTime = startTime != null ? startTime.toLocalTime().atDate(sessionDate) : null;
        this.endTime = endTime != null ? endTime.toLocalTime().atDate(sessionDate) : null;
        this.price = price;
        this.tickets = new ArrayList<>();
    }

    /**
     * Get the base price of the movie session.
     *
     * @return the price
     */
    public Double getBasePrice() {
        return this.price;
    }

    /**
     * Set the base price of the movie session.
     *
     * @param basePrice the price to set
     */
    public void setBasePrice(Double basePrice) {
        this.price = basePrice;
    }

}

