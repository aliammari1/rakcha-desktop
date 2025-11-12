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
     * Constructor without id for creating new comment instances.
     *
     * @param comment
     *                the comment text
     * @param client
     *                the user who made the comment
     * @param film
     *                the film to which the comment belongs
     */
    public FilmComment(final String comment, final Client client, final Film film) {
        this.comment = comment;
        this.client = client;
        this.film = film;
    }

}

