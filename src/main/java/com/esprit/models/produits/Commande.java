package com.esprit.models.produits;

import com.esprit.models.users.Client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * The Commande class represents a command made by a client.
 */
public class Commande {
    private int idCommande;
    private Date dateCommande;
    private String statu;
    private Client idClient;
    private List<CommandeItem> commandeItem = new ArrayList<>();
    private int num_telephone;
    private String adresse;

    /**
     * Default constructor for the Commande class.
     */
    public Commande() {
    }

    /**
     * Parameterized constructor for the Commande class.
     *
     * @param dateCommande  The date of the command.
     * @param statu         The status of the command.
     * @param idClient      The client associated with the command.
     * @param num_telephone The telephone number associated with the command.
     * @param adresse       The address associated with the command.
     */
    public Commande(final Date dateCommande, final String statu, final Client idClient, final int num_telephone, final String adresse) {
        this.dateCommande = dateCommande;
        this.statu = statu;
        this.idClient = idClient;
        this.num_telephone = num_telephone;
        this.adresse = adresse;
    }

    /**
     * Parameterized constructor for the Commande class.
     *
     * @param idCommande    The ID of the command.
     * @param dateCommande  The date of the command.
     * @param statu         The status of the command.
     * @param idClient      The client associated with the command.
     * @param num_telephone The telephone number associated with the command.
     * @param adresse       The address associated with the command.
     */
    public Commande(final int idCommande, final Date dateCommande, final String statu, final Client idClient, final int num_telephone, final String adresse) {
        this.idCommande = idCommande;
        this.dateCommande = dateCommande;
        this.statu = statu;
        this.idClient = idClient;
        this.num_telephone = num_telephone;
        this.adresse = adresse;
    }

    /**
     * Get the ID of the command.
     *
     * @return The ID of the command.
     */
    public int getIdCommande() {
        return this.idCommande;
    }

    /**
     * Set the ID of the command.
     *
     * @param idCommande The ID of the command.
     */
    public void setIdCommande(final int idCommande) {
        this.idCommande = idCommande;
    }

    /**
     * Get the date of the command.
     *
     * @return The date of the command.
     */
    public Date getDateCommande() {
        return this.dateCommande;
    }

    /**
     * Set the date of the command.
     *
     * @param dateCommande The date of the command.
     */
    public void setDateCommande(final Date dateCommande) {
        this.dateCommande = dateCommande;
    }

    /**
     * Get the status of the command.
     *
     * @return The status of the command.
     */
    public String getStatu() {
        return this.statu;
    }

    /**
     * Set the status of the command.
     *
     * @param statu The status of the command.
     */
    public void setStatu(final String statu) {
        this.statu = statu;
    }

    /**
     * Get the client associated with the command.
     *
     * @return The client associated with the command.
     */
    public Client getIdClient() {
        return this.idClient;
    }

    /**
     * Set the client associated with the command.
     *
     * @param idClient The client associated with the command.
     */
    public void setIdClient(final Client idClient) {
        this.idClient = idClient;
    }

    /**
     * Get the list of items in the command.
     *
     * @return The list of items in the command.
     */
    public List<CommandeItem> getCommandeItem() {
        return this.commandeItem;
    }

    /**
     * Set the list of items in the command.
     *
     * @param commandeItem The list of items in the command.
     */
    public void setCommandeItem(final List<CommandeItem> commandeItem) {
        this.commandeItem = commandeItem;
    }

    /**
     * Get the telephone number associated with the command.
     *
     * @return The telephone number associated with the command.
     */
    public int getNum_telephone() {
        return this.num_telephone;
    }

    /**
     * Set the telephone number associated with the command.
     *
     * @param num_telephone The telephone number associated with the command.
     */
    public void setNum_telephone(final int num_telephone) {
        this.num_telephone = num_telephone;
    }

    /**
     * Get the address associated with the command.
     *
     * @return The address associated with the command.
     */
    public String getAdresse() {
        return this.adresse;
    }

    /**
     * Set the address associated with the command.
     *
     * @param adresse The address associated with the command.
     */
    public void setAdresse(final String adresse) {
        this.adresse = adresse;
    }

    /**
     * Get a string representation of the Commande object.
     *
     * @return A string representation of the Commande object.
     */
    @Override
    public String toString() {
        return "Commande{"
                + "idCommande=" + this.idCommande
                + ", dateCommande=" + this.dateCommande
                + ", statu='" + this.statu + '\''
                + ", idClient=" + this.idClient
                + ", commandeItem=" + this.commandeItem
                + ", num_telephone=" + this.num_telephone
                + ", adresse='" + this.adresse + '\''
                + '}';
    }
}
