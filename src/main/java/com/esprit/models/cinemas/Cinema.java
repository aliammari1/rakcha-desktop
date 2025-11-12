package com.esprit.models.cinemas;

import java.util.ArrayList;
import java.util.List;

import com.esprit.models.users.CinemaManager;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a cinema.
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
public class Cinema {

    private Long id;

    private String name;

    private String address;

    private String logoPath;

    private String status;

    private CinemaManager manager;

    @Builder.Default
    private List<CinemaHall> cinemaHalls = new ArrayList<>();

    @Builder.Default
    private List<CinemaRating> ratings = new ArrayList<>();

    @Builder.Default
    private List<CinemaComment> comments = new ArrayList<>();

    /**
     * Constructor without id for creating new cinema instances.
     *
     * @param name     the name of the cinema
     * @param address  the address of the cinema
     * @param manager  the cinema manager
     * @param logoPath the path to the cinema's logo
     * @param status   the current status of the cinema
     */
    public Cinema(final String name, final String address, final CinemaManager manager, final String logoPath,
            final String status) {
        this.name = name;
        this.address = address;
        this.manager = manager;
        this.logoPath = logoPath;
        this.status = status;
        this.cinemaHalls = new ArrayList<>();
        this.ratings = new ArrayList<>();
        this.comments = new ArrayList<>();
    }


    /**
     * Get all movie sessions for this cinema across all halls.
     *
     * @return a list of all movie sessions in this cinema
     */
    public List<MovieSession> getMovieSessions() {
        List<MovieSession> allSessions = new ArrayList<>();
        if (cinemaHalls != null) {
            for (CinemaHall hall : cinemaHalls) {
                if (hall.getMovieSessions() != null) {
                    allSessions.addAll(hall.getMovieSessions());
                }

            }

        }

        return allSessions;
    }

}

