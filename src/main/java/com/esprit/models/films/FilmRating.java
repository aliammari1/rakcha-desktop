package com.esprit.models.films;

import com.esprit.models.users.Client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a rating for a film given by a user.
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
public class FilmRating {

    private Long id;

    private Film film;

    private Client client;

    private int rating;

    /**
     * Constructor without id for creating new rating instances.
     *
     * @param film
     *               The film being rated.
     * @param client
     *               The user who is rating the film.
     * @param rating
     *               The rating given to the film.
     */
    public FilmRating(final Film film, final Client client, final int rating) {
        this.film = film;
        this.client = client;
        this.rating = rating;
    }

}

