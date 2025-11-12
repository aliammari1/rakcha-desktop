package com.esprit.models.series;

import java.util.Date;

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
public class Feedback {

    private Long id;

    private Long userId;

    private String description;

    private Date date;

    private Long episodeId;

    private Episode episode;

    /**
     * Create a Feedback instance for a given user, episode, and date without specifying an id.
     *
     * @param userId     the id of the user who submitted the feedback
     * @param description the feedback text
     * @param date       the date when the feedback was created
     * @param episodeId  the id of the related episode
     */
    public Feedback(final Long userId, final String description, final Date date, final Long episodeId) {
        this.userId = userId;
        this.description = description;
        this.date = date;
        this.episodeId = episodeId;
    }

}
