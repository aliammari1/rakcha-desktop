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
     * Create a new Episode populated with title, episode number, season, image, video, and series identifier.
     *
     * The constructor does not initialize `id`, `series`, or `feedbacks`.
     *
     * @param title         the episode title
     * @param episodeNumber the episode number within its season
     * @param season        the season number
     * @param image         the episode image URL or path
     * @param video         the episode video URL or path
     * @param seriesId      the identifier of the series this episode belongs to
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
