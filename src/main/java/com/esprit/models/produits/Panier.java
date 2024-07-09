package com.esprit.models.produits;
import com.esprit.models.users.User;
import java.util.Objects;
/**
 * Is used to represent a shopping cart containing various products and users. It has
 * several fields such as idpanier, quantity, produit, and users, which can be accessed
 * through getters and setters methods. The class also provides a toString() method
 * for creating a string representation of the panier object.
 */
public class Panier {
    private int idpanier;
    private int quantity;
    private Produit produit;
    private User users;
    /**
     * Default constructor for the Panier class.
     */
    public Panier() {
    }
    /**
     * Constructor for the Panier class with all parameters.
     *
     * @param idpanier The ID of the panier.
     * @param produit  The produit associated with the panier.
     * @param quantity The quantity of the produit in the panier.
     * @param users    The user associated with the panier.
     */
    public Panier(int idpanier, Produit produit, int quantity, User users) {
        this.idpanier = idpanier;
        this.quantity = quantity;
        this.produit = produit;
        this.users = users;
    }
    /**
     * Constructor for the Panier class without the ID parameter.
     *
     * @param produit  The produit associated with the panier.
     * @param quantity The quantity of the produit in the panier.
     * @param users    The user associated with the panier.
     */
    public Panier(Produit produit, int quantity, User users) {
        this.quantity = quantity;
        this.produit = produit;
        this.users = users;
    }
    /**
     * Get the ID of the panier.
     *
     * @return The ID of the panier.
     */
    public int getIdPanier() {
        return idpanier;
    }
    /**
     * Set the ID of the panier.
     *
     * @param idPanier The ID of the panier.
     */
    public void setIdPanier(int idPanier) {
        this.idpanier = idPanier;
    }
    /**
     * Get the quantity of the produit in the panier.
     *
     * @return The quantity of the produit in the panier.
     */
    public int getQuantity() {
        return quantity;
    }
    /**
     * Set the quantity of the produit in the panier.
     *
     * @param quantity The quantity of the produit in the panier.
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    /**
     * Get the produit associated with the panier.
     *
     * @return The produit associated with the panier.
     */
    public Produit getProduit() {
        return produit;
    }
    /**
     * Set the produit associated with the panier.
     *
     * @param produit The produit associated with the panier.
     */
    public void setProduit(Produit produit) {
        this.produit = produit;
    }
    /**
     * Get the user associated with the panier.
     *
     * @return The user associated with the panier.
     */
    public User getUser() {
        return users;
    }
    /**
     * Set the user associated with the panier.
     *
     * @param users The user associated with the panier.
     */
    public void setUser(User users) {
        this.users = users;
    }
    /**
     * Returns a string representation of the Panier object.
     *
     * @return A string representation of the Panier object.
     */
    @Override
    public String toString() {
        return "Panier{" +
                "idpanier=" + idpanier +
                ", quantity=" + quantity +
                ", produit=" + produit +
                ", users=" + users +
                '}';
    }
}
