package com.esprit.models.cinemas;

public class Salle {

    private int id_salle;
    private int id_cinema;
    private int nb_places;
    private String nom_salle;

    public Salle(int id_salle, int id_cinema, int nb_places, String nom_salle) {
        this.id_salle = id_salle;
        this.id_cinema = id_cinema;
        this.nb_places = nb_places;
        this.nom_salle = nom_salle;
    }

    public Salle(int id_cinema, int nb_places, String nom_salle) {
        this.id_cinema = id_cinema;
        this.nb_places = nb_places;
        this.nom_salle = nom_salle;
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

    public String getNom_salle() {
        return nom_salle;
    }

    public void setNom_salle(String nom_salle) {
        this.nom_salle = nom_salle;
    }

    @Override
    public String toString() {
        return "Salle{" +
                "id_salle=" + id_salle +
                ", id_cinema=" + id_cinema +
                ", nb_places=" + nb_places +
                ", nom_salle=" + nom_salle +
                '}';
    }
}
