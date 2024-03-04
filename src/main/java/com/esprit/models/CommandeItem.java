package com.esprit.models;

public class CommandeItem {

    private int idCommandeItem;
    private int quantity;

    private Produit produit;


   public CommandeItem(){

   }
    public CommandeItem(int idCommandeItem, Produit produit,int quantity) {
        this.idCommandeItem = idCommandeItem;
        this.quantity = quantity;
        this.produit = produit;
    }

    public CommandeItem(Produit produit,int quantity) {
        this.quantity = quantity;
        this.produit = produit;
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

    @Override
    public String toString() {
        return "CommandeItem{" +
                "idCommandeItem=" + idCommandeItem +
                ", quantity=" + quantity +
                ", produit=" + produit +
                '}';
    }
}
