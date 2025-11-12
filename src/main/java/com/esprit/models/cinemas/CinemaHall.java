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
     * Constructor without id for creating new cinema hall instances.
     * 
     * @param cinema       the cinema this hall belongs to
     * @param seatCapacity the seating capacity of the hall
     * @param name         the name of the hall
     */
    public CinemaHall(final Cinema cinema, final Integer seatCapacity, final String name) {
        this.cinema = cinema;
        this.seatCapacity = seatCapacity;
        this.name = name;
        this.seats = new ArrayList<>();
        this.movieSessions = new ArrayList<>();
    }

}

