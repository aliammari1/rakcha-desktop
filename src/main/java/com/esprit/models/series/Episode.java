package com.esprit.models.series;

import java.util.List;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "episode")
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
public class Episode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idepisode")
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "episode_number", nullable = false)
    private int episodeNumber;

    @Column(name = "season", nullable = false)
    private int season;

    @Column(name = "image")
    private String image;

    @Column(name = "video")
    private String video;

    @Column(name = "series_id", nullable = false)
    private int seriesId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "series_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Series series;

    @OneToMany(mappedBy = "episode", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
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
