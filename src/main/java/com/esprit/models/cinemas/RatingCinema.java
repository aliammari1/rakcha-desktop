package com.esprit.models.cinemas;

import com.esprit.models.users.Client;

/**
 * Represents a rating for a cinema given by a user.
 */
public class RatingCinema {
    public Cinema id_cinema;
    public Client id_user;
    public int rate;

    /**
     * Constructs a new RatingCinema object with the specified cinema, user, and rating.
     *
     * @param id_cinema The cinema being rated.
     * @param id_user   The user who is giving the rating.
     * @param rate      The rating given by the user.
     */
    public RatingCinema(final Cinema id_cinema, final Client id_user, final int rate) {
        this.id_cinema = id_cinema;
        this.id_user = id_user;
        this.rate = rate;
    }

    /**
     * Gets the cinema being rated.
     *
     * @return The cinema being rated.
     */
    public Cinema getId_cinema() {
        return this.id_cinema;
    }

    /**
     * Sets the cinema being rated.
     *
     * @param id_cinema The cinema being rated.
     */
    public void setId_cinema(final Cinema id_cinema) {
        this.id_cinema = id_cinema;
    }

    /**
     * Gets the user who is giving the rating.
     *
     * @return The user who is giving the rating.
     */
    public Client getId_user() {
        return this.id_user;
    }

    /**
     * Sets the user who is giving the rating.
     *
     * @param id_user The user who is giving the rating.
     */
    public void setId_user(final Client id_user) {
        this.id_user = id_user;
    }

    /**
     * Gets the rating given by the user.
     *
     * @return The rating given by the user.
     */
    public int getRate() {
        return this.rate;
    }

    /**
     * Sets the rating given by the user.
     *
     * @param rate The rating given by the user.
     */
    public void setRate(final int rate) {
        this.rate = rate;
    }

    /**
     * Returns a string representation of the RatingCinema object.
     *
     * @return A string representation of the RatingCinema object.
     */
    @Override
    public String toString() {
        return "RatingCinema{"
                + "id_cinema=" + this.id_cinema
                + ", id_user=" + this.id_user
                + ", rate=" + this.rate
                + '}';
    }
}
