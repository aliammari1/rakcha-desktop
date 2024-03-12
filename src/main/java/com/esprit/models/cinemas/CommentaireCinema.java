package com.esprit.models.cinemas;

import com.esprit.models.users.Client;

public class CommentaireCinema {
    private int idcommentaire;

    private Cinema cinema;

    private Client client;

    private String commentaire;

    private String sentiment;

    public CommentaireCinema(int idcommentaire, Cinema cinema, Client client, String commentaire, String sentiment) {
        this.idcommentaire = idcommentaire;
        this.cinema = cinema;
        this.client = client;
        this.commentaire = commentaire;
        this.sentiment = sentiment;
    }

    public CommentaireCinema(Cinema cinema, Client client, String commentaire, String sentiment) {
        this.cinema = cinema;
        this.client = client;
        this.commentaire = commentaire;
        this.sentiment = sentiment;
    }

    public int getIdcommentaire() {
        return idcommentaire;
    }

    public void setIdcommentaire(int idcommentaire) {
        this.idcommentaire = idcommentaire;
    }

    public Cinema getCinema() {
        return cinema;
    }

    public void setCinema(Cinema cinema) {
        this.cinema = cinema;
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
                ", cinema=" + cinema +
                ", client=" + client +
                ", commentaire='" + commentaire + '\'' +
                ", sentiment='" + sentiment + '\'' +
                '}';
    }
}
