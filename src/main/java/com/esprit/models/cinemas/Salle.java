package com.esprit.models.cinemas;
/**
 * Is used to represent a salle within a cinema. It has several fields and methods
 * that allow for the manipulation of these values. The class has four fields: id_salle,
 * id_cinema, nb_places, and nom_salle. Additionally, it has several methods for
 * accessing and modifying these fields, such as getId_salle(), setId_salle(),
 * getId_cinema(), setId_cinema(), getNb_places(), and setNb_places().
 */
public class Salle {
    private int id_salle;
    private int id_cinema;
    private int nb_places;
    private String nom_salle;
    /**
     * Constructs a new Salle object with the specified id_salle, id_cinema, nb_places, and nom_salle.
     *
     * @param id_salle   the id of the salle
     * @param id_cinema  the id of the cinema
     * @param nb_places  the number of places in the salle
     * @param nom_salle  the name of the salle
     */
    public Salle(int id_salle, int id_cinema, int nb_places, String nom_salle) {
        this.id_salle = id_salle;
        this.id_cinema = id_cinema;
        this.nb_places = nb_places;
        this.nom_salle = nom_salle;
    }
    /**
     * Constructs a new Salle object with the specified id_cinema, nb_places, and nom_salle.
     *
     * @param id_cinema  the id of the cinema
     * @param nb_places  the number of places in the salle
     * @param nom_salle  the name of the salle
     */
    public Salle(int id_cinema, int nb_places, String nom_salle) {
        this.id_cinema = id_cinema;
        this.nb_places = nb_places;
        this.nom_salle = nom_salle;
    }
    /**
     * Returns the id of the salle.
     *
     * @return the id of the salle
     */
    public int getId_salle() {
        return id_salle;
    }
    /**
     * Sets the id of the salle.
     *
     * @param id_salle the id of the salle
     */
    public void setId_salle(int id_salle) {
        this.id_salle = id_salle;
    }
    /**
     * Returns the id of the cinema.
     *
     * @return the id of the cinema
     */
    public int getId_cinema() {
        return id_cinema;
    }
    /**
     * Sets the id of the cinema.
     *
     * @param id_cinema the id of the cinema
     */
    public void setId_cinema(int id_cinema) {
        this.id_cinema = id_cinema;
    }
    /**
     * Returns the number of places in the salle.
     *
     * @return the number of places in the salle
     */
    public int getNb_places() {
        return nb_places;
    }
    /**
     * Sets the number of places in the salle.
     *
     * @param nb_places the number of places in the salle
     */
    public void setNb_places(int nb_places) {
        this.nb_places = nb_places;
    }
    /**
     * Returns the name of the salle.
     *
     * @return the name of the salle
     */
    public String getNom_salle() {
        return nom_salle;
    }
    /**
     * Sets the name of the salle.
     *
     * @param nom_salle the name of the salle
     */
    public void setNom_salle(String nom_salle) {
        this.nom_salle = nom_salle;
    }
    /**
     * Returns a string representation of the Salle object.
     *
     * @return a string representation of the Salle object
     */
    @Override
    public String toString() {
        return "Salle{" +
                "id_salle=" + id_salle +
                ", id_cinema=" + id_cinema +
                ", nb_places=" + nb_places +
                ", nom_salle=" + nom_salle +
                '}';
    }
}
