package com.esprit.models;

public class Salle {

    private int id_salle;
    private int id_cinema;
    private int nb_places;
    private int nb_places_reserve;
    private int num_salle;
    private String statut;

    public Salle(int id_salle, int id_cinema, int nb_places, int nb_places_reserve, int num_salle, String statut) {
        this.id_salle = id_salle;
        this.id_cinema = id_cinema;
        this.nb_places = nb_places;
        this.nb_places_reserve = nb_places_reserve;
        this.num_salle = num_salle;
        this.statut = statut;
    }

    public Salle(int id_cinema, int nb_places, int nb_places_reserve, int num_salle, String statut) {
        this.id_cinema = id_cinema;
        this.nb_places = nb_places;
        this.nb_places_reserve = nb_places_reserve;
        this.num_salle = num_salle;
        this.statut = statut;
    }

    public int getId_salle() {
        return id_salle;
    }

    public void setId_salle(int id_salle) {
        this.id_salle = id_salle;
    }

    public int getId_cinema() {
        return id_cinema;
    }

    public void setId_cinema(int id_cinema) {
        this.id_cinema = id_cinema;
    }

    public int getNb_places() {
        return nb_places;
    }

    public void setNb_places(int nb_places) {
        this.nb_places = nb_places;
    }

    public int getNb_places_reserve() {
        return nb_places_reserve;
    }

    public void setNb_places_reserve(int nb_places_reserve) {
        this.nb_places_reserve = nb_places_reserve;
    }

    public int getNum_salle() {
        return num_salle;
    }

    public void setNum_salle(int num_salle) {
        this.num_salle = num_salle;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    @Override
    public String toString() {
        return "Salle{" +
                "id_salle=" + id_salle +
                ", id_cinema=" + id_cinema +
                ", nb_places=" + nb_places +
                ", nb_places_reserve=" + nb_places_reserve +
                ", num_salle=" + num_salle +
                ", statut='" + statut + '\'' +
                '}';
    }
}
