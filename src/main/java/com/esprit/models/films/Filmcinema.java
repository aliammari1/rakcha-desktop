package com.esprit.models.films;

import com.esprit.models.cinemas.Cinema;

/**
 * Represents a film and cinema combination.
 */
public class Filmcinema {
    private Film id_film;
    private Cinema id_cinema;

    /**
     * Constructs a new Filmcinema object with the specified film and cinema.
     *
     * @param id_film   The film associated with the cinema.
     * @param id_cinema The cinema associated with the film.
     */
    public Filmcinema(final Film id_film, final Cinema id_cinema) {
        this.id_film = id_film;
        this.id_cinema = id_cinema;
    }

    /**
     * Returns the film associated with the cinema.
     *
     * @return The film associated with the cinema.
     */
    public Film getId_film() {
        return this.id_film;
    }

    /**
     * Sets the film associated with the cinema.
     *
     * @param id_film The film to be associated with the cinema.
     */
    public void setId_film(final Film id_film) {
        this.id_film = id_film;
    }

    /**
     * Returns the cinema associated with the film.
     *
     * @return The cinema associated with the film.
     */
    public Cinema getId_cinema() {
        return this.id_cinema;
    }

    /**
     * Sets the cinema associated with the film.
     *
     * @param id_cinema The cinema to be associated with the film.
     */
    public void setId_cinema(final Cinema id_cinema) {
        this.id_cinema = id_cinema;
    }

    /**
     * Returns the film.
     *
     * @return The film.
     */
    public Film getFilm() {
        return this.id_film;
    }

    /**
     * Returns a string representation of the Filmcinema object.
     *
     * @return A string representation of the Filmcinema object.
     */
    @Override
    public String toString() {
        return "Filmcinema{"
                + "id_film=" + this.id_film
                + ", id_cinema=" + this.id_cinema
                + '}';
    }
}
