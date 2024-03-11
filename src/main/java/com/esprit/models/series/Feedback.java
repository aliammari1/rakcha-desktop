package com.esprit.models.series;

import java.util.Date;

public class Feedback {
    private final int id_episode;
    private int id;
    private int id_user;
    private String description;
    private Date date;


    public Feedback(int id, int id_user, String description, Date date, int id_episode) {
        this.id = id;
        this.id_user = id_user;
        this.description = description;
        this.date = date;
        this.id_episode = id_episode;
    }

    public Feedback(int id_user, String description, Date date, int id_episode) {
        this.id_user = id_user;
        this.description = description;
        this.date = date;
        this.id_episode = id_episode;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_user() {
        return id_user;
    }

    public void setId_user(int id_user) {
        this.id_user = id_user;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getId_episode() {
        return id_episode;
    }
}
