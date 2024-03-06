package com.esprit.models.produits;

import java.util.Objects;

public class CommandeItem {

    private int idCommandeItem;
    private int quantity;

    private Produit produit;

    private Commande commande;




   public CommandeItem(){

   }

    public CommandeItem(int idCommandeItem, int quantity, Produit produit, Commande commande) {
        this.idCommandeItem = idCommandeItem;
        this.quantity = quantity;
        this.produit = produit;
        this.commande = commande;
    }

    public CommandeItem(int quantity, Produit produit, Commande commande) {
        this.quantity = quantity;
        this.produit = produit;
        this.commande = commande;
    }

    public int getIdCommandeItem() {
        return idCommandeItem;
    }

    public void setIdCommandeItem(int idCommandeItem) {
        this.idCommandeItem = idCommandeItem;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Produit getProduit() {
        return produit;
    }

    public void setProduit(Produit produit) {
        this.produit = produit;
    }

    public Commande getCommande() {
        return commande;
    }

    public void setCommande(Commande commande) {
        this.commande = commande;
    }

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
