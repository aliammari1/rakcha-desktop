package com.esprit.models.films;

import com.esprit.models.users.Client;

public class Filmcoment {
    private int id;
    private String comment;
    private Client user_id;
    private Film film_id;

    public Filmcoment(int id, String comment, Client user_id, Film film_id) {
        this.id = id;
        this.comment = comment;
        this.user_id = user_id;
        this.film_id = film_id;
    }

    public Filmcoment(String comment, Client user_id, Film film_id) {
        this.comment = comment;
        this.user_id = user_id;
        this.film_id = film_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Client getUser_id() {
        return user_id;
    }

    public void setUser_id(Client user_id) {
        this.user_id = user_id;
    }

    public Film getFilm_id() {
        return film_id;
    }

    public void setFilm_id(Film film_id) {
        this.film_id = film_id;
    }

    @Override
    public String toString() {
        return "Filmcoment{" +
                "id=" + id +
                ", comment='" + comment + '\'' +
                ", user_id=" + user_id +
                ", film_id=" + film_id +
                '}';
    }
}
