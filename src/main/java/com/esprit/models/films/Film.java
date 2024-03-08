package com.esprit.models.films;

import java.sql.Time;

public class Film {
    private
    String nom;
    private String categoryNom;
    private int id;
    private String image;
    private Time duree;
    private String description;
    private int annederalisation;


    // Existing constructor
    public Film(final Film f) {
        this.id = f.id;
        this.nom = f.nom;
        this.image = f.image;
        this.duree = f.duree;
        this.description = f.description;
        this.annederalisation = f.annederalisation;
    }

    public Film(int id, String nom, String image, Time duree, String description, int annederalisation) {
        this.id = id;
        this.nom = nom;
        this.image = image;
        this.duree = duree;
        this.description = description;
        this.annederalisation = annederalisation;

    }

    public Film(String nom, String image, Time duree, String description, int annederalisation) {
        this.nom = nom;
        this.image = image;
        this.duree = duree;
        this.description = description;
        this.annederalisation = annederalisation;
    }


    public Film(int id) {
        this.id = id;
        this.nom = null;
        this.image = null;
        this.duree = null;
        this.description = null;
        this.annederalisation = 0;
    }


    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Time getDuree() {
        return duree;
    }

    public void setDuree(Time duree) {
        this.duree = duree;
    }

    public void setCategoryNom(String categoryNom) {
        this.categoryNom = categoryNom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getAnnederalisation() {
        return annederalisation;
    }

    public void setAnnederalisation(int annederalisation) {
        this.annederalisation = annederalisation;
    }


    @Override
    public String toString() {
        return "Film{" +
                "nom='" + nom + '\'' +
                ", id=" + id +
                ", image=" + image +
                ", duree=" + duree +
                ", description='" + description + '\'' +
                ", annederalisation=" + annederalisation +
                '}';
    }


}
