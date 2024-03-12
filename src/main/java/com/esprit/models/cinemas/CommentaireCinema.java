package com.esprit.models.cinemas;

import com.esprit.models.users.Client;

public class CommentaireCinema {
    private int idcommentaire;

    private Client client;

    private String commentaire;

    private String sentiment;




    public CommentaireCinema(){}
    public CommentaireCinema(int idcommentaire, Client client, String commentaire, String sentiment) {
        this.idcommentaire = idcommentaire;
        this.client = client;
        this.commentaire = commentaire;
        this.sentiment = sentiment;
    }

    public CommentaireCinema(Client client, String commentaire, String sentiment) {
        this.client = client;
        this.commentaire = commentaire;
        this.sentiment =sentiment;
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

    public String getSentiment() {
        return sentiment;
    }

    public void setSentiment(String sentiment) {
        this.sentiment = sentiment;
    }

    @Override
    public String toString() {
        return "CommentaireCinema{" +
                "idcommentaire=" + idcommentaire +
                ", client=" + client +
                ", commentaire='" + commentaire + '\'' +
                ", sentiment='" + sentiment + '\'' +
                '}';
    }
}
