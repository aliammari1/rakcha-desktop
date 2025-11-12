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
     * Constructor without id for creating new feedback instances.
     *
     * @param userId
     *                    the user ID
     * @param description
     *                    the feedback description
     * @param date
     *                    the date of feedback
     * @param episodeId
     *                    the episode ID
     */
    public Feedback(final Long userId, final String description, final Date date, final Long episodeId) {
        this.userId = userId;
        this.description = description;
        this.date = date;
        this.episodeId = episodeId;
    }

}

