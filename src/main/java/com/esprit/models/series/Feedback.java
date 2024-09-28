package com.esprit.models.series;

import java.util.Date;

public class Feedback {
    private final int id_episode;
    private int id;
    private int id_user;
    private String description;
    private Date date;

    public Feedback(final int id, final int id_user, final String description, final Date date, final int id_episode) {
        this.id = id;
        this.id_user = id_user;
        this.description = description;
        this.date = date;
        this.id_episode = id_episode;
    }

    public Feedback(final int id_user, final String description, final Date date, final int id_episode) {
        this.id_user = id_user;
        this.description = description;
        this.date = date;
        this.id_episode = id_episode;
    }

    /**
     * @return int
     */
    public int getId() {
        return this.id;
    }

    /**
     * @param id
     */
    public void setId(final int id) {
        this.id = id;
    }

    public int getId_user() {
        return this.id_user;
    }

    public void setId_user(final int id_user) {
        this.id_user = id_user;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public Date getDate() {
        return this.date;
    }

    public void setDate(final Date date) {
        this.date = date;
    }

    public int getId_episode() {
        return this.id_episode;
    }
}
