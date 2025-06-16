package com.esprit.models.cinemas;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a cinema hall (room) within a cinema.
 */
@Entity
@Table(name = "cinema_hall")
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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cinema_id")
    private Cinema cinema;

    @Column(name = "seat_capacity")
    private Integer seatCapacity;

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "cinemaHall", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Seat> seats = new ArrayList<>();

    @OneToMany(mappedBy = "cinemaHall", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MovieSession> movieSessions = new ArrayList<>();

    /**
     * Constructor without id for creating new cinema hall instances.
     */
    public CinemaHall(final Cinema cinema, final Integer seatCapacity, final String name) {
        this.cinema = cinema;
        this.seatCapacity = seatCapacity;
        this.name = name;
        this.seats = new ArrayList<>();
        this.movieSessions = new ArrayList<>();
    }
}
