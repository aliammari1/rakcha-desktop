package com.esprit.models.produits;

import com.esprit.models.users.Client;

import java.util.Date;

public class Commentaire {


    private int idcommentaire;

    private Client client;

    private String commentaire;

    private Produit produit;
    private Date datecommantaire;


    public Commentaire() {
    }

    public Commentaire(Client client, String commentaire, Produit produit) {

        this.client = client;
        this.commentaire = commentaire;
        this.produit = produit;

    }


    public Commentaire(int idcommentaire, Client client, String commentaire, Produit produit, Date datecommantaire) {
        this.idcommentaire = idcommentaire;
        this.client = client;
        this.commentaire = commentaire;
        this.produit = produit;
        this.datecommantaire = datecommantaire;
    }

    public Commentaire(Client client, String commentaire, Produit produit, Date datecommantaire) {
        this.client = client;
        this.commentaire = commentaire;
        this.produit = produit;
        this.datecommantaire = datecommantaire;
    }

    public int getIdCommentaire() {
        return idcommentaire;
    }

    public void setIdCommentaire(int idCommentaire) {
        this.idcommentaire = idCommentaire;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public Produit getProduit() {
        return produit;
    }

    public void setProduit(Produit produit) {
        this.produit = produit;
    }

    public Date getDatecommentaire() {
        return datecommantaire;
    }

    public void setDatecommentaire(Date datecommantaire) {
        this.datecommantaire = datecommantaire;
    }

    @Override
    public String toString() {
        return "Commentaire{" +
                "idcommentaire=" + idcommentaire +
                ", client=" + client +
                ", commentaire='" + commentaire + '\'' +
                ", produit=" + produit +
                ", datecommantaire=" + datecommantaire +
                '}';
    }
}
