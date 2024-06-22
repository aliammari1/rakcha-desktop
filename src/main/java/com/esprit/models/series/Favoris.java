package com.esprit.models.series;
public class Favoris {
    private int id;
    private int id_user;
    private int id_serie;
    public Favoris() {
    }
    public Favoris(int id, int id_user, int id_serie) {
        this.id = id;
        this.id_user = id_user;
        this.id_serie = id_serie;
    }
    public Favoris(int id_user, int id_serie) {
        this.id_user = id_user;
        this.id_serie = id_serie;
    }
    /** 
     * @return int
     */
    public int getId() {
        return id;
    }
    /** 
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }
    public int getId_user() {
        return id_user;
    }
    public void setId_user(int id_user) {
        this.id_user = id_user;
    }
    public int getId_serie() {
        return id_serie;
    }
    public void setId_serie(int id_serie) {
        this.id_serie = id_serie;
    }
}
