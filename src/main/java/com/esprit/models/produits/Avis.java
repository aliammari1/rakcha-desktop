package com.esprit.models.produits;
import com.esprit.models.users.Client;
/**
 * The Avis class represents a user's review of a product.
 */
public class Avis {
    private Client user;
    private int note;
    private Produit produit;
    public Avis() {
    }
    /**
     * Constructs a new Avis object with the specified user, note, and produit.
     *
     * @param user    the client who gave the avis
     * @param note    the note given by the client
     * @param produit the produit associated with the avis
     */
    public Avis(Client user, int note, Produit produit) {
        this.user = user;
        this.note = note;
        this.produit = produit;
    }
    /**
     * Returns the client who gave the avis.
     *
     * @return the client who gave the avis
     */
    public Client getUser() {
        return user;
    }
    /**
     * Sets the client who gave the avis.
     *
     * @param user the client who gave the avis
     */
    public void setUser(Client user) {
        this.user = user;
    }
    /**
     * Returns the note given by the client.
     *
     * @return the note given by the client
     */
    public int getNote() {
        return note;
    }
    /**
     * Sets the note given by the client.
     *
     * @param note the note given by the client
     */
    public void setNote(int note) {
        this.note = note;
    }
    /**
     * Returns the produit associated with the avis.
     *
     * @return the produit associated with the avis
     */
    public Produit getProduit() {
        return produit;
    }
    /**
     * Sets the produit associated with the avis.
     *
     * @param produit the produit associated with the avis
     */
    public void setProduit(Produit produit) {
        this.produit = produit;
    }
    @Override
    public String toString() {
        return "Avis{" +
                "user=" + user +
                ", note=" + note +
                ", produit=" + produit +
                '}';
    }
}
