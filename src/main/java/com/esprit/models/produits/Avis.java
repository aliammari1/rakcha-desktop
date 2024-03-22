package com.esprit.models.produits;

import com.esprit.models.users.Client;

public class Avis {


    private Client user;
    private int note;


    private Produit produit;

    public Avis() {
    }



    public Avis(Client user, int note, Produit produit) {
        this.user = user;
        this.note = note;
        this.produit = produit;
    }



    public Client getUser() {
        return user;
    }

    public void setUser(Client user) {
        this.user = user;
    }

    public int getNote() {
        return note;
    }

    public void setNote(int note) {
        this.note = note;
    }


    public Produit getProduit() {
        return produit;
    }

    public void setProduit(Produit produit) {
        this.produit = produit;
    }

    @Override
    public String toString() {
        return "Avis{" +
                ", user=" + user +
                ", note=" + note +
                ", produit=" + produit +
                '}';
    }
}
