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
    public Category(String nom, String description) {
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
    public Category(int id, String nom, String description) {
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
        return nom;
    }
    /**
     * Sets the name of the category.
     *
     * @param nom the name of the category
     */
    public void setNom(String nom) {
        this.nom = nom;
    }
    /**
     * Returns the id of the category.
     *
     * @return the id of the category
     */
    public int getId() {
        return id;
    }
    /**
     * Sets the id of the category.
     *
     * @param id the id of the category
     */
    public void setId(int id) {
        this.id = id;
    }
    /**
     * Returns the description of the category.
     *
     * @return the description of the category
     */
    public String getDescription() {
        return description;
    }
    /**
     * Sets the description of the category.
     *
     * @param description the description of the category
     */
    public void setDescription(String description) {
        this.description = description;
    }
    /**
     * Returns a string representation of the Category object.
     *
     * @return a string representation of the Category object
     */
    @Override
    public String toString() {
        return "Category{" +
                "nom='" + nom + '\'' +
                ", id=" + id +
                ", description='" + description + '\'' +
                '}';
    }
}
