package com.esprit.models.series;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
/**
 * Series management entity class for the RAKCHA application. Represents series
 * data with Hibernate/JPA annotations for database persistence.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class Favorite {

    private Long id;

    private Long userId;

    private Long seriesId;

    private Series series;

    /**
     * Creates a Favorite that links a user to a series and leaves the `id` and `series` fields unset.
     *
     * @param userId  the identifier of the user
     * @param seriesId the identifier of the series
     */
    public Favorite(final Long userId, final Long seriesId) {
        this.userId = userId;
        this.seriesId = seriesId;
    }

}
