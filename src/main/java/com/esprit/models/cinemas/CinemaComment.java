package com.esprit.models.cinemas;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import com.esprit.models.users.Client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a comment on a cinema.
 */
@Entity
@Table(name = "cinema_comment")
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
public class CinemaComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cinema_id")
    private Cinema cinema;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private Client client;

    @Column(name = "comment_text")
    private String commentText;

    @Column(name = "sentiment")
    private String sentiment;

    /**
     * Constructor without id for creating new comment instances.
     */
    public CinemaComment(final Cinema cinema, final Client client, final String commentText, final String sentiment) {
        this.cinema = cinema;
        this.client = client;
        this.commentText = commentText;
        this.sentiment = sentiment;
    }
}
