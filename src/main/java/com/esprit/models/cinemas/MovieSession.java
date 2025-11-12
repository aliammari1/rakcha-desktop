package com.esprit.models.cinemas;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.esprit.models.films.Film;
import com.esprit.models.films.Ticket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    private Time startTime;

    private Time endTime;

    private Date sessionDate;

    private Double price;

    @Builder.Default
    private List<Ticket> tickets = new ArrayList<>();

    /**
     * Constructor without id for creating new movie session instances.
     */
    public MovieSession(final CinemaHall cinemaHall, final Film film, final Time startTime, final Time endTime,
            final Date sessionDate, final Double price) {
        this.cinemaHall = cinemaHall;
        this.film = film;
        this.startTime = startTime;
        this.endTime = endTime;
        this.sessionDate = sessionDate;
        this.price = price;
        this.tickets = new ArrayList<>();
    }


    /**
     * Constructor with LocalDate for creating new movie session instances.
     */
    public MovieSession(final CinemaHall cinemaHall, final Film film, final Time startTime, final Time endTime,
            final LocalDate sessionDate, final Double price) {
        this.cinemaHall = cinemaHall;
        this.film = film;
        this.startTime = startTime;
        this.endTime = endTime;
        this.sessionDate = Date.valueOf(sessionDate);
        this.price = price;
        this.tickets = new ArrayList<>();
    }

}

