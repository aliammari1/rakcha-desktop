package com.esprit.models.series;

import java.util.Date;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "feedback")
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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_user", nullable = false)
    private Long userId;

    @Column(name = "description")
    private String description;

    @Column(name = "date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    @Column(name = "id_episode", nullable = false)
    private Long episodeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_episode", referencedColumnName = "idepisode", insertable = false, updatable = false)
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
