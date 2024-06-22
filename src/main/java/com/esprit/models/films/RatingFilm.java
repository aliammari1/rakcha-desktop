package com.esprit.models.films;
import com.esprit.models.users.Client;
/**
 * Represents a rating for a film given by a user.
 */
public class RatingFilm {
    public Film id_film;
    public Client id_user;
    public int rate;
    /**
     * Constructs a RatingFilm object with the specified film, user, and rating.
     *
     * @param id_film The film being rated.
     * @param id_user The user who is rating the film.
     * @param rate    The rating given to the film.
     */
    public RatingFilm(Film id_film, Client id_user, int rate) {
        this.id_film = id_film;
        this.id_user = id_user;
        this.rate = rate;
    }
    /**
     * Retrieves the film being rated.
     *
     * @return The film being rated.
     */
    public Film getId_film() {
        return id_film;
    }
    /**
     * Sets the film being rated.
     *
     * @param id_film The film being rated.
     */
    public void setId_film(Film id_film) {
        this.id_film = id_film;
    }
    /**
     * Retrieves the user who is rating the film.
     *
     * @return The user who is rating the film.
     */
    public Client getId_user() {
        return id_user;
    }
    /**
     * Sets the user who is rating the film.
     *
     * @param id_user The user who is rating the film.
     */
    public void setId_user(Client id_user) {
        this.id_user = id_user;
    }
    /**
     * Retrieves the rating given to the film.
     *
     * @return The rating given to the film.
     */
    public int getRate() {
        return rate;
    }
    /**
     * Sets the rating given to the film.
     *
     * @param rate The rating given to the film.
     */
    public void setRate(int rate) {
        this.rate = rate;
    }
    /**
     * Returns a string representation of the RatingFilm object.
     *
     * @return A string representation of the RatingFilm object.
     */
    @Override
    public String toString() {
        return "RatingFilm{" +
                "id_film=" + id_film +
                ", id_user=" + id_user +
                ", rate=" + rate +
                '}';
    }
}
