package com.esprit.models.produits;

/**
 * The `Produit` class represents a product.
 */
public class Produit {
    private int id_produit;
    private String nom;
    private int prix;
    private String image;
    private String description;
    private Categorie_Produit categorieProduit;
    private int quantiteP;

    /**
     * Constructs a `Produit` object with the specified parameters.
     *
     * @param id_produit       the ID of the product
     * @param nom              the name of the product
     * @param prix             the price of the product
     * @param image            the image URL of the product
     * @param description      the description of the product
     * @param categorieProduit the category of the product
     * @param quantiteP        the quantity of the product
     */
    public Produit(final int id_produit, final String nom, final int prix, final String image, final String description, final Categorie_Produit categorieProduit, final int quantiteP) {
        this.id_produit = id_produit;
        this.nom = nom;
        this.prix = prix;
        this.image = image;
        this.description = description;
        this.categorieProduit = categorieProduit;
        this.quantiteP = quantiteP;
    }

    /**
     * Constructs a `Produit` object with the specified parameters.
     *
     * @param nom              the name of the product
     * @param prix             the price of the product
     * @param image            the image URL of the product
     * @param description      the description of the product
     * @param categorieProduit the category of the product
     * @param quantiteP        the quantity of the product
     */
    public Produit(final String nom, final int prix, final String image, final String description, final Categorie_Produit categorieProduit, final int quantiteP) {
        this.nom = nom;
        this.prix = prix;
        this.image = image;
        this.description = description;
        this.categorieProduit = categorieProduit;
        this.quantiteP = quantiteP;
    }

    /**
     * Constructs a `Produit` object with the specified ID.
     *
     * @param id_produit the ID of the product
     */
    public Produit(final int id_produit) {
        this.id_produit = id_produit;
    }

    /**
     * Constructs an empty `Produit` object.
     */
    public Produit() {
    }

    /**
     * Returns the ID of the product.
     *
     * @return the ID of the product
     */
    public int getId_produit() {
        return this.id_produit;
    }

    /**
     * Sets the ID of the product.
     *
     * @param id_produit the ID of the product
     */
    public void setId_produit(final int id_produit) {
        this.id_produit = id_produit;
    }

    /**
     * Returns the name of the product.
     *
     * @return the name of the product
     */
    public String getNom() {
        return this.nom;
    }

    /**
     * Sets the name of the product.
     *
     * @param nom the name of the product
     */
    public void setNom(final String nom) {
        this.nom = nom;
    }

    /**
     * Returns the price of the product.
     *
     * @return the price of the product
     */
    public int getPrix() {
        return this.prix;
    }

    /**
     * Sets the price of the product.
     *
     * @param prix the price of the product
     */
    public void setPrix(final int prix) {
        this.prix = prix;
    }

    /**
     * Returns the image URL of the product.
     *
     * @return the image URL of the product
     */
    public String getImage() {
        return this.image;
    }

    /**
     * Sets the image URL of the product.
     *
     * @param image the image URL of the product
     */
    public void setImage(final String image) {
        this.image = image;
    }

    /**
     * Returns the description of the product.
     *
     * @return the description of the product
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Sets the description of the product.
     *
     * @param description the description of the product
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * Returns the quantity of the product.
     *
     * @return the quantity of the product
     */
    public int getQuantiteP() {
        return this.quantiteP;
    }

    /**
     * Sets the quantity of the product.
     *
     * @param quantiteP the quantity of the product
     */
    public void setQuantiteP(final int quantiteP) {
        this.quantiteP = quantiteP;
    }

    /**
     * Returns the category of the product.
     *
     * @return the category of the product
     */
    public Categorie_Produit getCategorie() {
        return this.categorieProduit;
    }

    /**
     * Sets the category of the product.
     *
     * @param categorieProduit the category of the product
     */
    public void setCategorie(final Categorie_Produit categorieProduit) {
        this.categorieProduit = categorieProduit;
    }

    /**
     * Returns a string representation of the `Produit` object.
     *
     * @return a string representation of the `Produit` object
     */
    @Override
    public String toString() {
        return "Produit{"
                + "id_produit=" + this.id_produit
                + ", nom='" + this.nom + '\''
                + ", prix='" + this.prix + '\''
                + ", image='" + this.image + '\''
                + ", description='" + this.description + '\''
                + ", categorie=" + this.categorieProduit
                + ", quantiteP=" + this.quantiteP
                + '}';
    }

    /**
     * Returns the category of the product.
     *
     * @return the category of the product
     */
    public Categorie_Produit getId_categorieProduit() {
        return this.categorieProduit;
    }

    /**
     * Returns the name of the category of the product.
     *
     * @return the name of the category of the product
     */
    public String getNom_categorie() {
        return this.categorieProduit.getNom_categorie();
    }
}
