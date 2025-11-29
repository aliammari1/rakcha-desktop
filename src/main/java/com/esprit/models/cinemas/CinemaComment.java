package com.esprit.models.cinemas;

import com.esprit.models.users.Client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a comment on a cinema.
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
public class CinemaComment {

    private Long id;

    private Cinema cinema;

    private Client client;

    private String commentText;

    private String sentiment;

    /**
     * Create a new CinemaComment instance without an id.
     *
     * @param cinema the cinema associated with the comment
     * @param client the client who authored the comment
     * @param commentText the text content of the comment
     * @param sentiment the sentiment classification of the comment
     */
    public CinemaComment(final Cinema cinema, final Client client, final String commentText, final String sentiment) {
        this.cinema = cinema;
        this.client = client;
        this.commentText = commentText;
        this.sentiment = sentiment;
    }

}
