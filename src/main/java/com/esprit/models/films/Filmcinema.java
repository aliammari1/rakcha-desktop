package com.esprit.models.films;

import com.esprit.models.cinemas.Cinema;

public class Filmcinema {
    private Film id_film;
    private Cinema id_cinema;

    public Filmcinema(Film id_film, Cinema id_cinema) {
        this.id_film = id_film;
        this.id_cinema = id_cinema;
    }

    public Film getId_film() {
        return id_film;
    }

    public void setId_film(Film id_film) {
        this.id_film = id_film;
    }

    public Cinema getId_cinema() {
        return id_cinema;
    }

    public void setId_cinema(Cinema id_cinema) {
        this.id_cinema = id_cinema;
    }

    @Override
    public String toString() {
        return "Cinemafilm{" +
                "id_film=" + id_film +
                ", id_cinema=" + id_cinema +
                '}';
    }
}
