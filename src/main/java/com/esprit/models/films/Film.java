package com.esprit.models.films;

import java.sql.Time;

/**
 * Represents a film.
 */
public class Film {
    private String nom;
    private String categoryNom;
    private int id;
    private String image;
    private Time duree;
    private String description;
    private int annederalisation;

    /**
     * Constructs a new Film object by copying the attributes of another Film object.
     *
     * @param f The Film object to copy.
     */
    public Film(Film f) {
        id = f.id;
        nom = f.nom;
        image = f.image;
        duree = f.duree;
        description = f.description;
        annederalisation = f.annederalisation;
    }

    /**
     * Constructs a new Film object with the specified attributes.
     *
     * @param id               The ID of the film.
     * @param nom              The name of the film.
     * @param image            The image URL of the film.
     * @param duree            The duration of the film.
     * @param description      The description of the film.
     * @param annederalisation The year of release of the film.
     */
    public Film(final int id, final String nom, final String image, final Time duree, final String description, final int annederalisation) {
        this.id = id;
        this.nom = nom;
        this.image = image;
        this.duree = duree;
        this.description = description;
        this.annederalisation = annederalisation;
    }

    /**
     * Constructs a new Film object with the specified attributes.
     *
     * @param nom              The name of the film.
     * @param image            The image URL of the film.
     * @param duree            The duration of the film.
     * @param description      The description of the film.
     * @param annederalisation The year of release of the film.
     */
    public Film(final String nom, final String image, final Time duree, final String description, final int annederalisation) {
        this.nom = nom;
        this.image = image;
        this.duree = duree;
        this.description = description;
        this.annederalisation = annederalisation;
    }

    /**
     * Constructs a new Film object with the specified ID.
     *
     * @param id The ID of the film.
     */
    public Film(final int id) {
        this.id = id;
        nom = null;
        image = null;
        duree = null;
        description = null;
        annederalisation = 0;
    }

    /**
     * Retrieves the name of the film.
     *
     * @return The name of the film.
     */
    public String getNom() {
        return this.nom;
    }

    /**
     * Sets the name of the film.
     *
     * @param nom The name of the film.
     */
    public void setNom(final String nom) {
        this.nom = nom;
    }

    /**
     * Retrieves the ID of the film.
     *
     * @return The ID of the film.
     */
    public int getId() {
        return this.id;
    }

    /**
     * Sets the ID of the film.
     *
     * @param id The ID of the film.
     */
    public void setId(final int id) {
        this.id = id;
    }

    /**
     * Retrieves the image URL of the film.
     *
     * @return The image URL of the film.
     */
    public String getImage() {
        return this.image;
    }

    /**
     * Sets the image URL of the film.
     *
     * @param image The image URL of the film.
     */
    public void setImage(final String image) {
        this.image = image;
    }

    /**
     * Retrieves the duration of the film.
     *
     * @return The duration of the film.
     */
    public Time getDuree() {
        return this.duree;
    }

    /**
     * Sets the duration of the film.
     *
     * @param duree The duration of the film.
     */
    public void setDuree(final Time duree) {
        this.duree = duree;
    }

    /**
     * Sets the category name of the film.
     *
     * @param categoryNom The category name of the film.
     */
    public void setCategoryNom(final String categoryNom) {
        this.categoryNom = categoryNom;
    }

    /**
     * Retrieves the description of the film.
     *
     * @return The description of the film.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Sets the description of the film.
     *
     * @param description The description of the film.
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * Retrieves the year of release of the film.
     *
     * @return The year of release of the film.
     */
    public int getAnnederalisation() {
        return this.annederalisation;
    }

    /**
     * Sets the year of release of the film.
     *
     * @param annederalisation The year of release of the film.
     */
    public void setAnnederalisation(final int annederalisation) {
        this.annederalisation = annederalisation;
    }

    /**
     * Returns a string representation of the Film object.
     *
     * @return A string representation of the Film object.
     */
    @Override
    public String toString() {
        return "Film{"
                + "nom='" + this.nom + '\''
                + ", id=" + this.id
                + ", image=" + this.image
                + ", duree=" + this.duree
                + ", description='" + this.description + '\''
                + ", annederalisation=" + this.annederalisation
                + '}';
    }
}
