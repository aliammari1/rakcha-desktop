package com.esprit.models.cinemas;

import jakarta.persistence.*;

import com.esprit.models.users.Client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a rating for a cinema given by a user.
 */
@Entity
@Table(name = "cinema_rating")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
/**
 * Cinema management entity class for the RAKCHA application. Handles
 * cinema-related data with database persistence capabilities.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class CinemaRating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cinema_id")
    private Cinema cinema;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private Client client;

    @Column(name = "rating")
    private Integer rating;

    /**
     * Constructor without id for creating new rating instances.
     */
    public CinemaRating(final Cinema cinema, final Client client, final Integer rating) {
        this.cinema = cinema;
        this.client = client;
        this.rating = rating;
    }
}
