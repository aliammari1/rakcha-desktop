package com.esprit.models.cinemas;

import com.esprit.models.users.Responsable_de_cinema;

/**
 * Represents a cinema.
 */
public class Cinema {
    private final Responsable_de_cinema responsable;
    private int id_cinema;
    private String nom;
    private String adresse;
    private String logo;
    private String Statut;

    /**
     * Constructs a Cinema object with the specified parameters.
     *
     * @param id_cinema   the ID of the cinema
     * @param nom         the name of the cinema
     * @param adresse     the address of the cinema
     * @param responsable the responsible person for the cinema
     * @param logo        the logo of the cinema
     * @param statut      the status of the cinema
     */
    public Cinema(final int id_cinema, final String nom, final String adresse, final Responsable_de_cinema responsable, final String logo, final String statut) {
        this.id_cinema = id_cinema;
        this.nom = nom;
        this.adresse = adresse;
        this.responsable = responsable;
        this.logo = logo;
        this.Statut = statut;
    }

    /**
     * Constructs a Cinema object with the specified parameters.
     *
     * @param nom         the name of the cinema
     * @param adresse     the address of the cinema
     * @param responsable the responsible person for the cinema
     * @param logo        the logo of the cinema
     * @param statut      the status of the cinema
     */
    public Cinema(final String nom, final String adresse, final Responsable_de_cinema responsable, final String logo, final String statut) {
        this.nom = nom;
        this.adresse = adresse;
        this.responsable = responsable;
        this.logo = logo;
        this.Statut = statut;
    }

    /**
     * Returns the ID of the cinema.
     *
     * @return the ID of the cinema
     */
    public int getId_cinema() {
        return this.id_cinema;
    }

    /**
     * Sets the ID of the cinema.
     *
     * @param id_cinema the ID of the cinema
     */
    public void setId_cinema(final int id_cinema) {
        this.id_cinema = id_cinema;
    }

    /**
     * Returns the name of the cinema.
     *
     * @return the name of the cinema
     */
    public String getNom() {
        return this.nom;
    }

    /**
     * Sets the name of the cinema.
     *
     * @param nom the name of the cinema
     */
    public void setNom(final String nom) {
        this.nom = nom;
    }

    /**
     * Returns the address of the cinema.
     *
     * @return the address of the cinema
     */
    public String getAdresse() {
        return this.adresse;
    }

    /**
     * Sets the address of the cinema.
     *
     * @param adresse the address of the cinema
     */
    public void setAdresse(final String adresse) {
        this.adresse = adresse;
    }

    /**
     * Returns the responsible person for the cinema.
     *
     * @return the responsible person for the cinema
     */
    public Responsable_de_cinema getResponsable() {
        return this.responsable;
    }

    /**
     * Returns the logo of the cinema.
     *
     * @return the logo of the cinema
     */
    public String getLogo() {
        return this.logo;
    }

    /**
     * Sets the logo of the cinema.
     *
     * @param logo the logo of the cinema
     */
    public void setLogo(final String logo) {
        this.logo = logo;
    }

    /**
     * Returns the status of the cinema.
     *
     * @return the status of the cinema
     */
    public String getStatut() {
        return this.Statut;
    }

    /**
     * Sets the status of the cinema.
     *
     * @param statut the status of the cinema
     */
    public void setStatut(final String statut) {
        this.Statut = statut;
    }

    /**
     * Returns a string representation of the Cinema object.
     *
     * @return a string representation of the Cinema object
     */
    @Override
    public String toString() {
        return "Cinema{"
                + "id_cinema=" + this.id_cinema
                + ", nom='" + this.nom + '\''
                + ", adresse='" + this.adresse + '\''
                + ", responsable='" + this.responsable + '\''
                + ", logo=" + this.logo
                + ", Statut='" + this.Statut + '\''
                + '}';
    }
}
