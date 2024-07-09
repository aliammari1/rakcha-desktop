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
    public Actor(int id, String nom, String image, String biographie) {
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
    public Actor(String nom, String image, String biographie) {
        this.nom = nom;
        this.image = image;
        this.biographie = biographie;
    }
    /**
     * Constructs an Actor object with the specified id.
     *
     * @param id the id of the actor
     */
    public Actor(int id) {
        this.id = id;
        this.nom = null;
        this.image = null;
        this.biographie = null;
    }
    /**
     * Constructs an Actor object with the specified id, name, image, biography, and number of appearances.
     *
     * @param id                   the id of the actor
     * @param nom                  the name of the actor
     * @param img                  the image URL of the actor
     * @param s                    the biography of the actor
     * @param numberOfAppearances  the number of appearances of the actor
     */
    public Actor(int id, String nom, String img, String s, int numberOfAppearances) {
        this.id = id;
        this.nom = nom;
        this.biographie = s;
        this.image = img;
        this.numberOfAppearances = numberOfAppearances;
    }
    /**
     * Returns the number of appearances of the actor.
     *
     * @return the number of appearances
     */
    public int getNumberOfAppearances() {
        return numberOfAppearances;
    }
    /**
     * Sets the number of appearances of the actor.
     *
     * @param numberOfAppearances the number of appearances
     */
    public void setNumberOfAppearances(int numberOfAppearances) {
        this.numberOfAppearances = numberOfAppearances;
    }
    /**
     * Returns the id of the actor.
     *
     * @return the id
     */
    public int getId() {
        return id;
    }
    /**
     * Sets the id of the actor.
     *
     * @param id the id
     */
    public void setId(int id) {
        this.id = id;
    }
    /**
     * Returns the name of the actor.
     *
     * @return the name
     */
    public String getNom() {
        return nom;
    }
    /**
     * Sets the name of the actor.
     *
     * @param nom the name
     */
    public void setNom(String nom) {
        this.nom = nom;
    }
    /**
     * Returns the image URL of the actor.
     *
     * @return the image URL
     */
    public String getImage() {
        return image;
    }
    /**
     * Sets the image URL of the actor.
     *
     * @param image the image URL
     */
    public void setImage(String image) {
        this.image = image;
    }
    /**
     * Returns the biography of the actor.
     *
     * @return the biography
     */
    public String getBiographie() {
        return biographie;
    }
    /**
     * Sets the biography of the actor.
     *
     * @param biographie the biography
     */
    public void setBiographie(String biographie) {
        this.biographie = biographie;
    }
    /**
     * Returns a string representation of the Actor object.
     *
     * @return a string representation of the object
     */
    @Override
    public String toString() {
        return "Actor{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", image=" + image +
                ", biographie='" + biographie + '\'' +
                '}';
    }
}
