package com.esprit.models.series;

public class Favoris {
    private int id;
    private int id_user;
    private int id_serie;

    public Favoris() {
    }

    public Favoris(final int id, final int id_user, final int id_serie) {
        this.id = id;
        this.id_user = id_user;
        this.id_serie = id_serie;
    }

    public Favoris(final int id_user, final int id_serie) {
        this.id_user = id_user;
        this.id_serie = id_serie;
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

    public int getId_serie() {
        return this.id_serie;
    }

    public void setId_serie(final int id_serie) {
        this.id_serie = id_serie;
    }
}
