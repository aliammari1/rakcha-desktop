package com.esprit.models;

public class Cinema {

    private int id_cinema;
    private String nom;
    private String adresse;
    private String responsable;
    private String image;

    public Cinema(int id_cinema, String nom, String adresse, String responsable, String image) {
        this.id_cinema = id_cinema;
        this.nom = nom;
        this.adresse = adresse;
        this.responsable = responsable;
        this.image = image;
    }

    public Cinema(String nom, String adresse, String responsable, String image) {
        this.nom = nom;
        this.adresse = adresse;
        this.responsable = responsable;
        this.image = image;
    }

    public int getId_cinema() {
        return id_cinema;
    }

    public String getNom() {
        return nom;
    }

    public String getAdresse() {
        return adresse;
    }

    public String getResponsable() {
        return responsable;
    }

    public String getImage() {
        return image;
    }

    public void setId_cinema(int id_cinema) {
        this.id_cinema = id_cinema;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public void setResponsable(String responsable) {
        this.responsable = responsable;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "Cinema{" +
                "id_cinema=" + id_cinema +
                ", nom='" + nom + '\'' +
                ", adresse='" + adresse + '\'' +
                ", responsable='" + responsable + '\'' +
                ", image='" + image + '\'' +
                '}';
    }
}
