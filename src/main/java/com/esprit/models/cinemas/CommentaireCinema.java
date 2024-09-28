package com.esprit.models.cinemas;

import com.esprit.models.users.Client;

/**
 * Represents a comment on a cinema.
 */
public class CommentaireCinema {
    private int idcommentaire;
    private Cinema cinema;
    private Client client;
    private String commentaire;
    private String sentiment;

    /**
     * Constructs a CommentaireCinema object with the specified parameters.
     *
     * @param idcommentaire The ID of the comment.
     * @param cinema        The cinema associated with the comment.
     * @param client        The client who made the comment.
     * @param commentaire   The comment text.
     * @param sentiment     The sentiment of the comment.
     */
    public CommentaireCinema(final int idcommentaire, final Cinema cinema, final Client client, final String commentaire, final String sentiment) {
        this.idcommentaire = idcommentaire;
        this.cinema = cinema;
        this.client = client;
        this.commentaire = commentaire;
        this.sentiment = sentiment;
    }

    /**
     * Constructs a CommentaireCinema object with the specified parameters.
     *
     * @param cinema      The cinema associated with the comment.
     * @param client      The client who made the comment.
     * @param commentaire The comment text.
     * @param sentiment   The sentiment of the comment.
     */
    public CommentaireCinema(final Cinema cinema, final Client client, final String commentaire, final String sentiment) {
        this.cinema = cinema;
        this.client = client;
        this.commentaire = commentaire;
        this.sentiment = sentiment;
    }

    /**
     * Returns the ID of the comment.
     *
     * @return The ID of the comment.
     */
    public int getIdcommentaire() {
        return this.idcommentaire;
    }

    /**
     * Sets the ID of the comment.
     *
     * @param idcommentaire The ID of the comment.
     */
    public void setIdcommentaire(final int idcommentaire) {
        this.idcommentaire = idcommentaire;
    }

    /**
     * Returns the cinema associated with the comment.
     *
     * @return The cinema associated with the comment.
     */
    public Cinema getCinema() {
        return this.cinema;
    }

    /**
     * Sets the cinema associated with the comment.
     *
     * @param cinema The cinema associated with the comment.
     */
    public void setCinema(final Cinema cinema) {
        this.cinema = cinema;
    }

    /**
     * Returns the client who made the comment.
     *
     * @return The client who made the comment.
     */
    public Client getClient() {
        return this.client;
    }

    /**
     * Sets the client who made the comment.
     *
     * @param client The client who made the comment.
     */
    public void setClient(final Client client) {
        this.client = client;
    }

    /**
     * Returns the comment text.
     *
     * @return The comment text.
     */
    public String getCommentaire() {
        return this.commentaire;
    }

    /**
     * Sets the comment text.
     *
     * @param commentaire The comment text.
     */
    public void setCommentaire(final String commentaire) {
        this.commentaire = commentaire;
    }

    /**
     * Returns the sentiment of the comment.
     *
     * @return The sentiment of the comment.
     */
    public String getSentiment() {
        return this.sentiment;
    }

    /**
     * Sets the sentiment of the comment.
     *
     * @param sentiment The sentiment of the comment.
     */
    public void setSentiment(final String sentiment) {
        this.sentiment = sentiment;
    }

    /**
     * Returns a string representation of the CommentaireCinema object.
     *
     * @return A string representation of the CommentaireCinema object.
     */
    @Override
    public String toString() {
        return "CommentaireCinema{"
                + "idcommentaire=" + this.idcommentaire
                + ", cinema=" + this.cinema
                + ", client=" + this.client
                + ", commentaire='" + this.commentaire + '\''
                + ", sentiment='" + this.sentiment + '\''
                + '}';
    }
}
