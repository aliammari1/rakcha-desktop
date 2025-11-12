package com.esprit.models.cinemas;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a cinema hall (room) within a cinema.
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
public class CinemaHall {

    private Long id;

    private Cinema cinema;

    private Integer seatCapacity;

    private String name;

    @Builder.Default
    private List<Seat> seats = new ArrayList<>();

    @Builder.Default
    private List<MovieSession> movieSessions = new ArrayList<>();

    /**
     * Create a new cinema hall associated with the given cinema, with the specified seat capacity and name.
     *
     * @param cinema the parent Cinema for this hall
     * @param seatCapacity the number of seats the hall can contain
     * @param name the human-readable name of the hall
     */
    public CinemaHall(final Cinema cinema, final Integer seatCapacity, final String name) {
        this.cinema = cinema;
        this.seatCapacity = seatCapacity;
        this.name = name;
        this.seats = new ArrayList<>();
        this.movieSessions = new ArrayList<>();
    }

}
