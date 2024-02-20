package com.esprit.models;

import java.sql.Blob;

public class Cinema {

    private int id_cinema;
    private String nom;
    private String adresse;
    private String responsable;
    private Blob logo;
    private String Statut;

    public Cinema(int id_cinema, String nom, String adresse, String responsable, Blob logo, String statut) {
        this.id_cinema = id_cinema;
        this.nom = nom;
        this.adresse = adresse;
        this.responsable = responsable;
        this.logo = logo;
        Statut = statut;
    }

    public Cinema(String nom, String adresse, String responsable, Blob logo, String statut) {
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

    public String getResponsable() {
        return responsable;
    }

    public void setResponsable(String responsable) {
        this.responsable = responsable;
    }

    public Blob getLogo() {
        return logo;
    }

    public void setLogo(Blob logo) {
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
