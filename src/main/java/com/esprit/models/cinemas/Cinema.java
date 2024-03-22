package com.esprit.models.cinemas;

import com.esprit.models.users.Responsable_de_cinema;

public class Cinema {

    private final Responsable_de_cinema responsable;
    private int id_cinema;
    private String nom;
    private String adresse;
    private String logo;
    private String Statut;

    public Cinema(int id_cinema, String nom, String adresse, Responsable_de_cinema responsable, String logo, String statut) {
        this.id_cinema = id_cinema;
        this.nom = nom;
        this.adresse = adresse;
        this.responsable = responsable;
        this.logo = logo;
        Statut = statut;
    }

    public Cinema(String nom, String adresse, Responsable_de_cinema responsable, String logo, String statut) {
        this.nom = nom;
        this.adresse = adresse;
        this.responsable = responsable;
        this.logo = logo;
        Statut = statut;
    }

    public int getId_cinema() {
        return id_cinema;
    }

    public void setId_cinema(int id_cinema) {
        this.id_cinema = id_cinema;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public Responsable_de_cinema getResponsable() {
        return responsable;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getStatut() {
        return Statut;
    }

    public void setStatut(String statut) {
        Statut = statut;
    }

    @Override
    public String toString() {
        return "Cinema{" +
                "id_cinema=" + id_cinema +
                ", nom='" + nom + '\'' +
                ", adresse='" + adresse + '\'' +
                ", responsable='" + responsable + '\'' +
                ", logo=" + logo +
                ", Statut='" + Statut + '\'' +
                '}';
    }
}
