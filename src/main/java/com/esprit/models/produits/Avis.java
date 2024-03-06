package com.esprit.models.produits;

import com.esprit.models.users.Client;

public class Avis {


    private Client user;
    private int note;
    private String avis;

    private Produit produit;

    public Avis() {
    }



    public Avis(Client user, int note, String avis, Produit produit) {
        this.user = user;
        this.note = note;
        this.avis = avis;
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

    public String getAvis() {
        return avis;
    }

    public void setAvis(String avis) {
        this.avis = avis;
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
                ", avis='" + avis + '\'' +
                ", produit=" + produit +
                '}';
    }
}
