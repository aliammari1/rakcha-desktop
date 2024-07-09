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
    public Categorie_Produit(){ }
    /**
     * Parameterized constructor.
     * @param id_categorie The ID of the category.
     * @param nom_categorie The name of the category.
     * @param description The description of the category.
     */
    public Categorie_Produit(int id_categorie, String nom_categorie, String description) {
        this.id_categorie = id_categorie;
        this.nom_categorie = nom_categorie;
        this.description = description;
    }
    /**
     * Parameterized constructor.
     * @param nom_categorie The name of the category.
     * @param description The description of the category.
     */
    public Categorie_Produit(String nom_categorie, String description) {
        this.nom_categorie = nom_categorie;
        this.description = description;
    }
    /**
     * Get the ID of the category.
     * @return The ID of the category.
     */
    public int getId_categorie() {
        return id_categorie;
    }
    /**
     * Set the ID of the category.
     * @param id_categorie The ID of the category.
     */
    public void setId_categorie(int id_categorie) {
        this.id_categorie = id_categorie;
    }
    /**
     * Get the name of the category.
     * @return The name of the category.
     */
    public String getNom_categorie() {
        return nom_categorie;
    }
    /**
     * Set the name of the category.
     * @param nom_categorie The name of the category.
     */
    public void setNom_categorie(String nom_categorie) {
        this.nom_categorie = nom_categorie;
    }
    /**
     * Get the description of the category.
     * @return The description of the category.
     */
    public String getDescription() {
        return description;
    }
    /**
     * Set the description of the category.
     * @param description The description of the category.
     */
    public void setDescription(String description) {
        this.description = description;
    }
    /**
     * Get a string representation of the category.
     * @return A string representation of the category.
     */
    @Override
    public String toString() {
        return "Categorie{" +
                "id_categorieProduit=" + id_categorie +
                ", nom='" + nom_categorie + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
