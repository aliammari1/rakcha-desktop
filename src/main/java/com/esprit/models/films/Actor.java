package com.esprit.models.films;

/**
 * The Actor class represents an actor in a film.
 */
public class Actor {
    private String biographie;
    private int id;
    private String nom;
    private String image;
    private int numberOfAppearances;

    /**
     * Constructs an Actor object with the specified id, name, image, and biography.
     *
     * @param id         the id of the actor
     * @param nom        the name of the actor
     * @param image      the image URL of the actor
     * @param biographie the biography of the actor
     */
    public Actor(final int id, final String nom, final String image, final String biographie) {
        this.id = id;
        this.nom = nom;
        this.image = image;
        this.biographie = biographie;
    }

    /**
     * Constructs an Actor object with the specified name, image, and biography.
     *
     * @param nom        the name of the actor
     * @param image      the image URL of the actor
     * @param biographie the biography of the actor
     */
    public Actor(final String nom, final String image, final String biographie) {
        this.nom = nom;
        this.image = image;
        this.biographie = biographie;
    }

    /**
     * Constructs an Actor object with the specified id.
     *
     * @param id the id of the actor
     */
    public Actor(final int id) {
        this.id = id;
        nom = null;
        image = null;
        biographie = null;
    }

    /**
     * Constructs an Actor object with the specified id, name, image, biography, and number of appearances.
     *
     * @param id                  the id of the actor
     * @param nom                 the name of the actor
     * @param img                 the image URL of the actor
     * @param s                   the biography of the actor
     * @param numberOfAppearances the number of appearances of the actor
     */
    public Actor(final int id, final String nom, final String img, final String s, final int numberOfAppearances) {
        this.id = id;
        this.nom = nom;
        biographie = s;
        image = img;
        this.numberOfAppearances = numberOfAppearances;
    }

    /**
     * Returns the number of appearances of the actor.
     *
     * @return the number of appearances
     */
    public int getNumberOfAppearances() {
        return this.numberOfAppearances;
    }

    /**
     * Sets the number of appearances of the actor.
     *
     * @param numberOfAppearances the number of appearances
     */
    public void setNumberOfAppearances(final int numberOfAppearances) {
        this.numberOfAppearances = numberOfAppearances;
    }

    /**
     * Returns the id of the actor.
     *
     * @return the id
     */
    public int getId() {
        return this.id;
    }

    /**
     * Sets the id of the actor.
     *
     * @param id the id
     */
    public void setId(final int id) {
        this.id = id;
    }

    /**
     * Returns the name of the actor.
     *
     * @return the name
     */
    public String getNom() {
        return this.nom;
    }

    /**
     * Sets the name of the actor.
     *
     * @param nom the name
     */
    public void setNom(final String nom) {
        this.nom = nom;
    }

    /**
     * Returns the image URL of the actor.
     *
     * @return the image URL
     */
    public String getImage() {
        return this.image;
    }

    /**
     * Sets the image URL of the actor.
     *
     * @param image the image URL
     */
    public void setImage(final String image) {
        this.image = image;
    }

    /**
     * Returns the biography of the actor.
     *
     * @return the biography
     */
    public String getBiographie() {
        return this.biographie;
    }

    /**
     * Sets the biography of the actor.
     *
     * @param biographie the biography
     */
    public void setBiographie(final String biographie) {
        this.biographie = biographie;
    }

    /**
     * Returns a string representation of the Actor object.
     *
     * @return a string representation of the object
     */
    @Override
    public String toString() {
        return "Actor{"
                + "id=" + this.id
                + ", nom='" + this.nom + '\''
                + ", image=" + this.image
                + ", biographie='" + this.biographie + '\''
                + '}';
    }
}
