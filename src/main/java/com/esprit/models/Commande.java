package com.esprit.models;

import java.util.Date;

public class Commande {


    private int idCommande;

    private Date dateCommande;

    private String etatCommande;

    private Users idClient;

    private CommandeItem commandeItem;

    public Commande() {
    }

    public Commande(int idCommande, Date dateCommande, String etatCommande, Users idClient, CommandeItem commandeItem) {
        this.idCommande = idCommande;
        this.dateCommande = dateCommande;
        this.etatCommande = etatCommande;
        this.idClient = idClient;
        this.commandeItem = commandeItem;
    }

    public Commande(Date dateCommande, String etatCommande, Users idClient, CommandeItem commandeItem) {
        this.dateCommande = dateCommande;
        this.etatCommande = etatCommande;
        this.idClient = idClient;
        this.commandeItem = commandeItem;
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

    public String getEtatCommande() {
        return etatCommande;
    }

    public void setEtatCommande(String etatCommande) {
        this.etatCommande = etatCommande;
    }

    public Users getIdClient() {
        return idClient;
    }

    public void setIdClient(Users idClient) {
        this.idClient = idClient;
    }

    public CommandeItem getCommandeItem() {
        return commandeItem;
    }

    public void setCommandeItem(CommandeItem commandeItem) {
        this.commandeItem = commandeItem;
    }

    @Override
    public String toString() {
        return "Commande{" +
                "idCommande=" + idCommande +
                ", dateCommande=" + dateCommande +
                ", etatCommande='" + etatCommande + '\'' +
                ", idClient=" + idClient +
                ", commandeItem=" + commandeItem +
                '}';
    }
}
