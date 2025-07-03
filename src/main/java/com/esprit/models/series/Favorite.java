package com.esprit.models.series;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "favorites")
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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "series_id", nullable = false)
    private Long seriesId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "series_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Series series;

    /**
     * Constructor without id for creating new favorite instances.
     *
     * @param userId
     *                 the user ID
     * @param seriesId
     *                 the series ID
     */
    public Favorite(final Long userId, final Long seriesId) {
        this.userId = userId;
        this.seriesId = seriesId;
    }
}
