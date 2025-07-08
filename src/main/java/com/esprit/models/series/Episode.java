package com.esprit.models.series;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Episode entity class for the RAKCHA application. Represents episode
 * data with Hibernate/JPA annotations for database persistence.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Episode {

    private Long id;

    private String title;

    private int episodeNumber;

    private int season;

    private String image;

    private String video;

    private int seriesId;

    private Series series;

    private List<Feedback> feedbacks;

    /**
     * Constructor without id for creating new episode instances.
     *
     * @param title
     *                      the title of the episode
     * @param episodeNumber
     *                      the episode number
     * @param season
     *                      the season number
     * @param image
     *                      the image URL of the episode
     * @param video
     *                      the video URL of the episode
     * @param seriesId
     *                      the ID of the series
     */
    public Episode(final String title, final int episodeNumber, final int season, final String image,
            final String video, final int seriesId) {
        this.title = title;
        this.episodeNumber = episodeNumber;
        this.season = season;
        this.image = image;
        this.video = video;
        this.seriesId = seriesId;
    }
}
