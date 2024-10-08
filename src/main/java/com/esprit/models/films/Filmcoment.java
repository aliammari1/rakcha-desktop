package com.esprit.models.films;

import com.esprit.models.users.Client;

/**
 * Represents a film comment.
 */
public class Filmcoment {
    private int id;
    private String comment;
    private Client user_id;
    private Film film_id;

    /**
     * Constructs a Filmcoment object with the specified id, comment, user_id, and film_id.
     *
     * @param id      the id of the comment
     * @param comment the comment text
     * @param user_id the user who made the comment
     * @param film_id the film to which the comment belongs
     */
    public Filmcoment(final int id, final String comment, final Client user_id, final Film film_id) {
        this.id = id;
        this.comment = comment;
        this.user_id = user_id;
        this.film_id = film_id;
    }

    /**
     * Constructs a Filmcoment object with the specified comment, user_id, and film_id.
     *
     * @param comment the comment text
     * @param user_id the user who made the comment
     * @param film_id the film to which the comment belongs
     */
    public Filmcoment(final String comment, final Client user_id, final Film film_id) {
        this.comment = comment;
        this.user_id = user_id;
        this.film_id = film_id;
    }

    /**
     * Returns the id of the comment.
     *
     * @return the id of the comment
     */
    public int getId() {
        return this.id;
    }

    /**
     * Sets the id of the comment.
     *
     * @param id the id of the comment
     */
    public void setId(final int id) {
        this.id = id;
    }

    /**
     * Returns the comment text.
     *
     * @return the comment text
     */
    public String getComment() {
        return this.comment;
    }

    /**
     * Sets the comment text.
     *
     * @param comment the comment text
     */
    public void setComment(final String comment) {
        this.comment = comment;
    }

    /**
     * Returns the user who made the comment.
     *
     * @return the user who made the comment
     */
    public Client getUser_id() {
        return this.user_id;
    }

    /**
     * Sets the user who made the comment.
     *
     * @param user_id the user who made the comment
     */
    public void setUser_id(final Client user_id) {
        this.user_id = user_id;
    }

    /**
     * Returns the film to which the comment belongs.
     *
     * @return the film to which the comment belongs
     */
    public Film getFilm_id() {
        return this.film_id;
    }

    /**
     * Sets the film to which the comment belongs.
     *
     * @param film_id the film to which the comment belongs
     */
    public void setFilm_id(final Film film_id) {
        this.film_id = film_id;
    }

    /**
     * Returns a string representation of the Filmcoment object.
     *
     * @return a string representation of the Filmcoment object
     */
    @Override
    public String toString() {
        return "Filmcoment{"
                + "id=" + this.id
                + ", comment='" + this.comment + '\''
                + ", user_id=" + this.user_id
                + ", film_id=" + this.film_id
                + '}';
    }
}
