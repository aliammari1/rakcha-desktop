package com.esprit.models.films;

import com.esprit.models.users.Client;

public class RatingFilm {
    public Film id_film;
    public Client id_user;
    public int rate;

    public RatingFilm(Film id_film, Client id_user, int rate) {
        this.id_film = id_film;
        this.id_user = id_user;
        this.rate = rate;
    }

    public Film getId_film() {
        return id_film;
    }

    public void setId_film(Film id_film) {
        this.id_film = id_film;
    }

    public Client getId_user() {
        return id_user;
    }

    public void setId_user(Client id_user) {
        this.id_user = id_user;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    @Override
    public String toString() {
        return "RatingFilm{" +
                "id_film=" + id_film +
                ", id_user=" + id_user +
                ", rate=" + rate +
                '}';
    }
}
