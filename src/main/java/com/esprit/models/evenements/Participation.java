package com.esprit.models.evenements;

import com.esprit.models.users.Client;

public class Participation {

    private int id_participation;
    private Evenement event;

    private Client userevenement;
    private int quantity;

    public Participation(int id_participation, Evenement event, Client userevenement, int quantity) {
        this.id_participation = id_participation;
        this.event = event;
        this.userevenement = userevenement;
        this.quantity = quantity;
    }

    public Participation(Evenement event, Client userevenement, int quantity) {
        this.event = event;
        this.userevenement = userevenement;
        this.quantity = quantity;
    }

    public Participation(Evenement event) {
        this.event = event;
    }

    public Participation() {
    }

    public int getId_participation() {
        return id_participation;
    }

    public void setId_participation(int id_participation) {
        this.id_participation = id_participation;
    }

    public Evenement getEvent() {
        return event;
    }

    public void setEvent(Evenement event) {
        this.event = event;
    }

    public Client getUserevenement() {
        return userevenement;
    }

    public void setUserevenement(Client userevenement) {
        this.userevenement = userevenement;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "Participation{" +
                "id_participation=" + id_participation +
                ", event=" + event +
                ", userevenement=" + userevenement +
                ", quantity=" + quantity +
                '}';
    }
}
