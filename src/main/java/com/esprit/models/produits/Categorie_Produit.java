package com.esprit.models.produits;

/**
 * Represents a category of products.
 */
public class Categorie_Produit {
    private int id_categorie;
    private String nom_categorie;
    private String description;

    /**
     * Default constructor.
     */
    public Categorie_Produit() {
    }

    /**
     * Parameterized constructor.
     *
     * @param id_categorie  The ID of the category.
     * @param nom_categorie The name of the category.
     * @param description   The description of the category.
     */
    public Categorie_Produit(final int id_categorie, final String nom_categorie, final String description) {
        this.id_categorie = id_categorie;
        this.nom_categorie = nom_categorie;
        this.description = description;
    }

    /**
     * Parameterized constructor.
     *
     * @param nom_categorie The name of the category.
     * @param description   The description of the category.
     */
    public Categorie_Produit(final String nom_categorie, final String description) {
        this.nom_categorie = nom_categorie;
        this.description = description;
    }

    /**
     * Get the ID of the category.
     *
     * @return The ID of the category.
     */
    public int getId_categorie() {
        return this.id_categorie;
    }

    /**
     * Set the ID of the category.
     *
     * @param id_categorie The ID of the category.
     */
    public void setId_categorie(final int id_categorie) {
        this.id_categorie = id_categorie;
    }

    /**
     * Get the name of the category.
     *
     * @return The name of the category.
     */
    public String getNom_categorie() {
        return this.nom_categorie;
    }

    /**
     * Set the name of the category.
     *
     * @param nom_categorie The name of the category.
     */
    public void setNom_categorie(final String nom_categorie) {
        this.nom_categorie = nom_categorie;
    }

    /**
     * Get the description of the category.
     *
     * @return The description of the category.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Set the description of the category.
     *
     * @param description The description of the category.
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * Get a string representation of the category.
     *
     * @return A string representation of the category.
     */
    @Override
    public String toString() {
        return "Categorie{"
                + "id_categorieProduit=" + this.id_categorie
                + ", nom='" + this.nom_categorie + '\''
                + ", description='" + this.description + '\''
                + '}';
    }
}
