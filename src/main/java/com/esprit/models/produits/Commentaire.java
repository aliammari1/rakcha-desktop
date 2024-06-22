package com.esprit.models.produits;
import com.esprit.models.users.Client;
import java.util.Date;
/**
 * The Commentaire class represents a comment made by a client on a product.
 */
public class Commentaire {
    private int idcommentaire;
    private Client client;
    private String commentaire;
    private Produit produit;
    /**
     * Default constructor for Commentaire class.
     */
    public Commentaire() {
    }
    /**
     * Parameterized constructor for Commentaire class.
     * 
     * @param client      The client who made the comment.
     * @param commentaire The comment text.
     * @param produit     The product associated with the comment.
     */
    public Commentaire(Client client, String commentaire, Produit produit) {
        this.client = client;
        this.commentaire = commentaire;
        this.produit = produit;
    }
    /**
     * Parameterized constructor for Commentaire class.
     * 
     * @param idcommentaire The ID of the comment.
     * @param client        The client who made the comment.
     * @param commentaire   The comment text.
     * @param produit       The product associated with the comment.
     */
    public Commentaire(int idcommentaire, Client client, String commentaire, Produit produit) {
        this.idcommentaire = idcommentaire;
        this.client = client;
        this.commentaire = commentaire;
        this.produit = produit;
    }
    /**
     * Get the ID of the comment.
     * 
     * @return The ID of the comment.
     */
    public int getIdCommentaire() {
        return idcommentaire;
    }
    /**
     * Set the ID of the comment.
     * 
     * @param idCommentaire The ID of the comment.
     */
    public void setIdCommentaire(int idCommentaire) {
        this.idcommentaire = idCommentaire;
    }
    /**
     * Get the client who made the comment.
     * 
     * @return The client who made the comment.
     */
    public Client getClient() {
        return client;
    }
    /**
     * Set the client who made the comment.
     * 
     * @param client The client who made the comment.
     */
    public void setClient(Client client) {
        this.client = client;
    }
    /**
     * Get the comment text.
     * 
     * @return The comment text.
     */
    public String getCommentaire() {
        return commentaire;
    }
    /**
     * Set the comment text.
     * 
     * @param commentaire The comment text.
     */
    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }
    /**
     * Get the product associated with the comment.
     * 
     * @return The product associated with the comment.
     */
    public Produit getProduit() {
        return produit;
    }
    /**
     * Set the product associated with the comment.
     * 
     * @param produit The product associated with the comment.
     */
    public void setProduit(Produit produit) {
        this.produit = produit;
    }
    /**
     * Returns a string representation of the Commentaire object.
     * 
     * @return A string representation of the Commentaire object.
     */
    @Override
    public String toString() {
        return "Commentaire{" +
                "idcommentaire=" + idcommentaire +
                ", client=" + client +
                ", commentaire='" + commentaire + '\'' +
                ", produit=" + produit +
                '}';
    }
}
