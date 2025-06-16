package com.esprit.models.cinemas;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

import com.esprit.models.films.Film;
import com.esprit.models.films.Ticket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a movie session in a cinema.
 */
@Entity
@Table(name = "movie_session")
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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cinema_hall_id", nullable = false)
    private CinemaHall cinemaHall;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "film_id", nullable = false)
    private Film film;

    @Column(name = "start_time", nullable = false)
    private Time startTime;

    @Column(name = "end_time", nullable = false)
    private Time endTime;

    @Column(name = "session_date", nullable = false)
    private Date sessionDate;

    @Column(name = "price", nullable = false)
    private Double price;

    @OneToMany(mappedBy = "movieSession", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
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
