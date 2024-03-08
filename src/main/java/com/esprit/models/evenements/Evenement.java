package com.esprit.models.evenements;

import java.sql.Date;

public class Evenement {

    private int id;
    private String nom;
    private Date dateDebut;
    private Date dateFin;
    private String lieu;

    private Categorie_evenement categorie;

    private String etat;

    private String description;


    public Evenement(int id, String nom, Date dateDebut, Date dateFin, String lieu, Categorie_evenement categorie, String etat, String description) {
        this.id = id;
        this.nom = nom;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.lieu = lieu;
        this.categorie = categorie;
        this.etat = etat;
        this.description = description;


    }

    public Evenement(String nom, Date dateDebut, Date dateFin, String lieu, Categorie_evenement categorie, String etat, String description) {
        this.nom = nom;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.lieu = lieu;
        this.categorie = categorie;
        this.etat = etat;
        this.description = description;


    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Date getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(Date dateDebut) {
        this.dateDebut = dateDebut;
    }

    public Date getDateFin() {
        return dateFin;
    }

    public void setDateFin(Date dateFin) {
        this.dateFin = dateFin;
    }

    public String getLieu() {
        return lieu;
    }

    public void setLieu(String lieu) {
        this.lieu = lieu;
    }

    public Categorie_evenement getCategorie() {
        return categorie;
    }

    public void setCategorie(Categorie_evenement categorie) {
        this.categorie = categorie;
    }

    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Evenement{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", dateDebut=" + dateDebut +
                ", dateFin=" + dateFin +
                ", lieu='" + lieu + '\'' +
                ", categorie=" + categorie +
                ", etat='" + etat + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    public Categorie_evenement getId_categorieEvenement() {
        return categorie;
    }

    public String getNom_categorieEvenement() {
        return categorie.getNom_categorie();
    }
}
