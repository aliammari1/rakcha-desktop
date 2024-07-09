package com.esprit.models.produits;
import com.esprit.models.users.Client;
import com.esprit.models.users.User;
import com.itextpdf.text.pdf.qrcode.QRCodeWriter;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
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
     * @param dateCommande The date of the command.
     * @param statu        The status of the command.
     * @param idClient     The client associated with the command.
     * @param num_telephone The telephone number associated with the command.
     * @param adresse      The address associated with the command.
     */
    public Commande(Date dateCommande, String statu, Client idClient, int num_telephone, String adresse) {
        this.dateCommande = dateCommande;
        this.statu = statu;
        this.idClient = idClient;
        this.num_telephone = num_telephone;
        this.adresse = adresse;
    }
    /**
     * Parameterized constructor for the Commande class.
     *
     * @param idCommande   The ID of the command.
     * @param dateCommande The date of the command.
     * @param statu        The status of the command.
     * @param idClient     The client associated with the command.
     * @param num_telephone The telephone number associated with the command.
     * @param adresse      The address associated with the command.
     */
    public Commande(int idCommande, Date dateCommande, String statu, Client idClient, int num_telephone, String adresse) {
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
        return idCommande;
    }
    /**
     * Set the ID of the command.
     *
     * @param idCommande The ID of the command.
     */
    public void setIdCommande(int idCommande) {
        this.idCommande = idCommande;
    }
    /**
     * Get the date of the command.
     *
     * @return The date of the command.
     */
    public Date getDateCommande() {
        return dateCommande;
    }
    /**
     * Set the date of the command.
     *
     * @param dateCommande The date of the command.
     */
    public void setDateCommande(Date dateCommande) {
        this.dateCommande = dateCommande;
    }
    /**
     * Get the status of the command.
     *
     * @return The status of the command.
     */
    public String getStatu() {
        return statu;
    }
    /**
     * Set the status of the command.
     *
     * @param statu The status of the command.
     */
    public void setStatu(String statu) {
        this.statu = statu;
    }
    /**
     * Get the client associated with the command.
     *
     * @return The client associated with the command.
     */
    public Client getIdClient() {
        return idClient;
    }
    /**
     * Set the client associated with the command.
     *
     * @param idClient The client associated with the command.
     */
    public void setIdClient(Client idClient) {
        this.idClient = idClient;
    }
    /**
     * Get the list of items in the command.
     *
     * @return The list of items in the command.
     */
    public List<CommandeItem> getCommandeItem() {
        return commandeItem;
    }
    /**
     * Set the list of items in the command.
     *
     * @param commandeItem The list of items in the command.
     */
    public void setCommandeItem(List<CommandeItem> commandeItem) {
        this.commandeItem = commandeItem;
    }
    /**
     * Get the telephone number associated with the command.
     *
     * @return The telephone number associated with the command.
     */
    public int getNum_telephone() {
        return num_telephone;
    }
    /**
     * Set the telephone number associated with the command.
     *
     * @param num_telephone The telephone number associated with the command.
     */
    public void setNum_telephone(int num_telephone) {
        this.num_telephone = num_telephone;
    }
    /**
     * Get the address associated with the command.
     *
     * @return The address associated with the command.
     */
    public String getAdresse() {
        return adresse;
    }
    /**
     * Set the address associated with the command.
     *
     * @param adresse The address associated with the command.
     */
    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }
    /**
     * Get a string representation of the Commande object.
     *
     * @return A string representation of the Commande object.
     */
    @Override
    public String toString() {
        return "Commande{" +
                "idCommande=" + idCommande +
                ", dateCommande=" + dateCommande +
                ", statu='" + statu + '\'' +
                ", idClient=" + idClient +
                ", commandeItem=" + commandeItem +
                ", num_telephone=" + num_telephone +
                ", adresse='" + adresse + '\'' +
                '}';
    }
}
