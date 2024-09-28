package com.esprit.models.films;

/**
 * Represents a category of films.
 */
public class Category {
    private String nom;
    private int id;
    private String description;

    /**
     * Constructs a Category object with the given name and description.
     *
     * @param nom         the name of the category
     * @param description the description of the category
     */
    public Category(final String nom, final String description) {
        this.nom = nom;
        this.description = description;
    }

    /**
     * Constructs a Category object with the given id, name, and description.
     *
     * @param id          the id of the category
     * @param nom         the name of the category
     * @param description the description of the category
     */
    public Category(final int id, final String nom, final String description) {
        this.id = id;
        this.nom = nom;
        this.description = description;
    }

    /**
     * Returns the name of the category.
     *
     * @return the name of the category
     */
    public String getNom() {
        return this.nom;
    }

    /**
     * Sets the name of the category.
     *
     * @param nom the name of the category
     */
    public void setNom(final String nom) {
        this.nom = nom;
    }

    /**
     * Returns the id of the category.
     *
     * @return the id of the category
     */
    public int getId() {
        return this.id;
    }

    /**
     * Sets the id of the category.
     *
     * @param id the id of the category
     */
    public void setId(final int id) {
        this.id = id;
    }

    /**
     * Returns the description of the category.
     *
     * @return the description of the category
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Sets the description of the category.
     *
     * @param description the description of the category
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * Returns a string representation of the Category object.
     *
     * @return a string representation of the Category object
     */
    @Override
    public String toString() {
        return "Category{"
                + "nom='" + this.nom + '\''
                + ", id=" + this.id
                + ", description='" + this.description + '\''
                + '}';
    }
}
