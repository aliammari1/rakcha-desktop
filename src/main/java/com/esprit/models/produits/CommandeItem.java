package com.esprit.models.produits;
import java.util.Objects;
/**
 * Represents an item in a command.
 */
public class CommandeItem {
    private int idCommandeItem;
    private int quantity;
    private Produit produit;
    private Commande commande;
    /**
     * Default constructor.
     */
    public CommandeItem() {
    }
    /**
     * Parameterized constructor.
     * 
     * @param idCommandeItem The ID of the command item.
     * @param quantity       The quantity of the item.
     * @param produit        The product associated with the item.
     * @param commande       The command associated with the item.
     */
    public CommandeItem(int idCommandeItem, int quantity, Produit produit, Commande commande) {
        this.idCommandeItem = idCommandeItem;
        this.quantity = quantity;
        this.produit = produit;
        this.commande = commande;
    }
    /**
     * Parameterized constructor.
     * 
     * @param quantity The quantity of the item.
     * @param produit  The product associated with the item.
     * @param commande The command associated with the item.
     */
    public CommandeItem(int quantity, Produit produit, Commande commande) {
        this.quantity = quantity;
        this.produit = produit;
        this.commande = commande;
    }
    /**
     * Get the ID of the command item.
     * 
     * @return The ID of the command item.
     */
    public int getIdCommandeItem() {
        return idCommandeItem;
    }
    /**
     * Set the ID of the command item.
     * 
     * @param idCommandeItem The ID of the command item.
     */
    public void setIdCommandeItem(int idCommandeItem) {
        this.idCommandeItem = idCommandeItem;
    }
    /**
     * Get the quantity of the item.
     * 
     * @return The quantity of the item.
     */
    public int getQuantity() {
        return quantity;
    }
    /**
     * Set the quantity of the item.
     * 
     * @param quantity The quantity of the item.
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    /**
     * Get the product associated with the item.
     * 
     * @return The product associated with the item.
     */
    public Produit getProduit() {
        return produit;
    }
    /**
     * Set the product associated with the item.
     * 
     * @param produit The product associated with the item.
     */
    public void setProduit(Produit produit) {
        this.produit = produit;
    }
    /**
     * Get the command associated with the item.
     * 
     * @return The command associated with the item.
     */
    public Commande getCommande() {
        return commande;
    }
    /**
     * Set the command associated with the item.
     * 
     * @param commande The command associated with the item.
     */
    public void setCommande(Commande commande) {
        this.commande = commande;
    }
    /**
     * Returns a string representation of the CommandeItem object.
     * 
     * @return A string representation of the CommandeItem object.
     */
    @Override
    public String toString() {
        return "CommandeItem{" +
                "idCommandeItem=" + idCommandeItem +
                ", quantity=" + quantity +
                ", produit=" + produit +
                ", commande=" + commande +
                '}';
    }
}
