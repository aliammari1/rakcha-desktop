package com.esprit.models.films;
import com.esprit.models.cinemas.Seance;
import com.esprit.models.users.Client;
/**
 * The Ticket class represents a ticket for a movie session.
 */
public class Ticket {
    private int nbrdeplace;
    private Client id_user;
    private Seance id_seance;
    private float prix;
    /**
     * Default constructor for the Ticket class.
     */
    public Ticket() {
    }
    /**
     * Constructor for the Ticket class.
     *
     * @param nbrdeplace The number of seats for the ticket.
     * @param id_user    The client associated with the ticket.
     * @param id_seance  The movie session associated with the ticket.
     * @param prix       The price of the ticket.
     */
    public Ticket(int nbrdeplace, Client id_user, Seance id_seance, float prix) {
        this.nbrdeplace = nbrdeplace;
        this.id_user = id_user;
        this.id_seance = id_seance;
        this.prix = prix;
    }
    /**
     * Get the number of seats for the ticket.
     *
     * @return The number of seats.
     */
    public int getNbrdeplace() {
        return nbrdeplace;
    }
    /**
     * Set the number of seats for the ticket.
     *
     * @param nbrdeplace The number of seats.
     */
    public void setNbrdeplace(int nbrdeplace) {
        this.nbrdeplace = nbrdeplace;
    }
    /**
     * Get the client associated with the ticket.
     *
     * @return The client.
     */
    public Client getId_user() {
        return id_user;
    }
    /**
     * Set the client associated with the ticket.
     *
     * @param id_user The client.
     */
    public void setId_user(Client id_user) {
        this.id_user = id_user;
    }
    /**
     * Get the movie session associated with the ticket.
     *
     * @return The movie session.
     */
    public Seance getId_seance() {
        return id_seance;
    }
    /**
     * Set the movie session associated with the ticket.
     *
     * @param id_seance The movie session.
     */
    public void setId_seance(Seance id_seance) {
        this.id_seance = id_seance;
    }
    /**
     * Get the price of the ticket.
     *
     * @return The price.
     */
    public float getPrix() {
        return prix;
    }
    /**
     * Set the price of the ticket.
     *
     * @param prix The price.
     */
    public void setPrix(float prix) {
        this.prix = prix;
    }
    /**
     * Get a string representation of the Ticket object.
     *
     * @return A string representation of the Ticket.
     */
    @Override
    public String toString() {
        return "Ticket{" +
                "nbrdeplace=" + nbrdeplace +
                ", id_user=" + id_user +
                ", id_seance=" + id_seance +
                ", prix=" + prix +
                '}';
    }
}
