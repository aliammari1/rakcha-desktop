package com.esprit.models.produits;

import com.esprit.models.users.Client;

import java.util.Date;

public class Commentaire {


    private int idcommentaire;

    private Client client;

    private String commentaire;




   public Commentaire(){}
    public Commentaire(int idcommentaire, Client client, String commentaire) {
        this.idcommentaire = idcommentaire;
        this.client = client;
        this.commentaire = commentaire;
    }

    public Commentaire(Client client, String commentaire) {
        this.client = client;
        this.commentaire = commentaire;
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

    @Override
    public String toString() {
        return "Commentaire{" +
                "idCommentaire=" + idcommentaire +
                ", Client=" + client +
                ", commentaire='" + commentaire + '\'' +
                '}';
    }
}