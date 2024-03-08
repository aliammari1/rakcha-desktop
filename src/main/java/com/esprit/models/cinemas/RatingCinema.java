package com.esprit.models.cinemas;

import com.esprit.models.users.Client;

public class RatingCinema {
    public Cinema id_cinema;
    public Client id_user;
    public int rate;

    public RatingCinema(Cinema id_cinema, Client id_user, int rate) {
        this.id_cinema = id_cinema;
        this.id_user = id_user;
        this.rate = rate;
    }

    public Cinema getId_cinema() {
        return id_cinema;
    }

    public void setId_cinema(Cinema id_cinema) {
        this.id_cinema = id_cinema;
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
        return "RatingCinema{" +
                "id_cinema=" + id_cinema +
                ", id_user=" + id_user +
                ", rate=" + rate +
                '}';
    }
}

