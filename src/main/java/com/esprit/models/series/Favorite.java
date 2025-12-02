package com.esprit.models.series;

import com.esprit.models.films.Film;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
/**
 * User Favorites entity class for the RAKCHA application. Links users to their favorite films and series.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */

public class Favorite {

    private Long id;

    private Long userId;

    private Long movieId;

    private Long seriesId;

    private Film film;

    private Series series;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * Creates a Favorite that links a user to a series.
     *
     * @param userId   the identifier of the user
     * @param seriesId the identifier of the series
     */
    public Favorite(final Long userId, final Long seriesId) {
        this.userId = userId;
        this.seriesId = seriesId;
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Creates a Favorite that links a user to a movie.
     *
     * @param userId  the identifier of the user
     * @param movieId the identifier of the movie
     */
    public Favorite(final Long userId, final Long movieId, final Film film) {
        this.userId = userId;
        this.movieId = movieId;
        this.film = film;
        this.createdAt = LocalDateTime.now();
    }
}


