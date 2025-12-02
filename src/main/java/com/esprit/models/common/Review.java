package com.esprit.models.common;

import com.esprit.models.cinemas.Cinema;
import com.esprit.models.films.Film;
import com.esprit.models.products.Product;
import com.esprit.models.series.Series;
import com.esprit.models.users.Client;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Review {

    private Long id;

    private Client user;

    private Integer rating;

    private String comment;

    private String sentiment;

    private LocalDateTime createdAt;

    private Film film;

    private Series series;

    private Product product;

    private Cinema cinema;

    private String contentType;

    private Long contentId;

    private String contentTitle;

    private String contentImage;

    private Integer helpfulCount;

    private String text;

    /**
     * Create a new Review instance for the given client and film without an id.
     *
     * @param comment the text content of the comment
     * @param user    the client who authored the comment
     * @param film    the film the comment refers to
     */
    public Review(final String comment, final Client user, final Film film) {
        this.comment = comment;
        this.user = user;
        this.film = film;
    }

    /**
     * Create a FilmRating for a given film and client without specifying an id.
     *
     * @param film   the film being rated
     * @param user   the user who gives the rating
     * @param rating the rating value assigned to the film
     */
    public Review(final Film film, final Client user, final int rating) {
        this.film = film;
        this.user = user;
        this.rating = rating;
    }

    /**
     * Create a Comment for a product without an id.
     *
     * @param user    the client who created the comment
     * @param comment the comment text
     * @param product the product the comment refers to
     */
    public Review(final Client user, final String comment, final Product product) {
        this.user = user;
        this.comment = comment;
        this.product = product;
    }

    /**
     * Create a Review for a given client, rating, and product without setting an id.
     *
     * @param user    the client who submitted the review
     * @param rating  the rating value assigned by the client
     * @param product the product being reviewed
     */
    public Review(final Client user, final int rating, final Product product) {
        this.user = user;
        this.rating = rating;
        this.product = product;
    }

    /**
     * Create a Feedback instance for a given user, episode, and date without specifying an id.
     *
     * @param user    the id of the user who submitted the feedback
     * @param comment the feedback text
     * @param series  the id of the related episode
     */
    public Review(final Client user, final Series series, final String comment) {
        this.user = user;
        this.comment = comment;
        this.series = series;
    }

    /**
     * Create a new CinemaRating for the given cinema and client with the specified rating.
     *
     * @param cinema the cinema being rated
     * @param user   the client who provided the rating
     * @param rating the rating value
     */
    public Review(final Cinema cinema, final Client user, final Integer rating) {
        this.cinema = cinema;
        this.user = user;
        this.rating = rating;
    }

    /**
     * Create a new CinemaComment instance without an id.
     *
     * @param cinema    the cinema associated with the comment
     * @param user      the client who authored the comment
     * @param comment   the text content of the comment
     * @param sentiment the sentiment classification of the comment
     */
    public Review(final Cinema cinema, final Client user, final String comment, final String sentiment) {
        this.cinema = cinema;
        this.user = user;
        this.comment = comment;
        this.sentiment = sentiment;
    }

    /**
     * Get the user ID (convenience method).
     * @return the user ID
     */
    public Long getUserId() {
        return this.user != null ? this.user.getId() : null;
    }

    /**
     * Get the user's avatar/profile picture URL (convenience method).
     * @return the user's profile picture URL
     */
    public String getUserAvatar() {
        return this.user != null ? this.user.getProfilePictureUrl() : null;
    }

    /**
     * Get the username (convenience method).
     * @return the username or email
     */
    public String getUsername() {
        return this.user != null ? this.user.getUsername() : "Anonymous";
    }

    /**
     * Get the review title (convenience method).
     * Returns content title or comment preview.
     * @return the title
     */
    public String getTitle() {
        if (this.contentTitle != null && !this.contentTitle.isEmpty()) {
            return this.contentTitle;
        }
        if (this.comment != null && this.comment.length() > 50) {
            return this.comment.substring(0, 50) + "...";
        }
        return this.comment != null ? this.comment : "Review";
    }

    /**
     * Check if the review contains spoilers (convenience method).
     * @return true if review may contain spoilers
     */
    public boolean hasSpoilers() {
        // Check if the comment contains spoiler indicators
        if (this.comment != null) {
            String lower = this.comment.toLowerCase();
            return lower.contains("spoiler") || lower.contains("dies") || lower.contains("twist");
        }
        return false;
    }

    /**
     * Set the user ID (convenience method for client assignment).
     * @param userId the user ID
     */
    public void setUserId(Long userId) {
        // This is a convenience method - would need actual Client object
        // For now, it's a placeholder
        if (this.user != null) {
            this.user.setId(userId);
        }
    }
}





