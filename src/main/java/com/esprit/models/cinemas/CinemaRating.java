package com.esprit.models.cinemas;

import com.esprit.models.users.Client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a rating for a cinema given by a user.
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
public class CinemaRating {

    private Long id;

    private Cinema cinema;

    private Client client;

    private Integer rating;

    /**
     * Constructor without id for creating new rating instances.
     * 
     * @param cinema the cinema being rated
     * @param client the client giving the rating
     * @param rating the rating value
     */
    public CinemaRating(final Cinema cinema, final Client client, final Integer rating) {
        this.cinema = cinema;
        this.client = client;
        this.rating = rating;
    }

}

