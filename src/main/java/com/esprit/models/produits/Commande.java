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

public class Commande {


    private int idCommande;

    private Date dateCommande;

    private String statu;

    private Client idClient;

    private List<CommandeItem> commandeItem=new ArrayList<>();

    private int num_telephone;

    private String adresse;



    public Commande() {
    }

    public Commande(Date dateCommande, String statu, Client idClient, int num_telephone, String adresse) {
        this.dateCommande = dateCommande;
        this.statu = statu;
        this.idClient = idClient;

        this.num_telephone = num_telephone;
        this.adresse = adresse;

    }

    public Commande(int idCommande, Date dateCommande, String statu, Client idClient, int num_telephone, String adresse) {
        this.idCommande = idCommande;
        this.dateCommande = dateCommande;
        this.statu = statu;
        this.idClient = idClient;
        this.num_telephone = num_telephone;
        this.adresse = adresse;

    }

    public int getIdCommande() {
        return idCommande;
    }

    public void setIdCommande(int idCommande) {
        this.idCommande = idCommande;
    }

    public Date getDateCommande() {
        return dateCommande;
    }

    public void setDateCommande(Date dateCommande) {
        this.dateCommande = dateCommande;
    }

    public String getStatu() {
        return statu;
    }

    public void setStatu(String statu) {
        this.statu = statu;
    }

    public Client getIdClient() {
        return idClient;
    }

    public void setIdClient(Client idClient) {
        this.idClient = idClient;
    }

    public List<CommandeItem> getCommandeItem() {
        return commandeItem;
    }

    public void setCommandeItem(List<CommandeItem> commandeItem) {
        this.commandeItem = commandeItem;
    }

    public int getNum_telephone() {
        return num_telephone;
    }

    public void setNum_telephone(int num_telephone) {
        this.num_telephone = num_telephone;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }


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
