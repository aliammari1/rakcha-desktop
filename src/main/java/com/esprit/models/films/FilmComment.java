package com.esprit.models.films;

import com.esprit.models.users.Client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a film comment.
 */
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
/**
 * Entity class for the RAKCHA application. Provides data persistence using
 * Hibernate/JPA annotations.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class FilmComment {

    private Long id;

    private String comment;

    private Client client;

    private Film film;

    /**
     * Create a new FilmComment instance for the given client and film without an id.
     *
     * @param comment the text content of the comment
     * @param client  the client who authored the comment
     * @param film    the film the comment refers to
     */
    public FilmComment(final String comment, final Client client, final Film film) {
        this.comment = comment;
        this.client = client;
        this.film = film;
    }

}
