package com.esprit.models.series;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

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

    private Long seasonId;

    private int episodeNumber;

    private String title;

    private String imageUrl;

    private String videoUrl;

    private Integer durationMin;

    private LocalDate releaseDate;

    /**
     * Create a new Episode populated with title, episode number, season, image,
     * video, and season identifier.
     *
     * @param seasonId      the identifier of the season this episode belongs to
     * @param episodeNumber the episode number within its season
     * @param title         the episode title
     * @param imageUrl      the episode image URL or path
     * @param videoUrl      the episode video URL or path
     * @param durationMin   the duration in minutes
     * @param releaseDate   the release date of the episode
     */
    public Episode(final Long seasonId, final int episodeNumber, final String title, final String imageUrl,
                   final String videoUrl, final Integer durationMin, final LocalDate releaseDate) {
        this.seasonId = seasonId;
        this.episodeNumber = episodeNumber;
        this.title = title;
        this.imageUrl = imageUrl;
        this.videoUrl = videoUrl;
        this.durationMin = durationMin;
        this.releaseDate = releaseDate;
    }

    /**
     * Legacy constructor with series ID and season number.
     *
     * @deprecated Use seasonId constructor instead
     */
    @Deprecated(forRemoval = true)
    public Episode(final String title, final int episodeNumber, final int season, final String imageUrl,
                   final String videoUrl, final int durationMin, final int seriesId) {
        this.title = title;
        this.episodeNumber = episodeNumber;
        this.imageUrl = imageUrl;
        this.videoUrl = videoUrl;
        this.durationMin = durationMin;
    }

    /**
     * Get the episode thumbnail URL (alias for compatibility).
     * @return the image/thumbnail URL
     */
    public String getThumbnail() {
        return this.imageUrl;
    }

    /**
     * Set the episode thumbnail URL (alias for compatibility).
     * @param thumbnail the thumbnail URL to set
     */
    public void setThumbnail(String thumbnail) {
        this.imageUrl = thumbnail;
    }

    /**
     * Get the episode duration in minutes (alias for compatibility).
     * @return the duration in minutes
     */
    public Integer getDuration() {
        return this.durationMin;
    }

    /**
     * Set the episode duration in minutes (alias for compatibility).
     * @param duration the duration in minutes
     */
    public void setDuration(Integer duration) {
        this.durationMin = duration;
    }

    /**
     * Get the episode description (alias for compatibility).
     * For episodes, the title serves as description.
     * @return the episode title/description
     */
    public String getDescription() {
        return this.title;
    }

    /**
     * Set the episode description (alias for compatibility).
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.title = description;
    }

}




