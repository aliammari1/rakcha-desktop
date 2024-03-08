package com.esprit.models.films;

import com.esprit.models.cinemas.Seance;
import com.esprit.models.users.Client;

public class Ticket {

    private int nbrdeplace;
    private Client id_user;
    private Seance id_seance;
    private float prix;

    public Ticket() {

    }

    public Ticket(int nbrdeplace, Client id_user, Seance id_seance, float prix) {
        this.nbrdeplace = nbrdeplace;
        this.id_user = id_user;
        this.id_seance = id_seance;
        this.prix = prix;
    }

    public int getNbrdeplace() {
        return nbrdeplace;
    }

    public void setNbrdeplace(int nbrdeplace) {
        this.nbrdeplace = nbrdeplace;
    }

    public Client getId_user() {
        return id_user;
    }

    public void setId_user(Client id_user) {
        this.id_user = id_user;
    }

    public Seance getId_seance() {
        return id_seance;
    }

    public void setId_seance(Seance id_seance) {
        this.id_seance = id_seance;
    }

    public float getPrix() {
        return prix;
    }

    public void setPrix(float prix) {
        this.prix = prix;
    }

    @Override
    public String toString() {
        return "TicketService{" +
                "nbrdeplace=" + nbrdeplace +
                ", id_user=" + id_user +
                ", id_seance=" + id_seance +
                ", prix=" + prix +
                '}';
    }
}


