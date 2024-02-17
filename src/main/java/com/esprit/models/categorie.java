package com.esprit.models;

public class categorie {
    public int idcategorie;

    public String nom;

    public String description;


    public categorie() {
    }

    public categorie(int idcategorie, String nom, String description) {
        this.idcategorie = idcategorie;
        this.nom = nom;
        this.description = description;
    }
    public categorie(String nom, String description) {
        this.nom = nom;
        this.description = description;
    }

    public int getIdcategorie() {
        return idcategorie;
    }

    public String getNom() {
        return nom;
    }

    public String getDescription() {
        return description;
    }
    public void setIdcategorie(int idcategorie) {
        this.idcategorie = idcategorie;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setDescription(String description) {
        this.description = description;
    }





}
